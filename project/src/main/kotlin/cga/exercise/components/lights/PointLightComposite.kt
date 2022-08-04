package cga.exercise.components.lights

import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class PointLightComposite(var list: MutableList<PointLight>): IPointLight {

    var colorsBuffer: FloatArray = FloatArray(3 * list.size);
    var posBuffer: FloatArray = FloatArray(3 * list.size);
    var enabled: FloatArray = FloatArray(3 * list.size);


    /*
    fun addPointLight(pointLight: PointLight) {
        list.add(pointLight)
        colorsBuffer = FloatArray(3 * list.size);
        posBuffer = FloatArray(3 * list.size);
    }*/
    init {
        for(i in 0..4)
        {
            enabled[i] = 0.0f
        }
    }
    fun setVisibilityOfLight(shaderProgram: ShaderProgram,index: Int, value: Float)
    {
        if(index >= 0 && index < list.size)
            enabled[index] = value
        shaderProgram.setUniformFloatArray("pointLightIsEnabled", enabled)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String) {
        for(i in 0..(list.size)* 3 - 1)
        {
            colorsBuffer[i] = (list[i/3].color.get(i%3))
        }
        for(i in 0..(list.size)* 3 - 1)
        {
            posBuffer[i] = (list[i/3].getWorldPosition().get(i%3))
        }

        shaderProgram.setUniformVec3Array("lightcolor", colorsBuffer)
        shaderProgram.setUniformVec3Array("lightPos", posBuffer)
        shaderProgram.setUniformFloatArray("pointLightIsEnabled", enabled)

    }
}