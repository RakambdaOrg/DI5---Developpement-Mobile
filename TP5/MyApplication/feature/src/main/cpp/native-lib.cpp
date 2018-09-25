#include <jni.h>
#include <string>

extern "C" JNIEXPORT void JNICALL
Java_com_example_polytech_app_MainActivity_process(
        JNIEnv* env,
        jobject /* this */,
        jint width,
        jint height,
        jbyteArray dataIn,
        jbyteArray dataOut) {

    jbyte* _dataIn = env->GetByteArrayElements(dataIn, 0);
    jbyte* _dataOut = env->GetByteArrayElements(dataOut, 0);

    env->ReleaseByteArrayElements(dataIn, _dataIn, 0);
    env->ReleaseByteArrayElements(dataOut, _dataOut, 0);
}
