precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main(){
	/*
	* 为了把纹理绘制到一个物体上，OpenGL会为每个片段都调用片段着色器，并且每个调用都接收v_TextureCoordinates的纹理坐标。
	* 片段着色器也通过uniform——u_TextureUnit接收实际的纹理数据，u_TextureUnit被定义为一个sampler2D，这个变量类型指的是一个二维纹理数据的数组。
	*/
	/*
	* 被插值的纹理坐标和纹理数据被传递给着色器函数texture2D()，它会读入纹理中那个特定坐标处的颜色值。接着通过把结果赋值给gl_FragColor设置片段颜色。
	*/
	gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}