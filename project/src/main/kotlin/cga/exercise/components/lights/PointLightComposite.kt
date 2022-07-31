package cga.exercise.components.lights

import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class PointLightComposite(list: List<PointLight>): IPointLight {

    var colorsBuffer: FloatArray = FloatArray(3 * 5);
    var posBuffer: FloatArray = FloatArray(3 * 5);
    init {
        for(i in 0..(list.size)* 3 - 1)
        {
            colorsBuffer[i] = (list[i/3].color.get(i%3))
        }
        for(i in 0..(list.size)* 3 - 1)
        {
            posBuffer[i] = (list[i/3].getWorldPosition().get(i%3))
        }
    }

    override fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.setUniformVec3Array("lightcolor", colorsBuffer)
        shaderProgram.setUniformVec3Array("lightPos", posBuffer)

    }
}