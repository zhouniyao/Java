precision mediump float;
uniform float uR;
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
varying vec4 vAmbient;//���մӶ�����ɫ�������Ļ��������
varying vec4 vDiffuse;//���մӶ�����ɫ��������ɢ������
varying vec4 vSpecular;//���մӶ�����ɫ�������ľ��淴������
void main()                         
{

   vec3 color = vec3(1.0,1.0,1.0);//��ɫ
   
   //������ɫ
   vec4 finalColor=vec4(color,0);
   //����ƬԪ��ɫֵ
   //gl_FragColor=finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
   gl_FragColor=finalColor;
}     