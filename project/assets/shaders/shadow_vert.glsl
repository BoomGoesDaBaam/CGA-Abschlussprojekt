#version 330 core
layout (location = 0) in vec3 apos;

uniform mat4 lightSpaceMatrix;
uniform mat4 model_matrix;

void main()
    {
    gl_Position = lightSpaceMatrix * model_matrix * vec4(apos, 1.0);
}