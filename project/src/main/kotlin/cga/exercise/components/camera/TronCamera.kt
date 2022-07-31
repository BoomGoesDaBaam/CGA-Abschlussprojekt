package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class TronCamera(var fieldOfView: Float = 1.57f, var aspectRatio: Float = 16f / 9f, var nearPlane: Float= 0.1f, var farPlane: Float= 100.0f, modelMatrix: Matrix4f = Matrix4f(), transformable: Transformable? = null): ICamera, Transformable(modelMatrix, transformable){
    private var lookAt: Vector3f? = null
    override fun getCalculateViewMatrix(): Matrix4f {
        //lookat(position of camera(eye), the point in space to look at(center), the direction of 'up'
        //Normalized Device Space
        if(lookAt == null)
            return Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()), getWorldYAxis())
        else
            return Matrix4f().lookAt(getWorldPosition(), lookAt, getWorldYAxis())

    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        //fieldOfView = the vertical field of view in radians
        //aspectRatio = seitenverhältnis
        //nearPlane = Entfernung der near plane zur camera
        //farPlane = Entfernung der far plane zur camera
        return Matrix4f().perspective(fieldOfView, aspectRatio, nearPlane, farPlane)
    }
    fun getOrthogonalCamera(): Matrix4f {
        //fieldOfView = the vertical field of view in radians
        //aspectRatio = seitenverhältnis
        //nearPlane = Entfernung der near plane zur camera
        //farPlane = Entfernung der far plane zur camera
        return Matrix4f().ortho(-35f, 35f, -35f, 35f, nearPlane, farPlane)
    }
    fun resetZoom()
    {
        fieldOfView = 1.57f
    }
    fun lockToPos(pos: Vector3f, distance: Float)
    {
        resetModelMatrix()
        preTranslate(Vector3f(pos).add(Vector3f(0f,5f,distance)))
        lookAt = Vector3f(pos)
    }
    fun update(dt: Float)
    {
        if (lookAt != null)
            rotateAroundPoint(0f, 1f * dt, 0f, lookAt!!)
    }
    fun unlock()
    {
        lookAt = null
    }
    fun zoom(factor: Float)
    {
        //Sichtwinkel in Grad. Desto kleiner, desto kleiner ist der dargestellte Bereich
        if(factor > 0 && fieldOfView * factor < Math.PI)
        fieldOfView *= factor
    }

    override fun bind(shader: ShaderProgram) {
        shader.use()
        shader.setUniform("view_matrix",getCalculateViewMatrix(),false);
        shader.setUniform("projection_matrix",getCalculateProjectionMatrix(),false);
    }
}