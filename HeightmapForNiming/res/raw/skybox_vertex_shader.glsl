uniform mat4 u_Matrix;
attribute vec3 a_Position;

varying vec3 v_Position;
void main(){
	v_Position = a_Position;
	v_Position.z = -v_Position.z;
	
	gl_Position = u_Matrix * vec4(a_Position, 1.0);
	gl_Position = gl_Position.xyww; 
	/*设置z分量与其w分量相等值，这一技巧，保证了天空盒的每一部分都将归一化设备坐标，及远平面在场景中其他一切后面*/
}