package cga.exercise.game

import cga.exercise.components.Logic.CellField
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
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
import org.joml.Vector2i
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram     //Shader
    private val shadowShader: ShaderProgram
    var player = ModelLoader.loadModel("assets/models/sphere.obj",0f,0f,0f)
    var playerIsMoving = false
    var animatedChar = AnimatedCharacter(0.75f, 0)
    var playerCanMove = false
    //var motorcycleRenderable = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90.0f),Math.toRadians(90.0f),0.0f)

    //min = 7 max =
    var pathLength = 12

     var field = CellField(pathLength);

    var texDiff = Texture2D("assets/textures/ground_diff.png", true)
    var texEmit = Texture2D("assets/textures/ground_emit.png", true)
    var texSpec = Texture2D("assets/textures/ground_spec.png", true)
    private var texMetal = Texture2D("assets/textures/metal.jpg", true)

    var stairsMat = Material(Texture2D("assets/textures/wood.jpg", true),
        Texture2D("assets/textures/wood.jpg", true),
        Texture2D("assets/textures/wood.jpg", true))

    var metalMat = Material(texMetal, texMetal, texMetal)


    var groundMaterial: Material? = null


    //var studio = Renderable(loadMeshes("assets/models/studio3.obj"), Matrix4f());

    var objekte = mutableListOf<Renderable>()


    //var groundRenderable: Renderable? = null
    //var textureGround = Renderable(loadMeshes("assets\\textures\\ground_emit.obj"), Matrix4f());
    var lastXMousePos: Double = 0.0
    var lastYMousePos: Double = 0.0

    var cameras = mutableListOf<TronCamera>()
    var flyingCam: TronCamera = TronCamera(modelMatrix = Matrix4f())  //Kamera schaut immer auf sphere drauf
    var playerCamera: TronCamera = TronCamera(modelMatrix = Matrix4f(), transformable = player)  //Kamera schaut immer auf sphere drauf
    var orthogonalCamera: TronCamera = TronCamera(modelMatrix = Matrix4f())

    var curActivCamera = 0

    var pointLights: PointLightComposite = PointLightComposite(mutableListOf(
        PointLight(Vector3f(0f,2f,0f), Vector3f(0f,1f,1f), null),
        PointLight(Vector3f(0f,0f,0f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(200f,-200f,0f), Vector3f(1f,1f,0f), null),
        PointLight(Vector3f(200f,-200f,0f), Vector3f(1f,0f,1f), null),
        PointLight(Vector3f(200f,-200f,0f), Vector3f(1f,0f,1f), null)
    ))

   var spotlights: SpotlightComposite = SpotlightComposite(mutableListOf(
       Spotlight(Vector3f(0f,2f,0f), Vector3f(0f,1f,0f),null, 5f,20f),
       Spotlight(Vector3f(0f,2f,0f), Vector3f(0f,1f,0f),null, 5f,20f),
       Spotlight(Vector3f(0f,2f,0f), Vector3f(0f,1f,0f),null, 5f,20f),
       Spotlight(Vector3f(0f,2f,0f), Vector3f(0f,1f,0f),null, 5f,20f),
       Spotlight(Vector3f(0f,2f,0f), Vector3f(0f,1f,0f),null, 5f,20f)))

    var curShader = 3
    var nShader = 4
    var shaderChangeCooldown = 0f
    var focusObjectCoolown = 0f
    var perspectivCoolown = 0f
    var cameraCoolown = 0f
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

        flyingCam.translate(Vector3f(0f, 10f, 10f))

        staticShader.setUniform("lightColorAmbiente", Vector3f(0f,0.01f,0f))
        //startPos
        player!!.translate(field.getCellPosition(1,1).add(Vector3f(0f,1f, 0f)))
        player!!.scale(Vector3f(0.1f, 0.1f, 0.1f))
        player!!.translate(Vector3f(0f,2f,0f))

        //pointLights.setVisibilityOfLight(staticShader, 0,1.0f)
        //pointLights.setVisibilityOfLight(staticShader,1,1.0f)

        cameras.add(flyingCam)
        cameras.add(playerCamera)
        cameras.add(orthogonalCamera)

        orthogonalCamera.rotate(Math.toRadians(-90.0).toFloat(), 0f ,0f)
        orthogonalCamera.translate(Vector3f(0f,-2f,0f))
        orthogonalCamera.orthogonalCamera = true

        objekte.add(Renderable(loadMeshes("assets/models/stairs2.obj"), Matrix4f()))
        objekte.add(Renderable(loadMeshes("assets/models/stairs2.obj"), Matrix4f()))
        objekte.add(Renderable(loadMeshes("assets/models/light.obj"), Matrix4f()))
        objekte.add(Renderable(loadMeshes("assets/models/Wall.obj"), Matrix4f()))

        objekte[0].meshes[0].mat = stairsMat;
        objekte[1].meshes[0].mat = stairsMat;

        objekte[0].rotate(0f,Math.toRadians(90.0).toFloat(), 0f)
        objekte[0].scale(Vector3f(4f,4f,4f))
        objekte[0].translate(Vector3f(-3f,-0.35f,2f))


        objekte[1].rotate(0f,Math.toRadians(-90.0).toFloat(), 0f)
        objekte[1].scale(Vector3f(4f,4f,4f))
        objekte[1].translate(Vector3f(1f,-0.35f,2f))

        objekte[2].translate(Vector3f(-0.25f,12f,5f)) // Lights
        objekte[2].scale(Vector3f(1.5f,1.5f,1.5f))
        objekte[2].meshes[0].mat = metalMat

        orthogonalCamera.parent = objekte[2]

        objekte[3].translate(Vector3f(-0.25f,-0.75f,20f))   //Wall
        objekte[3].rotate(0f,Math.toRadians(-90.0).toFloat(), 0f)
        objekte[3].meshes[0].mat = metalMat

        spotlights.list[0].rotate(Math.toRadians(-90.0).toFloat(), 0f, 0f)
        spotlights.list[0].rotate(0f, Math.toRadians(-15.0).toFloat(), 0f)
        spotlights.list[0].parent = objekte[2]
        spotlights.setVisibilityOfLight(staticShader, 0, 1f)

        spotlights.list[1].rotate(Math.toRadians(-90.0).toFloat(), 0f, 0f)
        spotlights.list[1].rotate(0f, Math.toRadians(15.0).toFloat(), 0f)
        spotlights.list[1].parent = objekte[2]
        spotlights.setVisibilityOfLight(staticShader, 1, 1f)

        spotlights.list[2].rotate(Math.toRadians(-120.0).toFloat(), 0f, 0f)
        spotlights.list[2].rotate(0f, Math.toRadians(-15.0).toFloat(), 0f)
        spotlights.list[2].parent = objekte[2]
        spotlights.setVisibilityOfLight(staticShader, 2, 1f)

        spotlights.list[3].rotate(Math.toRadians(-120.0).toFloat(), 0f, 0f)
        spotlights.list[3].rotate(0f, Math.toRadians(15.0).toFloat(), 0f)
        spotlights.list[3].parent = objekte[2]
        spotlights.setVisibilityOfLight(staticShader, 3, 1f)


        //objekte.add(Renderable(loadMeshes("assets/models/player/upper_leg.obj"), Matrix4f()))
        //objekte[4].translate(Vector3f(0f, 5f, 0f))


        var fan1 = AnimatedCharacter(charType = 1)
        fan1.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-80f, 70f, 70f, 0f, 0f, 20f,20f,20f,20f,10f))
        fan1.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-70f, 70f, 80f, 0f, 0f, 20f,20f,20f,20f,5f))
        fan1.translate(Vector3f(10f, 3.4f, -0.75f))
        fan1.scale(Vector3f(0.25f, 0.25f, 0.25f))
        fan1.rotate(0f,Math.toRadians(-20.0).toFloat(), 0f)
        fan1.startAnimation()
        objekte.add(fan1)

        var fan2 = AnimatedCharacter(charType =1)
        fan2.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-80f, 70f, 70f, 0f, 0f, 20f,20f,20f,20f,10f))
        fan2.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-70f, 70f, 80f, 0f, 0f, 20f,20f,20f,20f,5f))
        fan2.translate(Vector3f(11.5f, 3.4f, 0.4f))
        fan2.scale(Vector3f(0.25f, 0.25f, 0.25f))
        fan2.rotate(0f,Math.toRadians(-35.0).toFloat(), 0f)
        fan2.startAnimation()
        objekte.add(fan2)

        var fan3 = AnimatedCharacter(charType = 1)
        fan3.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-80f, 70f, 70f, 0f, 0f, 20f,20f,20f,20f,10f))
        fan3.addKeyFrame(AnimatedCharacter.KeyFrame(0f, 0f, -70f,-70f, 70f, 80f, 0f, 0f, 20f,20f,20f,20f,5f))
        fan3.translate(Vector3f(-11.0f, 1.4f, 5.0f))
        fan3.scale(Vector3f(0.25f, 0.25f, 0.25f))
        fan3.rotate(0f,Math.toRadians(-60.0).toFloat(), 0f)
        fan3.startAnimation()
        objekte.add(fan3)

        var moderator = AnimatedCharacter(charType = 2)
        moderator.addKeyFrame(AnimatedCharacter.KeyFrame(0f,0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, -0f, 10f))
        moderator.addKeyFrame(AnimatedCharacter.KeyFrame(0f,0f, 0f, 0f, 0f, 0f, 5f, 5f, 5f, 5f, 5f, 5f, -10f))
        //moderator.rotate(0f,Math.toRadians(180.0).toFloat(), 0f)
        moderator.translate(Vector3f(-1f, 1f, -2f))
        moderator.scale(Vector3f(0.25f, 0.25f, 0.25f))
        moderator.startAnimation()
        objekte.add(moderator)

        animatedChar.addKeyFrame(AnimatedCharacter.KeyFrame(30f,-45f, 0f, -45f, 45f, -45f, -15f, 45f, -15f, 45f, 45f, -15f, 10f))
        animatedChar.addKeyFrame(AnimatedCharacter.KeyFrame(-45f,30f, -45f, 0f, -45f, 45f, 45f, -15f, 45f, -15f, -15f, 45f, -10f))
        //animatedChar.translate(Vector3f(0f,10f,0f))
        animatedChar.scale(Vector3f(3f, 3f, 3f))
        animatedChar.rotate(0f,Math.toRadians(180.0).toFloat(), 0f)
        animatedChar.translate(Vector3f(0f, 1f, 0f))
        animatedChar.parent = player


        /*
        objekte.add(Renderable(loadMeshes("assets/models/player/upper_leg.obj"), Matrix4f()))
        objekte[6].translate(Vector3f(0f, 7f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/chest.obj"), Matrix4f()))
        objekte[7].translate(Vector3f(0f, 8f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/left_hand.obj"), Matrix4f()))
        objekte[8].translate(Vector3f(-3f, 6f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/right_hand.obj"), Matrix4f()))
        objekte[9].translate(Vector3f(3f, 6f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/lower_arm.obj"), Matrix4f()))
        objekte[10].translate(Vector3f(-3f, 7f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/upper_arm.obj"), Matrix4f()))
        objekte[11].translate(Vector3f(-3f, 8f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/head.obj"), Matrix4f()))
        objekte[12].translate(Vector3f(0f, 11f, 0f))
        objekte.add(Renderable(loadMeshes("assets/models/player/hat.obj"), Matrix4f()))
        objekte[13].translate(Vector3f(0f, 12f, 0f))
        //objekte.add(Renderable(loadMeshes("assets/models/stairs.obj"), Matrix4f()))
        //objekte.add(Renderable(loadMeshes("assets/models/light.obj"), Matrix4f()))
        //objekte.add(Renderable(loadMeshes("assets/models/Wall.obj"), Matrix4f()))
*/

        //studio.translate(Vector3f(0f,2f,0f))

        /*
        player!!.rotate(Math.toRadians(90.0).toFloat(),0f,0f)
        player!!.rotate(0f, Math.toRadians(90.0).toFloat(),0f)
        var axis = AxisAngle4d()
        player!!.getModelMatrix().getRotation(axis)

        player!!.rotate(-axis.x.toFloat(),-axis.y.toFloat(), -axis.z.toFloat())
        var axis2 = AxisAngle4d()
        player!!.getModelMatrix().getRotation(axis2)

        var k = 23
        */




        /*
        groundMaterial = Material(
            texDiff,
            texEmit,
            texSpec,
            shininess = 60.0f,
            tcMultiplier = Vector2f(64.0f, 64.0f)
        )

        groundRenderable = Renderable(loadMeshes("assets\\models\\ground.obj", groundMaterial), groundMatrix);
*/
        //motorcycleRenderable!!.scale(Vector3f(0.8f))
        //motorcycleRenderable!!.translate(Vector3f(0.0f,1f,0.0f))


        //camera.rotate(Math.toRadians(-35f),0f,0f)

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
        //var pos = field.getCellPos(3,1).add(Vector3f(0f,2.5f,0f))
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
        cameras[curActivCamera].bind(staticShader) //binden der Kamera

        pointLights.bind(staticShader)//Reihenfolge ist kritisch???

        spotlights.bind(staticShader, cameras[curActivCamera].getCalculateViewMatrix())


        field!!.render(staticShader, shadowShader)

        for(i in 0..objekte.size-1)
        {
            objekte[i].render(staticShader, shadowShader)
        }

        if(cameras[curActivCamera] != playerCamera)
        {
            animatedChar.render(staticShader, shadowShader)
        }

        //groundRenderable!!.render(staticShader, shadowShader) //rendern mit shader

        //field!!.translate(Vector3f(0f,0f,25f))
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
        flyingCam.update(dt)
        field.update(dt, player!!.getWorldPosition())

        if(field.gameState == CellField.state.FAILED)
        {
            spotlights.list.forEach { it.color = Vector3f(1f, 0f, 0f)}
        }
        else
        {
            spotlights.list.forEach { it.color = Vector3f(0f, 1f, 0f)}
        }

        animatedChar.update(dt)
        objekte.forEach {
            if(it is AnimatedCharacter)
            {
                it.update(dt)
            }
        }
        playerCanMove = field.gameState == CellField.state.PLAYER_STEPS

        if(window.getKeyState(GLFW.GLFW_KEY_ENTER))
        {
            field.continueGame()
        }
        if(window.getKeyState(GLFW.GLFW_KEY_O))
        {
            flyingCam.zoom(dt)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_I))
        {
            flyingCam.zoom(-dt)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_R))
        {
            flyingCam.resetZoom()
        }
        if(window.getKeyState(GLFW.GLFW_KEY_M) && focusObjectCoolown <= 0f)
        {
           // var topLeft = field.getTopLeftOftCells()
            //var camPos = camera.getWorldPosition()
            //var nearestPos = getNearestObjectToPoint(camera.getWorldPosition())
            flyingCam.lockToPos(getNearestObjectToPoint(flyingCam.getWorldPosition()), 5f)
            focusObjectCoolown=2f
        }else if(focusObjectCoolown > 0f)
        {
            focusObjectCoolown -= dt
        }

        if(window.getKeyState(GLFW.GLFW_KEY_P) && perspectivCoolown <= 0f)
        {
            flyingCam.switchPerspective()
            perspectivCoolown=2f
        }else if(perspectivCoolown > 0f)
        {
            perspectivCoolown -= dt
        }

        if(window.getKeyState(GLFW.GLFW_KEY_U))
        {
            flyingCam.unlock()
        }
        if(shaderChangeCooldown > 0)
        shaderChangeCooldown -= dt
        if(window.getKeyState(GLFW.GLFW_KEY_C) && shaderChangeCooldown <= 0)
        {
            curShader++
            if(curShader == nShader)
            {
                curShader = 0
            }
            staticShader.setUniform("shader", curShader)
            shaderChangeCooldown = 1f
        }
        //camera.translate(Vector3f(0.1f,0.1f,0.1f))
        if(window.getKeyState(GLFW.GLFW_KEY_T)) //GLFW_KEY_UP
        {
            flyingCam.translate(Vector3f(0f,0f,-0.1f))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_G)) // GLFW_KEY_DOWN
        {
            flyingCam.translate(Vector3f(0f,0f,0.1f))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_H))
        {
            field.hidePath()
        }
        if(window.getKeyState(GLFW.GLFW_KEY_J))
        {
            field.showPath()
        }
        var oldPlayerIsMoving = playerIsMoving
        playerIsMoving = window.getKeyState(GLFW.GLFW_KEY_W) || window.getKeyState(GLFW.GLFW_KEY_A) || window.getKeyState(GLFW.GLFW_KEY_S) || window.getKeyState(GLFW.GLFW_KEY_D)

        if(playerIsMoving != oldPlayerIsMoving)
        {
            if(playerIsMoving)
            {
                animatedChar.startAnimation()
            }
            else
            {
                animatedChar.stopAnimation()
            }
        }
        if(playerCanMove) {
            if (window.getKeyState(GLFW.GLFW_KEY_W)) {
                var zAxis = player!!.getWorldZAxis()
                var yAxis = player!!.getWorldYAxis()
                player!!.preTranslate(Vector3f(yAxis.x - zAxis.x, 0f, yAxis.z - zAxis.z).normalize().mul(0.05f))
            }
            if (window.getKeyState(GLFW.GLFW_KEY_A)) {
                var xAxis = player!!.getWorldXAxis()
                player!!.preTranslate(Vector3f(-xAxis.x, 0f, -xAxis.z).normalize().mul(0.05f))
            }
            if (window.getKeyState(GLFW.GLFW_KEY_S)) {

                var zAxis = player!!.getWorldZAxis()
                var xAxis = player!!.getWorldYAxis()
                player!!.preTranslate(Vector3f(-xAxis.x + zAxis.x, 0f, -xAxis.z + zAxis.z).normalize().mul(0.05f))
            }
            if (window.getKeyState(GLFW.GLFW_KEY_D)) {
                var xAxis = player!!.getWorldXAxis()
                player!!.preTranslate(Vector3f(xAxis.x, 0f, xAxis.z).normalize().mul(0.05f))
            }
        }

        if(window.getKeyState(GLFW.GLFW_KEY_R))
        {
            player!!.resetModelMatrix()
            player!!.translate(field.getCellPosition(1,1).add(Vector3f(0f,1f, 0f)))
            player!!.scale(Vector3f(0.1f, 0.1f, 0.1f))
            player!!.translate(Vector3f(0f, 2f, 0f))
            field.startGame(pathLength)
        }
        if(cameraCoolown <= 0f) {
            if(window.getKeyState(GLFW.GLFW_KEY_E)) {
                curActivCamera++
                cameraCoolown = 1f
                if (curActivCamera == cameras.size)
                    curActivCamera = 0
                if(cameras[curActivCamera] != playerCamera)
                {
                    resetPlayerPosition()
                }
            }
        }
        else
        {
            cameraCoolown -=dt
        }
    }
    fun resetPlayerPosition()
    {
        var pos = player!!.getPosition()
        player!!.resetModelMatrix()
        player!!.translate(pos)
        player!!.scale(Vector3f(0.1f, 0.1f, 0.1f))
    }

    fun getNearestObjectToPoint(pos: Vector3f): Vector3f
    {
        var nearest = Vector3f(100f,100f,100f)//groundRenderable!!.getWorldPosition()

        var nearestCell: Vector2i = field.getNearestCellToPos(pos)
        if(nearest.distance(pos) > field.getCellPosition(nearestCell.x, nearestCell.y).distance(pos))
        {
            nearest = field.getCellPosition(nearestCell.x, nearestCell.y)
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


        var transformable: Transformable = cameras[curActivCamera]
        transformable = when(curActivCamera)
        {
            0 -> cameras[curActivCamera]
            1 -> player!!
            else -> player!!
        }
        if(curActivCamera == 2) return

        transformable.rotate((deltayPos * 0.002).toFloat(),0f,0f);
        transformable.rotateAroundPoint(0f,(deltaxPos * 0.002).toFloat(),0f,transformable.getPosition().add(transformable.getZAxis().normalize().mul(0.1f)))

    }


    fun cleanup() {}
}
