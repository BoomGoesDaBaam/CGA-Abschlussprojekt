package cga.exercise.components.lights

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

interface ISpotLight {
    fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f)
}