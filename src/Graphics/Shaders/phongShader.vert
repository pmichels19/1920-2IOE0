#version 330 core

attribute vec4 vertexPosition;
attribute vec2 vertexTexture;
attribute vec3 vertexNormal;

uniform mat4 viewMatrix;
uniform mat4 tilePosition;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

out vec3 position;
out vec3 normal;
out vec2 textureCoords;

void main() {

    /* These calculations should be done on the CPU, and then send to the shader through uniforms... */
    mat4 modelToWorld = modelMatrix * tilePosition * viewMatrix;
    mat4 modelToScreen = projectionMatrix * modelToWorld;
    mat3 normalToWorld = mat3(transpose(inverse(modelToWorld)));

    position = (modelToWorld * vertexPosition).xyz;
    normal = normalize(normalToWorld * vertexNormal);
    textureCoords = vertexTexture;

    gl_Position = modelToScreen * vertexPosition;

}