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

// Light positions
uniform vec3 lightPosition[5];

// To fragment shader
out vec3 surfaceNormal;
out vec2 textureCoords;
out vec3 toLightVector[5];

void main() {

    // Compute seperately for efficiency
    mat4 tileModelMatrix = modelMatrix * tilePosition;

    // Compute vectors pointing to the light sources
    for (int i = 0; i < 5; i++) {
        toLightVector[i] = vec3(modelMatrix * vec4(lightPosition[i], 1.0)) - (tileModelMatrix * vertexPosition).xyz;
    }

    // Compute the surface normal
    surfaceNormal = (tileModelMatrix * vec4(vertexNormal, 0.0)).xyz;

    // Compute the texture coordinates
    textureCoords = vertexTexture;

    // Set the position
    gl_Position = projectionMatrix * tileModelMatrix * viewMatrix * vertexPosition;

}