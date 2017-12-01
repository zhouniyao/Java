precision mediump float;

varying vec4 v_Color;
uniform vec4 u_Color;

void main(){
/*我们要使用这个uniform设置将要绘制的东西的颜色*/
//	gl_FragColor = v_Color;
      
	gl_FragColor = u_Color;//为women更改
}