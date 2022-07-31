package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        //                        name      textureunit
        //Gibt Textureunit von Textur in den shader
        shaderProgram.setUniform("emit",0)
        //Bindet Textur auf eine Bestimmte Texturunit
        emit.bind(0)

        shaderProgram.setUniform("diff",1)
        diff.bind(1)

        shaderProgram.setUniform("spec",2)
        specular.bind(2)

        //Der tcMultiplier gibt an, wie häufig eine Textur auf einem Objekt wiederholt wird
        //also wird eine Textur über das ganze Objekt gezogen
        shaderProgram.setUniform("shininess", shininess)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
    }
}