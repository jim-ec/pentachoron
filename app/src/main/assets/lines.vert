#version 300 es

uniform mat4 P;
uniform mat4 V;

in vec4 position;
in vec4 color;

out vec4 vColor;

void main() {
    gl_Position = P * V * position;
    vColor = color;
}
