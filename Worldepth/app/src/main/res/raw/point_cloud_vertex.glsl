precision mediump float;

attribute vec4 a_Position;
attribute vec4 a_Color;
uniform mat4 u_MVP;
uniform float u_PointThickness;
varying vec4 v_Color;

void main() {
    gl_Position = u_MVP * a_Position;
    gl_PointSize = u_PointThickness;
    v_Color = a_Color;
}