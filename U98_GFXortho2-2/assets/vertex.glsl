uniform mediump mat4 MODELVIEWPROJECTIONMATRIX; 
attribute mediump vec4 POSITION;/*����һ�����������涥����ɫ����ǰ���ڴ���Ķ���λ��*/
attribute lowp vec4 COLOR;/*����ÿ��������ɫ�ı���*/
varying lowp vec4 color;/*�м����������ɫ���Է��͵�Ƭ����ɫ��*/

void main(){
	gl_Position = MODEVIEWPROJECTIONMATRIX * POSITION;
	color = COLOR;
}