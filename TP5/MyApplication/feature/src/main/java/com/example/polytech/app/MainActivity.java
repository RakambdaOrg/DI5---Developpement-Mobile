package com.example.polytech.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.example.polytech.app.R;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    public native void gauss(int width, int height, byte input[], byte output[]);
    public native void sobel(int width, int height, byte input[], byte output[], double[] convol, int cSize);

    static {
        System.loadLibrary("native-lib");
    }

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private byte[] outarray;
    private byte[] outarray2;
    double filter[][] = new double[][]{{
            -1,0,1
    },{
            -1,0,1
    },{
            -1,0,1
    }};

    private int w;

    private int h;

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        outarray = new byte[width*height];
        outarray2 = new byte[width*height];
        w=width;
        h=height;
    }

    public void onCameraViewStopped() {
    }

    public int getPix(byte[] outarray, int x, int y){
        if(x < 0 || x >= w || y < 0 || y >= h){
            Log.w(TAG, "Getting pixel out of range: " + x + "/" + y);
            return 0;
        }
        return outarray[y * w + x];
    }

    public void setPix(byte[] array, int x, int y, int pix){
        if(x < 0 || x >= w || y < 0 || y >= h){
            Log.w(TAG, "Setting pixel out of range: " + x + "/" + y + "/");
            return;
        }
        array[y * w + x] = (byte)(pix %255);
    }

    public static int floorMod(int x, int y) {
        return x - floorDiv(x, y) * y;
    }

    public static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    public void gradient(byte[] array){
        for(int y = 1; y < h -1; y++){
            for(int x = 1; x < w -1; x++){
                int gh = Math.abs(getPix(array, x-1, y) - getPix(array, x+1, y));
                int gv = Math.abs(getPix(array, x, y-1) - getPix(array, x, y+1));
                setPix(outarray2, x, y,gh + gv);
            }
        }
    }

    public void sobel(byte[] array, double[][] convol){
        if(convol.length == 0 || convol[0].length == 0 || convol.length > w || convol[0].length > h || convol.length%2 == 0 || convol[0].length%2 == 0){
            Log.w(TAG, "Convolution array not good !!!");
            return;
        }

//        int min = Integer.MAX_VALUE;
//        double max = Integer.MIN_VALUE;
        for(int y = 1; y < h -1; y++){
            for(int x = 1; x < w -1; x++){
                int pix = applyConvFilterAt(outarray, convol, x, y);
//                min = Math.min(min, pix);
//                max = Math.max(max, pix);
                setPix(outarray2, x, y, pix);
            }
        }
    }

    private int applyConvFilterAt(byte[] outarray, double[][] convol, int x, int y) {
        int xcenter = convol.length/2;
        int ycenter = convol[0].length/2;

        double sumConv = 0;
        for(double[] i : convol)
            for(double ignored : i)
                sumConv++;

        int sum = 0;
        for(int xc = 0; xc < convol.length; xc++){
            for (int yc = 0; yc < convol[0].length; yc++) {
                if(x + xc - xcenter < 0 || y+yc-ycenter < 0 || x + xc - xcenter >= w || y+yc-ycenter >= h)
                    continue;
                sum += convol[xc][yc] * getPix(outarray, x - xcenter + xc, y - ycenter + yc);
            }
        }

        return (int) (sum / (sumConv == 0 ? 1 : sumConv));
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat gray =inputFrame.gray();
        MatToArray(gray);

        gauss(w, h, outarray, outarray2); //C++
//        gradient(outarray); //Java

//        sobel(outarray, filter); // Java
//        sobel(w, h, outarray, outarray2, flattern(filter), filter.length); //C++

        return ArrayToMat(gray,w,h,outarray2);
    }

    public double[] flattern(double[][] array){
        int i = 0;
        double out[] = new double[array.length * array.length];
        for(double[] tab : array)
            for(double d : tab)
            {
                out[i++] = d;
            }
        return out;
    }

    private Mat ArrayToMat(Mat gray,int w, int h, byte[] grayarray) {
        // TODO Auto-generated method stub
        Mat out = gray.clone();//new Mat(w,h,CvType.CV_8UC1);
        out.put(0,0,grayarray);
        return out;
    }

    private void MatToArray(Mat gray) {
        // TODO Auto-generated method stub
        gray.get(0, 0, outarray);

    }



}

