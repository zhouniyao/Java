precision mediump float;

varying vec4 v_Color;
uniform vec4 u_Color;

void main(){
/*����Ҫʹ�����uniform���ý�Ҫ���ƵĶ�������ɫ*/
//	gl_FragColor = v_Color;
      
	gl_FragColor = u_Color;//Ϊwomen����
}