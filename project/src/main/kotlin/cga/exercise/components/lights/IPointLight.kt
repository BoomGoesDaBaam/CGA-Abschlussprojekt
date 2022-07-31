package cga.exercise.components.lights

import cga.exercise.components.shader.ShaderProgram

interface IPointLight {
    fun bind(shaderProgram: ShaderProgram, name: String = "")
}