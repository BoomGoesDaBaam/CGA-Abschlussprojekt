#version 330 core
//declares the version of glsl

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texcoord;
    vec3 normal;
    vec3 LightDir[5];
    vec3 SpotLightDir[5];
    vec3 ViewDir;  //Vector von vertex Richtung Kamera
} vertexData;

uniform int shader;    //0 == Phong 1== Blinn Phong 3 == Toon
//sampler = Datentyp für Texturen
//verbunden mit texturunits in der gpu
uniform sampler2D emit;
uniform sampler2D diff;
uniform sampler2D spec;
uniform sampler2D normal;

//PointLight + Diffuse
uniform vec3 lightcolor[5];
uniform float pointLightIsEnabled[5];
uniform vec3 spotlightcolor[5];

//Ambiente Reflektion
uniform vec3 lightColorAmbiente;

//Speculare Reflektion
uniform float shininess;

uniform int useNormalMapping;

uniform float innerCone[5];
uniform float outerCone[5];
uniform vec3 lightPos[5];
uniform vec3 spotlightPos[5];
uniform float spotLightIsEnabled[5];

uniform vec3 spotlightorien[5];

//fragment shader output
out vec4 color;

//Drückt eine Textur einen Farbwert aus muss man den Gammakorrektur-Schritt anwenden, hat
//man anderweitige Daten, sind diese meistens schon linear.
vec3 gamma ( vec3 C_linear ) // returns value in gamma / sRGB space
{
    return vec3(pow(C_linear[0], 1/2.2), pow(C_linear[1], 1/2.2), pow(C_linear[2], 1/2.2));
}
vec3 invgamma ( vec3 C_gamma ) // returns value in linear space
{
    return vec3(pow(C_gamma[0], 2.2), pow(C_gamma[1], 2.2), pow(C_gamma[2], 2.2));
}

void main(){
    //abs = we just need positive values for the colors
    //normalize = we want the length of the vector to be 1
    // R G B = X;Y;Z
    //color = vec4(abs(normalize(vertexData.normal)), 1.0f); //Normalisieren
    //color = vec4(0, (0.5f + abs(vertexData.position.z)), 0, 1.0f);
    //color = texture2D(emit,vertexData.texcoord);

    //länge der normalen der unterschiedlichen positionen ist nicht konstant

    //Vorlesung

    //Drückt eine Textur einen Farbwert aus muss man den Gammakorrektur-Schritt anwenden, hat
    //man anderweitige Daten, sind diese meistens schon linear.
    vec4 diffTexture = vec4(invgamma(texture(diff, vertexData.texcoord).rgb), 1f);
    vec4 emitTexture = vec4(invgamma(texture(emit, vertexData.texcoord).rgb), 1f);
    vec4 specTexture = vec4(invgamma(texture(spec, vertexData.texcoord).rgb), 1f);


    color = emitTexture + diffTexture * vec4(lightColorAmbiente, 1f);
    if(shader == 0 || shader == 1)
    {
        //Emissive Reflektion (M_e) + Ambiente Reflektion
        vec3 N = normalize(vertexData.normal);
        if(useNormalMapping != 0)
        {
            //N = normalize(texture(normal,  vertexData.texcoord).rgb);
            //N.r *= -1;
        /*
            float placeHolder = N.b;
            N.b = N.g;
            N.g = placeHolder;
        */
        }
        //pointlight
        for(int i=0;i<5;i++)
        {
            if(pointLightIsEnabled[i] != 0.0f)
            {
                vec3 L = normalize(vertexData.LightDir[i]);
                //Diffuse Reflection
                float cosa = max(0.0f, dot(N, L));//dot(N, L) = cos(alpha)
                //colorDiffuse = M_d * max(cos(winkel), 0.0f)        emitPoints=Regenbogeneffekt lightcolor[i]=Normale Lichtfarbe
                vec4 colorDiffuse = vec4(diffTexture.xyz * cosa, 1.0f);

                //Speculare Reflection
                vec3 V = normalize(vertexData.ViewDir);
                vec3 R = normalize(reflect(-L, N));//-L ist der zu reflektierende Vector
                //N ist die Oberfläche, an der Reflektiert werden soll
                vec4 colorSpecular;
                //The only difference between Blinn-Phong and Phong specular reflection is that we now measure the
                //angle between the normal and halfway vector instead of the angle between the view and reflection vector.
                if(shader == 1)
                {
                    //vec3 lightDir   = normalize(lightPos[i] - vertexData.position);
                    //vec3 viewDir = normalize(viewPos - vertexData.position);
                    vec3 halfwayDir = normalize(normalize(vertexData.LightDir[i]) + normalize(vertexData.ViewDir));
                    float spec = pow(max(dot(N, halfwayDir), 0.0), shininess);
                    colorSpecular = specTexture * vec4(lightcolor[i], 1f) * spec * 20;
                }
                else
                {
                    float cosBeta = max(0.0f, dot(R, V));
                    float cosBetak = pow(cosBeta, shininess);// shininess = Glanzvariable
                    colorSpecular = specTexture * cosBetak * vec4(lightcolor[i], 1f) * 20;//emitPoints = Regenbogen  lightcolor[i]=Normale Lampenfarbe
                }
                //Attenuation + Summe diffusive und spekulare reflektion mal lichtfarbe abhängig von Entfernung
                color += (colorDiffuse + colorSpecular) * (1/(pow(length(vertexData.LightDir[i]),2)));
            }
     }
     //spotlight
     for(int i=0;i<5;i++)
     {
         if(spotLightIsEnabled[i] != 0.0f)
         {
            vec3 L = normalize(vertexData.SpotLightDir[i]);
            //Diffuse Reflection
            float cosa = max(0.0f, dot(N, L));//dot(N, L) = cos(winkel)
                vec4 colorDiffuse = vec4(diffTexture.xyz * cosa, 1.0f) * vec4(spotlightcolor[i], 1.0f);

                vec4 colorSpecular;
                if(shader == 1)
                {
                    vec3 halfwayDir = normalize(normalize(vertexData.SpotLightDir[i]) + normalize(vertexData.ViewDir));
                    float spec = pow(max(dot(N, halfwayDir), 0f), shininess);
                    colorSpecular = specTexture * vec4(spotlightcolor[i], 1f) * spec;
                }
                else
                {
                    //Speculare Reflection
                    vec3 V = normalize(vertexData.ViewDir);
                    vec3 R = normalize(reflect(-L, N));//-L ist der zu reflektierende Vector
                    //N ist die Oberfläche, an der Reflektiert werden soll
                    float cosBeta = max(0.0f, dot(R, V));
                    float cosBetak = pow(cosBeta, shininess);// shininess = Glanzvariable =
                    colorSpecular = specTexture * cosBetak * vec4(spotlightcolor[i], 1.0f);
               }
                float intensity = 0;

                //                                      Richtung des Spotlight              //vector zwischen lichtquelle und vertex
                float costheta = max(0.0, dot(normalize(spotlightorien[i]), -normalize(vertexData.SpotLightDir[i])));
                float theta = acos(costheta);
                if (theta < innerCone[i])
                {
                    intensity = 1;
                }
                else if (theta < outerCone[i])
                {
                    intensity = (costheta-cos(outerCone[i])) / (cos(innerCone[i]) - cos(outerCone[i]));
                }
                color += (colorDiffuse + colorSpecular) * intensity;
            }
        }
    }

/*
float intensity = 0;

//                                      Richtung des Spotlight              //vector zwischen lichtquelle und vertex
float costheta = max(0.0, dot(normalize(spotlightorien[i]), -normalize(vertexData.SpotLightDir[i])));
         float theta = acos(costheta);
       if (theta < innerCone[i])
    {
        intensity = 1;
}
else if (theta < outerCone[i])
    {
        intensity = (costheta-cos(outerCone[i])) / (cos(innerCone[i]) - cos(outerCone[i]));
}
color += (colorDiffuse + colorSpecular) * intensity;
*/
    if(shader == 2 || shader == 3) // Toon-Shader - hat diskrete Übergänge, anders als bei diffusive Reflektion
    {
        float intensity;
        for(int i=0;i<5;i++)
        {
            //Pointlights
            if(pointLightIsEnabled[i] != 0.0f)
                {
            intensity += dot(normalize(vertexData.LightDir[i]),normalize(vertexData.normal));
         }

         if(spotLightIsEnabled[i] != 0.0f)
             {
                //Spotlight
                float costheta = max(0.0, dot(normalize(spotlightorien[i]), -normalize(vertexData.SpotLightDir[i])));
                float theta = acos(costheta);
                if (theta < innerCone[i])
                {
                    intensity = 1;
                }
                else if (theta < outerCone[i])
                {
                    intensity += (costheta-cos(outerCone[i])) / (cos(innerCone[i]) - cos(outerCone[i]));
                }
            }
        }
        if (intensity > 0.95)
        {color = color*2;}
        //color = vec4(1.0,0.5,0.5,1.0);
        else if (intensity > 0.5)
        {color = color;}
        //color = vec4(0.6,0.3,0.3,1.0);
        else if (intensity > 0.25)
        {color = color*0.5;}
        //color = vec4(0.4,0.2,0.2,1.0);
        else
        {color = color*0.2;}
        //color = vec4(0.2,0.1,0.1,1.0);
    }
    color = vec4(gamma(color.rgb), 1f);
    if(shader == 3)
    {
        //float intensity = dot(normalize(vertexData.ViewDir),normalize(vertexData.normal));
        //color = intensity * vec4(1f,0f,0f,0f);
        color =  ((color.r + color.g + color.b)/3) * vec4(1f,1f,1f,1f);
    }
    //color = vec4(gamma(color.rgb), 1f);
}