#version 120

attribute vec3 a_position;
uniform mat4 u_projection;

varying vec3 texCoord;

void main() {
    texCoord = a_position;
    vec4 pos = u_projection * vec4(a_position, 1.0);
    gl_Position = pos.xyww;
}
