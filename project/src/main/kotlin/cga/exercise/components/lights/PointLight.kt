package cga.exercise.components.lights

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

open class PointLight(pos: Vector3f, val color: Vector3f, parent: Transformable?): Transformable(Matrix4f(),parent),IPointLight {

    init {
        translate(pos)
    }
    override fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.setUniform(name+"lightPos",getWorldPosition());
        shaderProgram.setUniform(name+"lightcolor",color);
    }
}