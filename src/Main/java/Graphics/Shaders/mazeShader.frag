#version 330 core

uniform sampler2D sampler;

uniform vec3 lightPosition[5];
uniform vec3 lightColor[5];
uniform vec3 lightAttenuation[5];

in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec2 textureCoords;

vec4 c = vec4(.9,.9,.9,1);

void main() {

    vec4 texture = texture2D(sampler, textureCoords);
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 totalDiffuse = vec3(0.0);


    for (int i = 0; i < 5; i++) {
        vec3 unitLightVector = normalize(toLightVector[i]);
        float distance = length(toLightVector[i]);
        float attFactor = lightAttenuation[i].x + lightAttenuation[i].y*distance + lightAttenuation[i].z*distance*distance;
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        totalDiffuse += (brightness * lightColor[i])/attFactor;
    }

    gl_FragColor = vec4(totalDiffuse * texture.rgb, texture.a);

}