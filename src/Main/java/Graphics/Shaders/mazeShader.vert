#version 330 core

attribute vec4 vertexPosition;
attribute vec2 vertexTexture;
attribute vec3 vertexNormal;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 tilePosition;

uniform mat4 modelTransform;
uniform int transform;

uniform vec3 lightPosition[5];

out vec3 surfaceNormal;
out vec3 toLightVector[5];
out vec2 textureCoords;
out vec3 surfacePos;

void main() {

    /* These calculations should be done on the CPU, and then send to the shader through uniforms... */
    mat4 modelToWorld;
    if (transform == 1) { modelToWorld = modelMatrix * viewMatrix * modelTransform; }
    else { modelToWorld = modelMatrix * tilePosition * viewMatrix; }

    mat4 modelToScreen = projectionMatrix * modelToWorld;
    mat3 normalToWorld = mat3(transpose(inverse(modelToWorld)));

    vec4 vertexWorldPosition;
    if (transform == 1) { vertexWorldPosition = modelMatrix * modelTransform * vertexPosition; }
    else { vertexWorldPosition = modelMatrix * tilePosition * vertexPosition; }

    if (transform == 1) { surfaceNormal = normalize(modelToWorld * vec4(vertexNormal, 0.0)).xyz; }
    else { surfaceNormal = (modelMatrix * tilePosition * vec4(vertexNormal, 0.0)).xyz; }

    for (int i = 0; i < 5; i++) {
        toLightVector[i] = vec3(modelMatrix * vec4(lightPosition[i], 1.0)) - vertexWorldPosition.xyz;
    }

    textureCoords = vertexTexture;

    gl_Position = modelToScreen * vertexPosition;

}