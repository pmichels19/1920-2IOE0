#version 330 core

// Texture map
uniform sampler2D diffuseMap;

// Normal map
uniform sampler2D normalMap;

// Whether normal mapping is enabled
uniform int normalMapping;

// Light attributes
uniform vec3 lightPosition[10];
uniform vec3 lightColor[10];
uniform vec3 lightAttenuation[10];

// From vertex shader
in vec3 surfaceNormal;
in vec2 textureCoords;
in vec3 toLightVector[10];
in vec3 toCameraVector;

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

    // Compute the diffuse and specular for every light source
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 10; i++) {
        vec3 unitLightVector = normalize(toLightVector[i]);
        vec3 unitVectorToCamera = normalize(toCameraVector);
        vec3 lightDirection = -unitLightVector;

        float distance = length(toLightVector[i]);
        float attFactor = lightAttenuation[i].x + lightAttenuation[i].y*distance + lightAttenuation[i].z*distance*distance;

        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);

        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specular = pow(max(dot(reflectedLightDirection, unitVectorToCamera), 0.0), 16);

        totalDiffuse += (brightness * lightColor[i])/attFactor;
        totalSpecular += (0.05 * specular * lightColor[i])/attFactor;
    }

    // Compute the final fragment color
    gl_FragColor = vec4(totalDiffuse * texture.rgb, texture.a) + vec4(totalSpecular, 0.0);


}

