#version 330 core

// Vertex attributes
attribute vec4 vertexPosition;
attribute vec2 vertexTexture;
attribute vec3 vertexNormal;

// Transformation matrices
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 tilePosition;
uniform mat4 viewMatrix;

uniform mat4 modelTransform;
uniform int transform;

// Light positions
uniform vec3 lightPosition[10];

// To fragment shader
out vec3 surfaceNormal;
out vec2 textureCoords;
out vec3 surfacePos;
out vec3 toLightVector[10];

void main() {

    /* These calculations should be done on the CPU, and then send to the shader through uniforms... */
    mat4 modelToWorld;
    if (transform == 1) { modelToWorld = modelMatrix * viewMatrix * modelTransform; }
    else { modelToWorld = modelMatrix * tilePosition * viewMatrix; }

    vec4 vertexWorldPosition;
    if (transform == 1) { vertexWorldPosition = modelMatrix * modelTransform * vertexPosition; }
    else { vertexWorldPosition = modelMatrix * tilePosition * vertexPosition; }

    if (transform == 1) { surfaceNormal = normalize(modelToWorld * vec4(vertexNormal, 0.0)).xyz; }
    else { surfaceNormal = (modelMatrix * tilePosition * vec4(vertexNormal, 0.0)).xyz; }

    // Compute vectors pointing to the light sources
    for (int i = 0; i < 10; i++) {
        if (transform==1) {
            toLightVector[i] = vec3(modelMatrix * vec4(lightPosition[i], 1.0)) - (modelMatrix * modelTransform * vertexPosition).xyz;
        } else {
            toLightVector[i] = vec3(modelMatrix * vec4(lightPosition[i], 1.0)) - (modelMatrix * tilePosition * vertexPosition).xyz;
        }
    }

    // Compute the texture coordinates
    textureCoords = vertexTexture;

    // Set the position
    gl_Position = projectionMatrix * modelToWorld * vertexPosition;

}