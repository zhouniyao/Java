precision mediump float;
uniform float uR;
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
varying vec4 vAmbient;//接收从顶点着色器过来的环境光分量
varying vec4 vDiffuse;//接收从顶点着色器过来的散射光分量
varying vec4 vSpecular;//接收从顶点着色器过来的镜面反射光分量
void main()                         
{

   vec3 color = vec3(1.0,1.0,1.0);//白色
   
   //最终颜色
   vec4 finalColor=vec4(color,0);
   //给此片元颜色值
   //gl_FragColor=finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
   gl_FragColor=finalColor;
}     