#version 330 core

uniform sampler2D sampler;

in vec2 textureCoords;
in float lightValue;

void main() {

    vec4 texture = texture2D(sampler, textureCoords);
    vec4 color = vec4(.2,.2,.6,1);
    vec4 fragmentColor = vec4(texture.rgb * lightValue, texture.a);

    gl_FragColor = fragmentColor;

}