# version 120

attribute vec3 vertexPosition;
attribute vec2 vertexTexture;

varying vec2 tex_coords;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 tilePosition;

void main() {
    gl_Position = projectionMatrix * modelMatrix * tilePosition * viewMatrix * vec4(vertexPosition, 1);

    tex_coords = vertexTexture;
}