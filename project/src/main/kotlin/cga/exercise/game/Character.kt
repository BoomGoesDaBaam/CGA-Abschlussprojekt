package cga.exercise.game

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

open class Character: Renderable(mutableListOf<Mesh>(), Matrix4f()) {

    var playerMat = Material(
        Texture2D("assets/textures/char.png", true),
        Texture2D("assets/textures/char.png", true),
        Texture2D("assets/textures/char.png", true)
    )

    var player = ModelLoader.loadModel("assets/models/player/player.obj", 0f, 0f, 0f)
    var sphere = ModelLoader.loadModel("assets/models/mini_sphere.obj", 0f, 0f, 0f)


    init {
        player!!.meshes[0].mat = playerMat
        player?.parent = this
/*
        vertexdata[0] = 10.0f
        vertexdata[1] = 10.0f
        vertexdata[2] = 10.0f

        vertexdata[8] = 10.01f
        vertexdata[9] = 10.01f
        vertexdata[10] = 10.01f
*/
/*
        var vertexdata: FloatArray = player!!.meshes[0].vertexdata

        //rotateRightFoot(45.0f, vertexdata)
        rotateLeftFoot(45.0f, vertexdata)

        //rotateRightLowerLeg(-45.0f, vertexdata)
        rotateLeftLowerLeg(-45.0f, vertexdata)

        rotateRightUpperLeg(-45.0f, vertexdata)
        rotateLeftUpperLeg(45.0f, vertexdata)

        player!!.meshes[0].updateVertecies(vertexdata)

        //rotateEverythingUnder(Vector3f(-0.819553f,0.521273f, -4.04336f),
        //    Vector3f(-0.082971f,-0.943048f, -3.10097f))

        sphere?.parent = this

        sphere?.translate(Vector3f(0.5f,-3.5f,0f))

        translate(Vector3f(0f, 7f, -5f))
        */
    }
    fun rotateRightFoot(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-2f,-5.1f, 2f),
            Vector3f(0f,-3.05f, -1f),
            Vector2f(-3.1f, 0.079254f), angle)
    }
    fun rotateLeftFoot(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(2f,-5.1f, 2f),
            Vector3f(0f,-3.05f, -1f),
            Vector2f(-3.1f, 0.079254f), angle)
    }
    fun rotateRightLowerLeg(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-2f,-2.1f, 2f),
            Vector3f(0f,-5.1f, -1f),
            Vector2f(-2.1f, 0.079254f), angle)
    }
    fun rotateLeftLowerLeg(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(2f,-2.1f, 2f),
            Vector3f(0f,-5.1f, -1f),
            Vector2f(-2.1f, 0.079254f), angle)
    }
    fun rotateRightUpperLeg(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-2f,-1.1f, 2f),
            Vector3f(0f,-5.1f, -1f),
            Vector2f(-1.1f, 0.079254f), angle)
    }
    fun rotateLeftUpperLeg(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(2f,-1.1f, 2f),
            Vector3f(0f,-5.1f, -1f),
            Vector2f(-1.1f, 0.079254f), angle)
    }
    //


    fun rotateRightHand(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-3.0547f,-2f, 0.5f),
            Vector3f(-2f,-0.7f, -0.522292f),
            Vector2f(-0.7f, -0.0f), angle)
    }
    fun rotateLeftHand(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(3.0547f,-2f, 0.5f),
            Vector3f(2f,-0.7f, -0.522292f),
            Vector2f(-0.7f, -0.0f), angle)
    }

    fun rotateLeftLowerArm(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(3.0547f,0.4f, 0.5f),
            Vector3f(1f,-2.7f, -0.522292f),
            Vector2f(0.4f, 0.0f), angle)
    }
    fun rotateRightLowerArm(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-3.0547f,0.4f, 0.5f),
            Vector3f(-1f,-2.7f, -0.522292f),
            Vector2f(0.4f, 0.0f), angle)
    }
    fun rotateLeftUpperArm(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(-3.0547f,1.8f, 0.5f),
            Vector3f(-1f,-2.7f, -0.522292f),
            Vector2f(1.8f, 0.0f), angle)
    }
    fun rotateRightUpperArm(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(3.0547f,1.8f, 0.5f),
            Vector3f(1f,-2.7f, -0.522292f),
            Vector2f(1.8f, 0.0f), angle)
    }
    fun rotateHead(angle: Float, vertexdata: FloatArray)
    {
        rotateEverythingInBetween(vertexdata, Vector3f(3.0547f,2f, 3f),
            Vector3f(-3f,5f, -3f),
            Vector2f(1.8f, 0.0f), angle)
    }
    fun rotateEverythingInBetween(vertexdataChanged: FloatArray, bounds1: Vector3f, bounds2: Vector3f, rotPlane: Vector2f, angle: Float)
    {
        var vertexdata: FloatArray = player!!.meshes[0].vertexdata
        var nVerts = 4260
        for(i in 0..nVerts-1)
        {
            var index = i * 8
            var curVector = Vector3f(vertexdata[index], vertexdata[index+1], vertexdata[index+2])
            var curPoint = Transformable(Matrix4f().translate(Vector3f(vertexdataChanged[index], vertexdataChanged[index+1], vertexdataChanged[index+2])))

            if(isInBetween(bounds1, bounds2, curVector)) {
                curPoint.rotateAroundPoint(
                    Math.toRadians(-angle.toDouble()).toFloat(),
                    0.0f,
                    0.0f,
                    Vector3f(curVector.x, rotPlane.x, rotPlane.y)
                )
                var pos = curPoint.getPosition()

                vertexdataChanged[index] = pos.x
                vertexdataChanged[index + 1] = pos.y
                vertexdataChanged[index + 2] = pos.z
            }
        }
    }
    fun isInBetween(bounds1: Vector3f, bounds2: Vector3f, pos: Vector3f):Boolean
    {
        return ((bounds2.x <= pos.x && pos.x <= bounds1.x)||(bounds1.x <= pos.x && pos.x <= bounds2.x))
                &&((bounds2.y <= pos.y && pos.y <= bounds1.y)||(bounds1.y <= pos.y && pos.y <= bounds2.y))
                &&((bounds2.z <= pos.z && pos.z <= bounds1.z)||(bounds1.z <= pos.z && pos.z <= bounds2.z))
    }
    override fun render(shaderProgram: ShaderProgram, shadowProgram: ShaderProgram?) {
        sphere?.render(shaderProgram, shadowProgram)

        player?.render(shaderProgram, shadowProgram)

    }
}