uniform mat4 u_Matrix;
attribute vec4 a_Position;

attribute vec4 a_Color;
varying vec4 v_Color;

void main(){
	v_Color = a_Color;
	gl_Position = u_Matrix * a_Position;/*透视投影变换*/	
	gl_PointSize = 30.0;
}
