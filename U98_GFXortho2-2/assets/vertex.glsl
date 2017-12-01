uniform mediump mat4 MODELVIEWPROJECTIONMATRIX; 
attribute mediump vec4 POSITION;/*定义一个变量来保存顶点着色器当前正在处理的顶点位置*/
attribute lowp vec4 COLOR;/*定义每个顶点颜色的变量*/
varying lowp vec4 color;/*中间变量，把颜色属性发送到片段着色器*/

void main(){
	gl_Position = MODEVIEWPROJECTIONMATRIX * POSITION;
	color = COLOR;
}