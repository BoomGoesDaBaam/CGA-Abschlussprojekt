package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

open class Renderable(var meshes: MutableList<Mesh>, modelMatrix: Matrix4f, parent:Transformable? = null): IRenderable, Transformable(modelMatrix, parent) {
    override fun render(shaderProgram: ShaderProgram, shadowShader: ShaderProgram?) {
        var pos = getWorldPosition()
        shaderProgram.setUniform("model_matrix" , getWorldModelMatrix(),false)
        //shadowShader.setUniform("model_matrix" , getWorldModelMatrix(),false)

        for(i in 0..meshes.size-1)
        {
            meshes[i].render(shaderProgram)
        }
    }
}