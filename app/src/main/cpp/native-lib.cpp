
#include <jni.h>

JNIEXPORT jstring JNICALL
Java_su_sniff_cepter_View_InitActivity_stringFromJNI(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("FROM JNI YEAHHHH");
}