uniform mat4 u_Matrix;
uniform vec4 u_Color; /*��ʱ*/
attribute vec4 a_Position;

attribute vec4 a_Color;
varying vec4 v_Color;

void main(){
	v_Color = u_Color;
	gl_Position = u_Matrix * a_Position;/*͸��ͶӰ�任*/	
	gl_PointSize = 30.0;
}
