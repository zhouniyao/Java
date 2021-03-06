//#include <stdio.h>
//#include <stdlib.h>
//#include <jni.h>
//#include <com_niming_vrndk03_JniClient.h>
//
//#define CB_CLASS "VRndk03"
//
///**
// * OnMessage 回调方法
// */
//#define CB_CLASS_MSG_CB "OnMessage"
//#define CB_CLASS_MSG_SIG "([Ljava/lang/String;)I"
//
///*原型声明*/
//int lib_main(int argc, char **argv);//原生库主要子例程
//const int getArrayLen(JNIEnv *env, jobjectArray jarray);//用于获得Java数组的长度
//void jni_printf(char *format, ...);// printf str messages back to java
//// Global env ref (for callbacks)
//static JavaVM *g_VM;//Java虚拟机的全局环境引用，用于回调Java方法
//// Global Reference to the native Java class jni.Natives.java
//static jclass jNativesCls;//原生Java类JniClient.java的全局引用。
//
///*
// * Class:     com_niming_vrndk03_JniClient
// * Method:    LibMain
// * Signature: ([Ljava/lang/String;)I
// */
//JNIEXPORT jint JNICALL Java_com_niming_vrndk03_JniClient_LibMain
//  (JNIEnv *env, jclass class, jobjectArray jargv){
//	//这即是java层的JniClient。java方法的原生实现，将java字符串数组转换为C 数组。
//
////	//从调用这jclass 获得全局引用
////	(*env)->GetJavaVM(env, &g_VM);
////	//从Java数组提取char **args
////	jsize clen = getArrayLen(env, jargv);//获得数组长度信息
////	char * args[(int)clen];//指针数组，每个元素是一个指针
////	int i;
////	jstring jrow;
////	for(i=0; i<clen; i++){
////		//将java字符串数组转换成C数组
//////		jrow = (jstring)(*env)->GetObjectArrayElement(env, jargv, i);//得到java数组String[i]元素
////		jrow = (jstring)(env)->GetObjectArrayElement(env, jargv, i);//得到java数组String[i]元素
////		const char *row = (*env)->GetStringUTFChars(env, jrow, 0);//将检索的元素转换成C 字符串char *
////		args[i] = malloc(strlen(row) + 1);//为C的char *分配空间，额外的空间放置"/0"
////		strcpy(args[i], row);//strcpy(char **target, char *source)
////		//打印args参数对应的字符串
////		jni_printf("Main argv[%d]=%s", i, args[i]);
////		//释放Java字符串jrow
////		(*env)->ReleaseStringUTFChars(env, jrow, row);
////	}
////	/*
////	 * 加载 JniClient类
////	 */
////	jNativesCls = (*env)->FindClass(env, CB_CLASS);
////
////	if(jNativesCls == 0){
////		jni_printf("Unable to find class: %s", CB_CLASS);
////		return -1;
////	}
////	//调用本地库主要子例程，以从Java层传递的字符串作为程序参数
////	lib_main(clen, args);
////	return 0;
//
//
//	// obtain a global ref to the caller jclass
//		(*env)->GetJavaVM(env, &g_VM);
//
//		// Extract char ** args from Java array
//		jsize clen =  getArrayLen(env, jargv);
//		char * args[(int)clen];
//
//		int i;
//		jstring jrow;
//		for (i = 0; i < clen; i++)
//		{
//			// Get C string from Java Strin[i]
//			jrow = (jstring)(*env)->GetObjectArrayElement(env, jargv, i);
//			const char *row  = (*env)->GetStringUTFChars(env, jrow, 0);
//
//			args[i] = malloc( strlen(row) + 1);
//			strcpy (args[i], row);
//
//			// print args
//			jni_printf("Main argv[%d]=%s", i, args[i]);
//
//			// free java string jrow
//			(*env)->ReleaseStringUTFChars(env, jrow, row);
//		}
//
//		/*
//		 * Load the jni.Natives class
//		 */
//		jNativesCls = (*env)->FindClass(env, CB_CLASS);
//
//		if ( jNativesCls == 0 ) {
//			jni_printf("Unable to find class: %s", CB_CLASS);
//			return -1;
//		}
//
//		// Invoke the Lib main sub. This will loop forever
//		// Program args come from Java
//		lib_main (clen, args);
//
//		return 0;
//
//}
//
///**
// * 将字符串发送回Java
// */
//jmethodID mSendStr;
//static void jni_send_str(const char *text, int level){
//	JNIEnv *env;
//	if(!g_VM){ //*g_VM;//Java虚拟机的全局环境引用
//		printf("I_JNI-NOVM: %s\n", text);
//		return;
//	}
//	(*g_VM)->AttachCurrentThread(g_VM, (void **) &env, NULL);
//	//如果为空，就加载JniClient类
//	if(!jNativesCls){
//		jNativesCls = (*env)->FindClass(env, CB_CLASS);
//		if(jNativesCls == 0){
//			printf("Unable to find class: %s", CB_CLASS);
//			return;
//		}
//	}
//	//调用JniClient.OnMessage(String, int)方法
//	if(!mSendStr){
//		//获得静态方法jni.Natives.OnMessage的引用
//		mSendStr = (*env)->GetStaticMethodID(env, jNativesCls
//											, CB_CLASS_MSG_CB
//											, CB_CLASS_MSG_SIG);
//	}
//	if(mSendStr){
//		//调用方法
//		(*env)->CallStaticVoidMethod(env, jNativesCls
//									, mSendStr
//									, (*env)->NewStringUTF(env, text)
//									, (jint)level);
//	}else{
//		printf("Unable to find method: %s, signature: %s\n"
//				, CB_CLASS_MSG_CB, CB_CLASS_MSG_SIG);
//	}
//}
//
///**
// * 该函数用于从原生库中向Java层发送文本消息， 打印到Java层
// * 将可变参数缓存到临时缓存区
// * 并调用jni_sebd_str
// */
//void jni_printf(char *format, ...){
//	va_list argptr;
//	static char string[1024];
//	va_start(argptr, format);
//	vsprintf(string, format, argptr);
//	va_end(argptr);
//	jni_send_str(string, 0);
//}
///**
// * 获得Java数组的长度
// */
//const int getArrayLen(JNIEnv *env, jobjectArray jarray){
////	return (*env)->GetArrayLength(env, jarray);
//	return env->GetArrayLength(jarray);
//}
///**
// * 原生库主要子例程
// */
//int lib_main(int argc, char **argv){
//	int i;
//	jni_printf("Entering LIB MAIN");
//	for(i=0; i<argc; i++){
//		jni_printf("Lib Main argv[%d] = %s ", i, argv[i]);
//	}
//	return 0;
//}
//
//
////void swap(int *a, int *b){
////    *a += *b;
////    *b = *a - *b;
////    *a = *a - *b;
////}
//JNIEXPORT jintArray JNICALL Java_com_niming_vrndk03_JniClient_useArray
//(JNIEnv *env, jclass jNativesCls, jintArray arr, jint length){
//////    //整形数组操作示例,每个数组元素加上length值后，返回数组
//////    int nLength = env->GetArrayLength(arr);
//////    int *pArr = env->GetIntArrayElements(arr, 0);
//////
//////    for(int i=0; i < nLength; i++){
//////        *(pArr + i) += length;
//////    }
//////    env->ReleaseIntArrayElements(arr,pArr,0);
//////    return arr;
////
////
////
////    // 选择排序,成功
////    int nLength = env->GetArrayLength(arr);
////    int *pArr = env->GetIntArrayElements(arr, 0);
////
////    for(jint i=0; i<nLength; i++){
////        for(jint j=i+1; j<nLength; j++){
////            if(*(pArr + i) > *(pArr + j)){
////                swap(pArr + i, pArr + j);
////            }
////        }
////    }//End outer for
////    env->ReleaseIntArrayElements(arr,pArr,0);
////    return arr;
//}
