#version 330 core

attribute vec4 vertexPosition;
attribute vec2 vertexTexture;
attribute vec3 vertexNormal;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 tilePosition;

out vec2 textureCoords;
out float lightValue;

void main() {

    /* These calculations should be done on the CPU, and then send to the shader through uniforms... */
    mat4 modelToWorld = modelMatrix * tilePosition * viewMatrix;
    mat4 modelToScreen = projectionMatrix * modelToWorld;
    mat3 normalToWorld = mat3(transpose(inverse(modelToWorld)));

    vec4 lightPosition = vec4(4,4,1,0);

    vec4 p = modelToWorld * vertexPosition;
    vec4 n = vec4(normalize(normalToWorld * vertexNormal),1);
    vec4 l = normalize(lightPosition - p);

    float ambient = 0.2;
    float diffuse = 0.9 * clamp (0, dot(n, l), 1);
    lightValue = (ambient + diffuse);
    textureCoords = vertexTexture;


    gl_Position = modelToScreen * vertexPosition;

}