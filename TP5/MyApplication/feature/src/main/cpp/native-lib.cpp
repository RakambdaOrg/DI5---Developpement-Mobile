#include <jni.h>
#include <string>

int getPix(jbyte* outarray, jint w, jint h, int x, int y){
    if(x < 0 || x >= w || y < 0 || y >= h){
        return 0;
    }
    return outarray[y * w + x];
}

jbyte floorMod(int value, int mod){
    while(value < 0)
        value += mod;
    while(value >= mod)
        value -= mod;
    return static_cast<jbyte>(value);
}

void setPix(jbyte* array, jint w, jint h, int x, int y, int value){
    if(x < 0 || x >= w || y < 0 || y >= h){
        return;
    }
    array[y * w + x] = floorMod(value, 255);
}

void gauss(jbyte *_dataIn, jbyte *_dataOut, jint w, jint h) {
    for(int y = 1; y < h -1; y++){
        for(int x = 1; x < w -1; x++){
            int gh = abs(getPix(_dataIn, w, h, x-1, y) - getPix(_dataIn, w, h, x+1, y));
            int gv = abs(getPix(_dataIn, w, h, x, y-1) - getPix(_dataIn, w, h, x, y+1));
            setPix(_dataOut, w, h, x, y, gh + gv);
        }
    }
}

int applyConvFilterAt(jbyte* outarray, jdouble *convol, int x, int y, int w, int h, int cSize) {
    int xcenter = cSize/2;
    int ycenter = cSize/2;

    int size = cSize*cSize;
    double sum = 0;
    for(int xc = 0; xc < cSize; xc++){
        for (int yc = 0; yc < cSize; yc++) {
            if(x + xc - xcenter < 0 || y+yc-ycenter < 0 || x + xc - xcenter >= w || y+yc-ycenter >= h)
                continue;
            sum += convol[xc * cSize + yc] * getPix(outarray, w, h, x - xcenter + xc, y - ycenter + yc);
        }
    }

    return (int) (sum / (size == 0 ? 1 : size));
}

void sobel(jbyte *_dataIn, jbyte *_dataOut, jdouble* convol, jint cSize, jint w, jint h) {

//        int min = Integer.MAX_VALUE;
//        double max = Integer.MIN_VALUE;
    for(int y = 1; y < h -1; y++){
        for(int x = 1; x < w -1; x++){
            int pix = applyConvFilterAt(_dataIn, convol, x, y, w, h, cSize);
//                min = Math.min(min, pix);
//                max = Math.max(max, pix);
            setPix(_dataOut, w, h, x, y, pix);
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_polytech_app_MainActivity_gauss(
        JNIEnv* env,
        jobject /* this */,
        jint w,
        jint h,
        jbyteArray dataIn,
        jbyteArray dataOut) {

    jbyte* _dataIn = env->GetByteArrayElements(dataIn, 0);
    jbyte* _dataOut = env->GetByteArrayElements(dataOut, 0);

    gauss(_dataIn, _dataOut, w, h);

    env->ReleaseByteArrayElements(dataIn, _dataIn, 0);
    env->ReleaseByteArrayElements(dataOut, _dataOut, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_polytech_app_MainActivity_sobel(
        JNIEnv* env,
        jobject /* this */,
        jint w,
        jint h,
        jbyteArray dataIn,
        jbyteArray dataOut,
        jdoubleArray convol,
        jint cSize) {

    jbyte* _dataIn = env->GetByteArrayElements(dataIn, 0);
    jbyte* _dataOut = env->GetByteArrayElements(dataOut, 0);
    jdouble * _convol = env->GetDoubleArrayElements(convol, 0);

    sobel(_dataIn, _dataOut, _convol, cSize, w, h);

    env->ReleaseByteArrayElements(dataIn, _dataIn, 0);
    env->ReleaseByteArrayElements(dataOut, _dataOut, 0);
}
