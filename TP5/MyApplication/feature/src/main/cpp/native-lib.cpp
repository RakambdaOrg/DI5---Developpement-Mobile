#include <jni.h>
#include <string>

jbyte getPix(jbyte* outarray, jint w, jint h, int x, int y){
    if(x < 0 || x >= w || y < 0 || y >= h){
        return 0;
    }
    return outarray[y * w + x];
}

jbyte floorMod(jbyte value, int mod){
    while(value < 0)
        value += mod;
    while(value >= mod)
        value -= mod;
    return value;
}

void setPix(jbyte* array, jint w, jint h, int x, int y, jbyte value){
    if(x < 0 || x >= w || y < 0 || y >= h){
        return;
    }
    array[y * w + x] = floorMod(value, 255);
}

jbyte absByte(jbyte value){
    if(value >= 0)
        return value;
    return static_cast<jbyte>(-1 * value);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_polytech_app_MainActivity_process(
        JNIEnv* env,
        jobject /* this */,
        jint w,
        jint h,
        jbyteArray dataIn,
        jbyteArray dataOut) {

    jbyte* _dataIn = env->GetByteArrayElements(dataIn, 0);
    jbyte* _dataOut = env->GetByteArrayElements(dataOut, 0);


    for(int y = 1; y < h -1; y++){
        for(int x = 1; x < w -1; x++){
            jbyte gh = absByte(getPix(_dataIn, w, h, x-1, y) - getPix(_dataIn, w, h, x+1, y));
            jbyte gv = absByte(getPix(_dataIn, w, h, x, y-1) - getPix(_dataIn, w, h, x, y+1));
            setPix(_dataOut, w, h, x, y, gh + gv);
        }
    }


    for(int x = 0; x < w; x+= w -1)
        for(int y = 0; y < h; y+= h -1)
            setPix(_dataOut, w,h, x, y,getPix(_dataIn, w, h, x, y));


    env->ReleaseByteArrayElements(dataIn, _dataIn, 0);
    env->ReleaseByteArrayElements(dataOut, _dataOut, 0);
}
