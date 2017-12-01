precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main(){
	/*
	* Ϊ�˰�������Ƶ�һ�������ϣ�OpenGL��Ϊÿ��Ƭ�ζ�����Ƭ����ɫ��������ÿ�����ö�����v_TextureCoordinates���������ꡣ
	* Ƭ����ɫ��Ҳͨ��uniform����u_TextureUnit����ʵ�ʵ��������ݣ�u_TextureUnit������Ϊһ��sampler2D�������������ָ����һ����ά�������ݵ����顣
	*/
	/*
	* ����ֵ������������������ݱ����ݸ���ɫ������texture2D()����������������Ǹ��ض����괦����ɫֵ������ͨ���ѽ����ֵ��gl_FragColor����Ƭ����ɫ��
	*/
	gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}