#version 300 es

uniform mat4 P;
uniform mat4 V;
uniform mat4 M[100];

in vec4 position;
in vec4 color;
in int modelIndex;

out vec4 vColor;

void main() {
    gl_Position = P * V * M[modelIndex] * position;
    vColor = color;
}
