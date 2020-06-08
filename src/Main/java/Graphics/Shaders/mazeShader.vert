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
uniform vec3 lightPosition[17];

// To fragment shader
out vec3 surfaceNormal;
out vec2 textureCoords;
out vec3 toLightVector[17];
out vec3 toCameraVector;

void main() {

    /* These calculations should be done on the CPU, and then send to the shader through uniforms... */
    mat4 modelToWorld;
    if (transform == 1) { modelToWorld = modelMatrix * viewMatrix * modelTransform; }
    else { modelToWorld = modelMatrix * tilePosition * viewMatrix; }

    vec4 vertexWorldPosition = modelToWorld * vertexPosition;
    surfaceNormal = normalize(modelToWorld * vec4(vertexNormal, 0.0)).xyz;

    // Compute vectors pointing to the light sources
    for (int i = 0; i < 17; i++) {
        toLightVector[i] = vec3(modelMatrix * viewMatrix * vec4(lightPosition[i], 1.0)) - vertexWorldPosition.xyz;
    }

    // Compute vectors pointing to the camera
    toCameraVector = (inverse(viewMatrix) * vec4(0,0,0,1.0)).xyz - vertexWorldPosition.xyz;

    // Compute the texture coordinates
    textureCoords = vertexTexture;

    // Set the position
    gl_Position = projectionMatrix * modelToWorld * vertexPosition;

}