#version 330 core

uniform sampler2D sampler;

uniform vec3 lightPosition;
uniform vec3 eyePosition;
uniform vec3 lightColor;
uniform vec3 lightAttenuation;

in vec3 position;
in vec3 normal;
in vec2 textureCoords;

vec4 purple = vec4(.2,.2,.6,1);

void main() {

    vec4 texture = texture2D(sampler, textureCoords);

    vec3 n = normalize(normal);
    vec3 l = normalize(lightPosition - position);
    vec3 e = normalize(position - eyePosition);
    vec3 r = reflect(l, n);

    float distance = length(lightPosition - position);
    float attFactor = lightAttenuation.x + lightAttenuation.y*distance + lightAttenuation.z*distance*distance;

    float ambient = 0.2;
    float diffuse = clamp(0.4 * max(dot(n,l), 0.0), 0.0, 1.0);
    float specular = clamp(0.4 * pow(max(dot(r,e), 0.0), 2), 0.0, 1.0);

    vec3 lightValue = (ambient + diffuse + specular) * lightColor;

    gl_FragColor = vec4(texture.rgb * lightValue, texture.a);

}