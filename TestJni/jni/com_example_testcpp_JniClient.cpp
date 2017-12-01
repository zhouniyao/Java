#include "com_example_testcpp_JniClient.h"
#include <stdlib.h>
#include <stdio.h>

#ifdef __cplusplus   
extern "C"  
{   
#endif  

class CClass
{
public:
	int Add(int a,int b);
};
int CClass::Add(int a,int b)
{
	return a+b;
}

/*
* Class:   
* Method:    AddInt
* Signature: (II)I
*/
JNIEXPORT jint JNICALL Java_com_example_testcpp_JniClient_AddInt

  (JNIEnv *env, jclass arg, jint a, jint b)
{
    CClass *Class=new CClass;
	int aa=Class->Add(a,b);
    return aa;
	
	 //Ëã·¨
//    CSm3otp *Sm3otp=new CSm3otp;
	//Sm3otp->hotpWithKey(T0 ,K ,codeDigits);
    //return atoi(Sm3otp.computePassword()); 
}
#ifdef __cplusplus   
}   
#endif

