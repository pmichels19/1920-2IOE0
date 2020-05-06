# version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords;

uniform mat4 transformWorld;
uniform mat4 transformObject;
uniform mat4 cameraProjection;

void main() {
    gl_Position = cameraProjection * transformWorld * transformObject * vec4(vertices, 1);

    tex_coords = textures;
}