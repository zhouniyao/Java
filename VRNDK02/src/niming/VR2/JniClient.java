package niming.VR2;
/*
 * JNI的编写步骤
	
	a、编写带有native声明的方法的java类
	b、使用javac命令编译a中实现的类
	c、javah -jni java类名生成扩展名为h的头文件
	d、使用C/C++实现本地方法
	e、将d中的本地方法生成动态链接库
 */

public class JniClient {
	public static native int AddInt(int a, int b);
}
