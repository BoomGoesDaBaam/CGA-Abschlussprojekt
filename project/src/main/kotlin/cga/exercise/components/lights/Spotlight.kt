package cga.exercise.components.lights

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f

class Spotlight(pos: Vector3f, color: Vector3f, parent: Transformable?, private var innerCone: Float,private var outerCone: Float): PointLight(pos, color, parent),ISpotLight {
    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        super.bind(shaderProgram, "spot")
        shaderProgram.setUniform("spotlightorien",getWorldZAxis().negate().mul(Matrix3f(viewMatrix)));
        shaderProgram.setUniform("innerCone",Math.toRadians(innerCone.toDouble()).toFloat());
        shaderProgram.setUniform("outerCone",Math.toRadians(outerCone.toDouble()).toFloat());
    }
    fun getInnerCone(): Float
    {
        return Math.toRadians(innerCone.toDouble()).toFloat()
    }
    fun getOuterCone(): Float
    {
        return Math.toRadians(outerCone.toDouble()).toFloat();
    }
}