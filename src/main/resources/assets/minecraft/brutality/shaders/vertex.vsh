#version 120

varying vec2 texCoord0;

void main() {
    // ShaderUtils draws with glVertex/glTexCoord, not generic attribute arrays.
    // Generic attribute locations vary by driver and may receive no geometry.
    texCoord0 = gl_MultiTexCoord0.st;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
