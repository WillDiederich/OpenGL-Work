#version 430

uniform float x;
uniform float y;
uniform float color;
uniform float size;
out vec4 initialColor;

void main(void){ 
	if (gl_VertexID == 0) {
		gl_Position = vec4((0.25 + x) * size, (-0.25 + y) * size, 0.0, 1);
		if(color != 0)
			initialColor = vec4(color, 0.0, 0.0, 1.0);
		else
			initialColor = vec4(0.0, 0.0, 1.0, 1.0);
	}
	else if (gl_VertexID == 1) {
		gl_Position = vec4((-0.25 + x) * size, (-0.25 + y) * size, 0.0, 1);
		if(color != 0)
			initialColor = vec4(0.0, color, 0.0, 1.0);
		else
			initialColor = vec4(0.0, 0.0, 1.0, 1.0);
	}
	else {
		gl_Position = vec4((0.0 + x) * size,  (0.25 + y) * size, 0.0, 1);
		initialColor = vec4(0.0, 0.0, 1.0, 1.0);	
	}
}