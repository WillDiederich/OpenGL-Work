#version 430

void main(void)
{
	if (gl_VertexID == 0) gl_Position = vec4( 2.0, 0.0, 0.0, 1.0);
    else if (gl_VertexID == 1) gl_Position = vec4(-2.0, 0.0, 0.0, 1.0);
}