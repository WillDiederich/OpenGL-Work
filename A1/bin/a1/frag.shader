#version 430
in vec4 initialColor;
out vec4 finalColor;
void main(void){
	gl_FragColor = initialColor;
}