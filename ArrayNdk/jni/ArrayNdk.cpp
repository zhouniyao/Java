#include <jni.h>
#include <com_example_arrayndk_JniClient.h>

JNIEXPORT jintArray JNICALL Java_com_example_arrayndk_JniClient_useArray
  (JNIEnv *env, jclass, jintArray arr, jint length){
	//�����������ʾ��,ÿ������Ԫ�ؼ���lengthֵ�󣬷�������
	int nLength = env->GetArrayLength(arr);
	int *pArr = env->GetIntArrayElements(arr, 0);

	for(int i=0; i < nLength; i++){
		*(pArr + i) += length;
	}
	env->ReleaseIntArrayElements(arr,pArr,0);
	return arr;
}