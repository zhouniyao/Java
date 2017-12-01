uniform mat4 u_Matrix;

/*对于我们定义过的每一个单一顶点，顶点着色器都会被调用一次；
* 当它被调用的时候，它会在a_Position属性里接收当前顶点位置，这个属性被定义成vec4类型。
*/
/*一个顶点有几个属性，例如位置和颜色，关键词“attribute”就是把这些属性放进着色器的手段。*/
attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

/*main()是着色器主要入口，它所做的就是把前面定义过的位置复制到指定的输出变量gl_Position；
* 这个着色器一定要给gl_Position赋值；OpenGL会把gl_Position中存储的值作为当前顶点的最终位置，
* 并把这些顶点组装成点、直线和三角形。
*/
void main(){
	v_Color = a_Color;
	gl_Position = u_Matrix * a_Position;/*透视投影变换*/	
	gl_PointSize = 25.0;
}
