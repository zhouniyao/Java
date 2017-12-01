#include <jni.h>
#include <com_example_vrndk01_JniClient.h>
JNIEXPORT jint JNICALL Java_com_example_vrndk01_JniClient_AddInt
  (JNIEnv *env, jclass, jint a, jint b){
	return a + b;
}
