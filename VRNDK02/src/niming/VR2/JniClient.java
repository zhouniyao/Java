package niming.VR2;
/*
 * JNI�ı�д����
	
	a����д����native�����ķ�����java��
	b��ʹ��javac�������a��ʵ�ֵ���
	c��javah -jni java����������չ��Ϊh��ͷ�ļ�
	d��ʹ��C/C++ʵ�ֱ��ط���
	e����d�еı��ط������ɶ�̬���ӿ�
 */

public class JniClient {
	public static native int AddInt(int a, int b);
}
