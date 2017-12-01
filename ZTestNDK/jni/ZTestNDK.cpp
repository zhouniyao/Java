#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <com_example_ztestndk_JniClient.h>

JNIEXPORT jint JNICALL Java_com_example_ztestndk_JniClient_getSum
  (JNIEnv *env, jclass, jint a, jint b){
	return (a+b);
}

JNIEXPORT jstring JNICALL Java_com_example_ztestndk_JniClient_getUrl
  (JNIEnv *env, jclass){
	return env->NewStringUTF("http://baidu.com");
}

void swap(int *a, int *b){
	*a += *b;
	*b = *a - *b;
	*a = *a - *b;
}

JNIEXPORT jintArray JNICALL Java_com_example_ztestndk_JniClient_sort
  (JNIEnv *env, jclass, jintArray arr, jint length){


	// 排序,成功
	int nLength = env->GetArrayLength(arr);
	int *pArr = env->GetIntArrayElements(arr, 0);

	for(jint i=0; i<nLength; i++){
		for(jint j=i+1; j<nLength; j++){
			if(*(pArr + i) > *(pArr + j)){
				swap(pArr + i, pArr + j);
			}
		}
	}//End outer for
	env->ReleaseIntArrayElements(arr,pArr,0);
	return arr;



	/*
	//整形数组操作示例,每个数组元素加上length值后，返回数组
	int nLength = env->GetArrayLength(arr);
	int *pArr = env->GetIntArrayElements(arr, 0);

	for(int i=0; i < nLength; i++){
		*(pArr + i) += length;
	}
	env->ReleaseIntArrayElements(arr,pArr,0);
	return arr;
	*/
}
