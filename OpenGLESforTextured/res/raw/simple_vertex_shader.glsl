uniform mat4 u_Matrix;

/*�������Ƕ������ÿһ����һ���㣬������ɫ�����ᱻ����һ�Σ�
* ���������õ�ʱ��������a_Position��������յ�ǰ����λ�ã�������Ա������vec4���͡�
*/
/*һ�������м������ԣ�����λ�ú���ɫ���ؼ��ʡ�attribute�����ǰ���Щ���ԷŽ���ɫ�����ֶΡ�*/
attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

/*main()����ɫ����Ҫ��ڣ��������ľ��ǰ�ǰ�涨�����λ�ø��Ƶ�ָ�����������gl_Position��
* �����ɫ��һ��Ҫ��gl_Position��ֵ��OpenGL���gl_Position�д洢��ֵ��Ϊ��ǰ���������λ�ã�
* ������Щ������װ�ɵ㡢ֱ�ߺ������Ρ�
*/
void main(){
	v_Color = a_Color;
	gl_Position = u_Matrix * a_Position;/*͸��ͶӰ�任*/	
	gl_PointSize = 25.0;
}
