//#include <stdio.h>
//#include <stdlib.h>
//#include <jni.h>
//#include <com_niming_vrndk03_JniClient.h>
//
//#define CB_CLASS "VRndk03"
//
///**
// * OnMessage �ص�����
// */
//#define CB_CLASS_MSG_CB "OnMessage"
//#define CB_CLASS_MSG_SIG "([Ljava/lang/String;)I"
//
///*ԭ������*/
//int lib_main(int argc, char **argv);//ԭ������Ҫ������
//const int getArrayLen(JNIEnv *env, jobjectArray jarray);//���ڻ��Java����ĳ���
//void jni_printf(char *format, ...);// printf str messages back to java
//// Global env ref (for callbacks)
//static JavaVM *g_VM;//Java�������ȫ�ֻ������ã����ڻص�Java����
//// Global Reference to the native Java class jni.Natives.java
//static jclass jNativesCls;//ԭ��Java��JniClient.java��ȫ�����á�
//
///*
// * Class:     com_niming_vrndk03_JniClient
// * Method:    LibMain
// * Signature: ([Ljava/lang/String;)I
// */
//JNIEXPORT jint JNICALL Java_com_niming_vrndk03_JniClient_LibMain
//  (JNIEnv *env, jclass class, jobjectArray jargv){
//	//�⼴��java���JniClient��java������ԭ��ʵ�֣���java�ַ�������ת��ΪC ���顣
//
////	//�ӵ�����jclass ���ȫ������
////	(*env)->GetJavaVM(env, &g_VM);
////	//��Java������ȡchar **args
////	jsize clen = getArrayLen(env, jargv);//������鳤����Ϣ
////	char * args[(int)clen];//ָ�����飬ÿ��Ԫ����һ��ָ��
////	int i;
////	jstring jrow;
////	for(i=0; i<clen; i++){
////		//��java�ַ�������ת����C����
//////		jrow = (jstring)(*env)->GetObjectArrayElement(env, jargv, i);//�õ�java����String[i]Ԫ��
////		jrow = (jstring)(env)->GetObjectArrayElement(env, jargv, i);//�õ�java����String[i]Ԫ��
////		const char *row = (*env)->GetStringUTFChars(env, jrow, 0);//��������Ԫ��ת����C �ַ���char *
////		args[i] = malloc(strlen(row) + 1);//ΪC��char *����ռ䣬����Ŀռ����"/0"
////		strcpy(args[i], row);//strcpy(char **target, char *source)
////		//��ӡargs������Ӧ���ַ���
////		jni_printf("Main argv[%d]=%s", i, args[i]);
////		//�ͷ�Java�ַ���jrow
////		(*env)->ReleaseStringUTFChars(env, jrow, row);
////	}
////	/*
////	 * ���� JniClient��
////	 */
////	jNativesCls = (*env)->FindClass(env, CB_CLASS);
////
////	if(jNativesCls == 0){
////		jni_printf("Unable to find class: %s", CB_CLASS);
////		return -1;
////	}
////	//���ñ��ؿ���Ҫ�����̣��Դ�Java�㴫�ݵ��ַ�����Ϊ�������
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
// * ���ַ������ͻ�Java
// */
//jmethodID mSendStr;
//static void jni_send_str(const char *text, int level){
//	JNIEnv *env;
//	if(!g_VM){ //*g_VM;//Java�������ȫ�ֻ�������
//		printf("I_JNI-NOVM: %s\n", text);
//		return;
//	}
//	(*g_VM)->AttachCurrentThread(g_VM, (void **) &env, NULL);
//	//���Ϊ�գ��ͼ���JniClient��
//	if(!jNativesCls){
//		jNativesCls = (*env)->FindClass(env, CB_CLASS);
//		if(jNativesCls == 0){
//			printf("Unable to find class: %s", CB_CLASS);
//			return;
//		}
//	}
//	//����JniClient.OnMessage(String, int)����
//	if(!mSendStr){
//		//��þ�̬����jni.Natives.OnMessage������
//		mSendStr = (*env)->GetStaticMethodID(env, jNativesCls
//											, CB_CLASS_MSG_CB
//											, CB_CLASS_MSG_SIG);
//	}
//	if(mSendStr){
//		//���÷���
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
// * �ú������ڴ�ԭ��������Java�㷢���ı���Ϣ�� ��ӡ��Java��
// * ���ɱ�������浽��ʱ������
// * ������jni_sebd_str
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
// * ���Java����ĳ���
// */
//const int getArrayLen(JNIEnv *env, jobjectArray jarray){
////	return (*env)->GetArrayLength(env, jarray);
//	return env->GetArrayLength(jarray);
//}
///**
// * ԭ������Ҫ������
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
//////    //�����������ʾ��,ÿ������Ԫ�ؼ���lengthֵ�󣬷�������
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
////    // ѡ������,�ɹ�
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