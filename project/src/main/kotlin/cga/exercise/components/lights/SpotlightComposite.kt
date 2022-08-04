package cga.exercise.components.lights

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix3f
import org.joml.Matrix4f

class SpotlightComposite(var list: List<Spotlight>): ISpotLight{
    var colorsBuffer: FloatArray = FloatArray(3 * 5);
    var posBuffer: FloatArray = FloatArray(3 * 5);
    var spotlightorientations: FloatArray = FloatArray(3 * 5);
    var enabled: FloatArray = FloatArray(3 * list.size);

    var innerCones: FloatArray = FloatArray(5);
    var outerCones: FloatArray = FloatArray(5);
    init {
        for(i in 0..4)
        {
            enabled[i] = 0.0f
        }
    }
    fun setVisibilityOfLight(index: Int, value: Float)
    {
        if(index >= 0 && index < list.size)
            enabled[index] = value
    }

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        for(i in 0..(list.size)* 3 - 1)
        {
            enabled[i] = 0.0f
        }
        for(i in 0..(list.size)* 3 - 1)
        {
            colorsBuffer[i] = (list[i/3].color.get(i%3))
        }
        for(i in 0..(list.size)* 3 - 1)
        {
            posBuffer[i] = (list[i/3].getWorldPosition().get(i%3))
        }
        for(i in 0..(list.size)* 3 - 1)
        {
            spotlightorientations[i] = list[i/3].getWorldZAxis().negate().mul(Matrix3f(viewMatrix))[i%3]
        }
        for(i in 0..list.size - 1)
        {
            innerCones[i] = list[i].getInnerCone()
            outerCones[i] = list[i].getOuterCone()
        }

        shaderProgram.setUniformVec3Array("spotlightcolor", colorsBuffer)
        shaderProgram.setUniformVec3Array("spotlightPos", posBuffer)

        shaderProgram.setUniformFloatArray("spotLightIsEnabled", enabled)


        shaderProgram.setUniformVec3Array("spotlightorien",spotlightorientations);
        shaderProgram.setUniformFloatArray("innerCone",innerCones);
        shaderProgram.setUniformFloatArray("outerCone",outerCones);
    }
}