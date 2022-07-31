package cga.exercise.game

import cga.exercise.components.Logic.CellField
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.lights.PointLight
import cga.exercise.components.lights.PointLightComposite
import cga.exercise.components.lights.Spotlight
import cga.exercise.components.lights.SpotlightComposite
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram     //Shader
    private val shadowShader: ShaderProgram

    var sphereMatrix: Matrix4f = Matrix4f()     //Matrix Weltposition
    var groundMatrix: Matrix4f = Matrix4f()     //Matrix Weltposition

    //Aufgabe 2.1
    //var sphereMashes = loadMeshes("assets\\models\\sphere.obj") //gibt meshes zurück
    //var groundMashes = loadMeshes("assets\\models\\ground.obj")

    var sphere = ModelLoader.loadModel("assets/models/sphere.obj",0f,0f,0f)


    //var motorcycleRenderable = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90.0f),Math.toRadians(90.0f),0.0f)

     var field = CellField();

    var texDiff = Texture2D("assets/textures/ground_diff.png", true)
    var texEmit = Texture2D("assets/textures/ground_emit.png", true)
    var texSpec = Texture2D("assets/textures/ground_spec.png", true)


    var groundMaterial: Material? = null

    var groundRenderable: Renderable? = null
    //var textureGround = Renderable(loadMeshes("assets\\textures\\ground_emit.obj"), Matrix4f());
    var lastXMousePos: Double = 0.0
    var lastYMousePos: Double = 0.0

    var camera: TronCamera = TronCamera(modelMatrix = Matrix4f())  //Kamera schaut immer auf sphere drauf
    var cameraPlayer = TronCamera(modelMatrix = Matrix4f(), transformable = sphere) //Kamera schaut immer auf sphere drauf


    var pointLights: PointLightComposite = PointLightComposite(listOf(
        PointLight(Vector3f(0f,2f,0f), Vector3f(0f,1f,1f), null),
        PointLight(Vector3f(10f,2f,-10f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(20f,2f,-20f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(-10f,2f,-10f), Vector3f(1f,0f,1f), null),
        PointLight(Vector3f(-20f,2f,-20f), Vector3f(1f,0f,1f), null)))
/*

    var pointLights: PointLightComposite = PointLightComposite(listOf(
        PointLight(Vector3f(0f,2f,0f), Vector3f(0f,1f,1f), null),
        PointLight(Vector3f(0f,2f,0f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(0f,2f,0f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(0f,2f,0f), Vector3f(1f,0f,1f), null),
        PointLight(Vector3f(0f,2f,0f), Vector3f(1f,0f,1f), null)))
    */



   var spotlights: SpotlightComposite = SpotlightComposite(mutableListOf(
       Spotlight(Vector3f(10f,1f,-10f), Vector3f(0f,1f,1f),null, 50f,70f),
       Spotlight(Vector3f(10f,1f,-10f), Vector3f(0f,1f,1f),null, 40f,60f),
       Spotlight(Vector3f(10f,1f,-10f), Vector3f(0f,1f,1f),null, 30f,50f),
       Spotlight(Vector3f(10f,1f,-10f), Vector3f(0f,1f,1f),null, 20f,40f),
       Spotlight(Vector3f(10f,1f,-10f), Vector3f(0f,1f,1f),null, 20f,40f)))

    var curShader = 2
    var maxShader = 3
    var shaderChangeCooldown = 0f
    var focusObjectCoolown = 0f
    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        shadowShader = ShaderProgram("assets/shaders/shadow_vert.glsl", "assets/shaders/shadow_frag.glsl")

        //initial opengl state
        glClearColor(0.6f, 1.0f, 1.0f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        glEnable ( GL_CULL_FACE )
        glFrontFace ( GL_CCW )
        glCullFace ( GL_BACK )
        //Punkte müssen gegen den Uhrzeigersinn definiert werden

        glClearColor (0.0f , 0.0f , 0.0f , 1.0f); GLError . checkThrow ()

        staticShader.use() // der zu benutzende Shade
        staticShader.setUniform("shader", curShader)
        //spotlight.getWorldZAxis().negate().mul(Matrix3f(camera.getCalculateViewMatrix()))

        //spotlight.rotate(Math.toRadians(-90.00f),0f,0f);

        //Wrapping == Soll Textur wiederholt werden(GL_REPEAT) oder sollen pixel am Rand wiederholt werden(GL_CLAMP_TO_EDGE)?
        //Filtering == Soll der nähste Pixel verwendet werden(GL_NEAREST) oder soll linear interpoliert(GL_LINEAR_MIPMAP_LINEAR) werden?
        texEmit.setTexParams(GL12.GL_REPEAT, GL12.GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_NEAREST)
        //Effekte:
        //GL12.GL_REPEAT =
        //GL12.GL_CLAMP_TO_EDGE = einmaliges zeichnen und Rand wird wiederholt
        //GL_LINEAR_MIPMAP_LINEAR = Pixel haben weiche Übergänge
        //GL_NEAREST = Bild sieht schärfer aus



        groundMaterial = Material(
            texDiff,
            texEmit,
            texSpec,
            shininess = 60.0f,
            tcMultiplier = Vector2f(64.0f, 64.0f)
        )

        groundRenderable = Renderable(loadMeshes("assets\\models\\ground.obj", groundMaterial), groundMatrix);

        //motorcycleRenderable!!.scale(Vector3f(0.8f))
        //motorcycleRenderable!!.translate(Vector3f(0.0f,1f,0.0f))


        //camera.rotate(Math.toRadians(-35f),0f,0f)
        camera.translate(Vector3f(0f, 10f, 10f))

        staticShader.setUniform("lightColorAmbiente", Vector3f(0f,0.01f,0f))


        //Shadow Mapping
        // Framebuffer for Shadow Map
/*
        var SHADOW_WIDTH = 2048
        var SHADOW_HEIGHT = 2048

        var depthMapFBO = GL30.glGenFramebuffers();

        var depthMap = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthMap);
        //GL_DEPTH_COMPONENT = we just want to save the depth
        var pixels: IntArray? = null
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
            SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, pixels);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);//GL_REPEAT
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);//GL_REPEAT

        //
        val clampColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, clampColor)

        //attatch depthbuffer to framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
        //Man benötigt die Farbwerte nicht
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        /*
        // 1. first render to depth map
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO)
        glClear(GL_DEPTH_BUFFER_BIT)
        //?ConfigureShaderAndMatrices()
        //?RenderScene()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        // 2. then render scene as normal with shadow mapping (using depth map)
        glViewport(0, 0, 1000, 1000)//??SCR_WIDTH
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        //?ConfigureShaderAndMatrices()
        glBindTexture(GL_TEXTURE_2D, depthMap)
        //?RenderScene()
*/
        var lightProjection = camera.getOrthogonalCamera()
        var lightView = Matrix4f().lookAt(spotlights.list[0].getWorldPosition(),
            Vector3f( 0.0f, 0.0f,  0.0f),
            spotlights.list[0].getWorldYAxis());

        shadowShader.use()
        var lightSpaceMatrix = lightProjection.mul(lightView);  //T
        shadowShader.setUniform("lightSpaceMatrix", lightSpaceMatrix, false)

        // Enables the Depth Buffer
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO)
        glClear(GL_DEPTH_BUFFER_BIT)

        field!!.render(shadowShader, null)


        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
*/

    //shadowShader.use()
        var pos = field.getCellPos(3,1).add(Vector3f(0f,2.5f,0f))
        sphere!!.translate(pos)
    }
    fun loadMeshes(path: String, mat: Material? = null) : MutableList<Mesh> {
        val objRes: OBJLoader.OBJResult = OBJLoader.loadOBJ(path)

        val objMeshList: MutableList<OBJLoader.OBJMesh> = objRes.objects[0].meshes
        val meshs: MutableList<Mesh> = arrayListOf()

        val vertexAttributes = arrayOf(
            VertexAttribute(3, GL_FLOAT, 32, 0),    //pos
            VertexAttribute(2, GL_FLOAT, 32, 12),   //textur
            VertexAttribute(3, GL_FLOAT, 32, 20)    //norm
        )
        for (i in 0..objMeshList.size - 1) {
            meshs.add(Mesh(objMeshList[i].vertexData, objMeshList[i].indexData, vertexAttributes, mat))
        }

        return meshs
    }
    // für Aufgabe 2.1
    fun renderMeshes(list: MutableList<Mesh>)
    {
        for(i in 0..list.size-1)
        {
            list[i].render()
        }
    }

    fun render(dt: Float, t: Float) {
        //mesh!!.render()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        //mesh?.render()
        camera.bind(staticShader) //binden der Kamera


        pointLights.bind(staticShader)//Reihenfolge ist kritisch???

        spotlights.bind(staticShader, camera.getCalculateViewMatrix())

        field!!.render(staticShader, shadowShader)

        sphere!!.render(staticShader, shadowShader)

        groundRenderable!!.render(staticShader, shadowShader) //rendern mit shader

        /* //Aufagebe 2.1 manuell
        staticShader.setUniform("view_matrix", Matrix4f(), false)
        staticShader.setUniform("projection_matrix", Matrix4f(), false)


        staticShader.setUniform("model_matrix", sphereMatrix, false)
        renderMeshes(sphereMeshes)

        staticShader.setUniform("model_matrix", groundMatrix, false)
        renderMeshes(groundMeshes)
        */
    }

    fun update(dt: Float, t: Float)
    {
        camera.update(dt)


        if(window.getKeyState(GLFW.GLFW_KEY_O))
        {
            camera.zoom(1.01f*dt)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_I))
        {
            camera.zoom(0.95f*dt)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_R))
        {
            camera.resetZoom()
        }
        if(window.getKeyState(GLFW.GLFW_KEY_M) && focusObjectCoolown <= 0f)
        {
            camera.lockToPos(getNearestObjectToPoint(camera.getWorldPosition()), 5f)
            focusObjectCoolown=2f
        }else if(focusObjectCoolown > 0f)
        {
            focusObjectCoolown -= dt
        }
        if(window.getKeyState(GLFW.GLFW_KEY_U))
        {
            camera.unlock()
        }
        if(shaderChangeCooldown > 0)
        shaderChangeCooldown -= dt
        if(window.getKeyState(GLFW.GLFW_KEY_C) && shaderChangeCooldown <= 0)
        {
            curShader++
            if(curShader == maxShader)
            {
                curShader = 0
            }
            staticShader.setUniform("shader", curShader)
            shaderChangeCooldown = 1f
        }
        //camera.translate(Vector3f(0.1f,0.1f,0.1f))
        if(window.getKeyState(GLFW.GLFW_KEY_UP))
        {
            camera.translate(Vector3f(0f,0f,-0.1f))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_DOWN))
        {
            camera.translate(Vector3f(0f,0f,0.1f))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_H))
        {
            field.hidePath()
        }
        if(window.getKeyState(GLFW.GLFW_KEY_J))
        {
            field.showPath()
        }

        var xVel = 0.0f
        if(window.getKeyState(GLFW.GLFW_KEY_W))
        {
            xVel = -7f*dt
            sphere!!.translate(Vector3f(0.0f,0.0f,xVel))
        }else if(window.getKeyState(GLFW.GLFW_KEY_S))
        {
            xVel = 7f*dt
            sphere!!.translate(Vector3f(0.0f,0.0f,xVel))
        }


        if(window.getKeyState(GLFW.GLFW_KEY_A))
        {
            sphere!!.rotate(0.0f, 0.05f, 0.0f)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_D))
        {
            sphere!!.rotate(0.0f, -0.05f, 0.0f)
        }
    }

    fun getNearestObjectToPoint(pos: Vector3f): Vector3f
    {
        var nearest = groundRenderable!!.getWorldPosition()

        var nearestCell: Vector2i = field.getNearestCellToPos(pos)
        if(nearest.distance(pos) > field.getCellPos(nearestCell.x, nearestCell.y).distance(pos))
        {
            nearest = field.getCellPos(nearestCell.x, nearestCell.y)
        }
        return nearest
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double)
    {
        var deltaxPos = lastXMousePos - xpos;
        lastXMousePos = xpos;

        var deltayPos = lastYMousePos - ypos;
        lastYMousePos = ypos;


        camera.rotate((deltayPos * 0.002).toFloat(),0f,0f);
        camera.rotateAroundPoint(0f,(deltaxPos * 0.002).toFloat(),0f,camera.getWorldPosition().add(camera.getWorldZAxis().normalize().mul(0.1f)))

    }


    fun cleanup() {}
}
