
precision mediump float;           // Set the default precision to medium. We don't need as high of a 
                                // precision in the fragment shader.
uniform sampler2D u_TextureUnit;    // The input texture.
  
varying vec4 v_Color;              // This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  	/*
	* 为了把纹理绘制到一个物体上，OpenGL会为每个片段都调用片段着色器，并且每个调用都接收v_TextureCoordinates的纹理坐标。
	* 片段着色器也通过sampler2D u_TextureUnit接收实际的纹理数据，u_TextureUnit被定义为一个sampler2D，这个变量类型指的是一个二维纹理数据的数组。其专门用来进行纹理采样，一个采样器变量代表一副或一套纹理贴图。
	* 
	*/
	/*
	* 被插值的纹理坐标和纹理数据被传递给着色器函数texture2D()，它会读入纹理中那个特定坐标处的颜色值。接着通过把结果赋值给gl_FragColor设置片段颜色。
	*/
// The entry point for our fragment shader.
void main()                            
{                              
   // Multiply the color by the diffuse illumination level and texture value to get final output color.
   gl_FragColor = texture2D(u_TextureUnit, v_TexCoordinate);       
   //gl_FragColor = v_Color;                             
}