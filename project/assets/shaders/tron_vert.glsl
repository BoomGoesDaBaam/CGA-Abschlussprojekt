#version 330 core       //Version die benutzt wird

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 texcoords;
//uniforms
// translation object to world
uniform mat4 model_matrix; // Vektorisierte Daten
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

uniform vec2 tcMultiplier;

uniform vec3 lightPos[5];
uniform vec3 spotlightPos[5];

out struct VertexData   // gibt vertex daten weiter
{
    vec3 position;
    vec2 texcoord;
    vec3 normal;    //acctually tangent
    vec3 LightDir[5];
    vec3 SpotLightDir[5];
    vec3 ViewDir;
} vertexData;


//
void main(){

    //diffuse reflection
    //vec4 lp = view_matrix * vec4(lightPos, 0.0f);       //position der lichtquelle in camera space
    //vec4 p = (view_matrix * model_matrix * vec4(position,0.0f));//vertex position in camera space
   // LightDir = (lp - p).xyz;

    //Reihenfolge wurde durch Folien vorgegeben
    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f); // wo soll pixel gespeichert werden
    //vec4 pos = model_matrix * vec4(position, 1.0f);
    //gl_Position = vec4(pos.xy, -pos.z, 1.0f);
    gl_Position = pos;

    vertexData.position = pos.xyz;
    //"Normalen müssen mit der transponierten inversen transformiert werden
    vertexData.normal = (transpose(inverse(mat3(view_matrix * model_matrix))) * normal);

    //vertexData.normal = vec3(normal.x, normal.z, normal.y);
    //Der tcMultiplier gibt an, wie häufig eine Textur auf einem Objekt wiederholt wird
    vertexData.texcoord = texcoords * tcMultiplier;
    vertexData.ViewDir = -vec3(view_matrix * model_matrix * vec4(position, 1.0f));

    for(int i=0;i<5;i++)
    {
        vec4 lP = view_matrix * vec4(lightPos[i], 1.0f);                //lichposition in cameraspace
        vec4 p = view_matrix * model_matrix * vec4(position, 1.0f);     //vertex in cameraspace
        vertexData.LightDir[i] = (lP - (p )).xyz;                       //LightDir ist vector, der vom Vertex auf die Lichtquelle zeigt
    }

    for(int i=0;i<5;i++)
    {
        vec4 lP = view_matrix * vec4(spotlightPos[i], 1.0f);                //lichposition in cameraspace
        vec4 p = view_matrix * model_matrix * vec4(position, 1.0f);         //vertex in cameraspace
        vertexData.SpotLightDir[i] = (lP - (p )).xyz;                       //LightDir ist vector zwischen lichquelle und vertex
    }
}
