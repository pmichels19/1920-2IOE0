#version 330 core

// Texture map
uniform sampler2D diffuseMap;

// Normal map
uniform sampler2D normalMap;

// Whether normal mapping is enabled
uniform int normalMapping;

// Light attributes
uniform vec3 lightPosition[5];
uniform vec3 lightColor[5];
uniform vec3 lightAttenuation[5];

// From vertex shader
in vec3 surfaceNormal;
in vec2 textureCoords;
in vec3 toLightVector[5];

void main() {

    // Sample the texture from the texture map
    vec4 texture = texture2D(diffuseMap, textureCoords);

    // Compute normal from the normal map if normal mapping is enabled
    // Else, use the surface normal from the vertex shader
    vec3 unitNormal = vec3(0.0);
    if (normalMapping == 1) {
        unitNormal = texture2D(normalMap, textureCoords).rgb;
        unitNormal = unitNormal*2-1;
        unitNormal = normalize(unitNormal);
    } else {
        unitNormal = normalize(surfaceNormal);
    }

    // Compute the diffuse for every light source
    vec3 totalDiffuse = vec3(0.0);
    for (int i = 0; i < 5; i++) {
        vec3 unitLightVector = normalize(toLightVector[i]);
        float distance = length(toLightVector[i]);
        float attFactor = lightAttenuation[i].x + lightAttenuation[i].y*distance + lightAttenuation[i].z*distance*distance;
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        totalDiffuse += (brightness * lightColor[i])/attFactor;
    }

    // Compute the final fragment color
    gl_FragColor = vec4(totalDiffuse * texture.rgb, texture.a);

}