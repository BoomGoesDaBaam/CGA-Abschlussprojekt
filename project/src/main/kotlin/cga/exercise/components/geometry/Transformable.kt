package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.Vector

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    //Die Modelmatrix stellt alle Transformationen als Matrix dar
    fun getModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }
    fun resetModelMatrix()
    {
        modelMatrix = Matrix4f()
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        val copy = Matrix4f(modelMatrix)
        if (parent != null) {
            parent!!.getWorldModelMatrix().mul(modelMatrix, copy)
        }
        return copy
    }

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotate(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotateX(pitch)
        modelMatrix.rotateY(yaw)
        modelMatrix.rotateZ(roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val newMatrix = Matrix4f().translate(Vector3f(altMidpoint))
        newMatrix.rotateXYZ(pitch,yaw,roll)
        newMatrix.translate(Vector3f(altMidpoint).negate())
        modelMatrix = newMatrix.mul(modelMatrix)

    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    open fun translate(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun preTranslate(deltaPos: Vector3f) {
        modelMatrix.translateLocal(deltaPos)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scale(scale: Vector3f) {
        modelMatrix.scale(scale)

    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        var returnThis = Vector3f()
        modelMatrix.getColumn(3,returnThis)
        return returnThis
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
        var vec = Vector3f()
        getWorldModelMatrix().getColumn(3,vec)
        return vec
    }
    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        var returnThis = Vector3f()
        modelMatrix.getColumn(0,returnThis).normalize()
        return returnThis
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        var returnThis = Vector3f()
        modelMatrix.getColumn(1,returnThis).normalize()
        return returnThis
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        var returnThis = Vector3f()
        modelMatrix.getColumn(2,returnThis).normalize()
        return returnThis
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        var returnThis = Vector3f()
        returnThis = getWorldModelMatrix().getColumn(0,returnThis).normalize()
        return returnThis
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        var returnThis = Vector3f()
        returnThis =  getWorldModelMatrix().getColumn(1,returnThis).normalize()
        return returnThis
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        var returnThis = Vector3f()
        returnThis = getWorldModelMatrix().getColumn(2,returnThis)
        returnThis = returnThis.normalize()
        return returnThis
    }
}