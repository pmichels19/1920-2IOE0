#version 330 core

uniform sampler2D sampler;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 lightAttenuation;

in vec3 surfaceNormal;
in vec3 toLightVector;
in vec2 textureCoords;

vec4 purple = vec4(.2,.2,.6,1);

void main() {

    vec4 texture = texture2D(sampler, textureCoords);

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float distance = length(toLightVector);
    float attFactor = lightAttenuation.x + lightAttenuation.y*distance + lightAttenuation.z*distance*distance;

    float nDotl = dot(unitNormal, unitLightVector);

    float brightness = max(nDotl, 0.0);
    vec3 diffuse = (brightness * lightColor)/attFactor;

    gl_FragColor = vec4(diffuse * texture.rgb, texture.a);

}