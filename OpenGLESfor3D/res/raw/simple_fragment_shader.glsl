precision mediump float;

varying vec4 v_Color;

void main(){
/*我们要使用这个uniform设置将要绘制的东西的颜色*/
	gl_FragColor = v_Color;
}