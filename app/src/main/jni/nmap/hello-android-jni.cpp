#include <jni.h>
#include <string>

JNIEXPORT jstring JNICALL

Java_su_sniff_cepter_View_InitActivity_getMsgFromJni(JNIEnv *env, jobject instance) {

   // TODO

   return (*env)->NewStringUTF(env, "JNI JACKPOT");
}