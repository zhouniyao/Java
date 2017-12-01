
precision mediump float;           // Set the default precision to medium. We don't need as high of a 
                                // precision in the fragment shader.
uniform sampler2D u_TextureUnit;    // The input texture.
  
varying vec4 v_Color;              // This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  	/*
	* Ϊ�˰�������Ƶ�һ�������ϣ�OpenGL��Ϊÿ��Ƭ�ζ�����Ƭ����ɫ��������ÿ�����ö�����v_TextureCoordinates���������ꡣ
	* Ƭ����ɫ��Ҳͨ��sampler2D u_TextureUnit����ʵ�ʵ��������ݣ�u_TextureUnit������Ϊһ��sampler2D�������������ָ����һ����ά�������ݵ����顣��ר�������������������һ����������������һ����һ��������ͼ��
	* 
	*/
	/*
	* ����ֵ������������������ݱ����ݸ���ɫ������texture2D()����������������Ǹ��ض����괦����ɫֵ������ͨ���ѽ����ֵ��gl_FragColor����Ƭ����ɫ��
	*/
// The entry point for our fragment shader.
void main()                            
{                              
   // Multiply the color by the diffuse illumination level and texture value to get final output color.
   gl_FragColor = texture2D(u_TextureUnit, v_TexCoordinate);       
   //gl_FragColor = v_Color;                             
}