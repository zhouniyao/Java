precision mediump float;

uniform samplerCube u_TextureUnit;
varying vec3 v_Position;
	    	   								
void main()                    		
{
	gl_FragColor = textureCube(u_TextureUnit, v_Position);    
	/*使用立方体纹理绘制这个天空盒，调用textureCube()，把那个被插值的立方体面的位置作为那个片段的纹理坐标*/
}

