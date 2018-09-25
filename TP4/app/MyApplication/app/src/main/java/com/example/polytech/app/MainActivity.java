package com.example.polytech.app;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

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
        array[y * w + x] = (byte)(pix%255);
    }

    public byte[] gradient(byte[] array){

        byte[] outarray2 = new byte[array.length];
        for(int y = 1; y < h -1; y++){
            for(int x = 1; x < w -1; x++){
                int gh = Math.abs(getPix(array, x-1, y) - getPix(array, x+1, y));
                int gv = Math.abs(getPix(array, x, y-1) - getPix(array, x, y+1));
                setPix(outarray2, x, y,gh + gv);
            }
        }

        for(int x : new int[]{0, w - 1})
            for(int y : new int[]{0, h - 1})
                setPix(outarray2, x, y,getPix(array, x, y));

        return outarray2;
    }

    public byte[] sobel(byte[] array, int[][] convol){
        if(convol.length == 0 || convol[0].length == 0 || convol.length > w || convol[0].length > h || convol.length%2 == 1 || convol[0].length%2 == 1){
            Log.w(TAG, "Convolation array not good !!!");
            return array;
        }

        byte[] outarray2 = new byte[array.length];
        for(int y = 1; y < h -1; y++){
            for(int x = 1; x < w -1; x++){
                setPix(outarray2, x, y, applyConvFilterAt(outarray, convol, x, y));
            }
        }
        return outarray2;
    }

    private int applyConvFilterAt(byte[] outarray, int[][] convol, int x, int y) {

        int xcenter = convol.length/2;
        int ycenter = convol[0].length/2;

        int sumConv = 0;
        for(int[] i : convol)
            for(int j : i)
                sumConv += j;

        int sum = 0;
        for(int xc = 0; xc < convol.length; xc++){
            for (int yc = 0; yc < convol[0].length; yc++) {
                if(x + xc - xcenter < 0 || y+yc-ycenter < 0 || x + xc - xcenter >= w || y+yc-ycenter >= h)
                    continue;
                sum += convol[xc][yc] * getPix(outarray, x - xcenter + xc, y - ycenter + yc);
            }
        }

        return sum / sumConv;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat gray =inputFrame.gray();
        MatToArray(gray);

        byte[] outarray2 = sobel(outarray, new int[][]{{0, 1, 0}, {1, 0, -1}, {0, -1, 0}});

        Mat out=ArrayToMat(gray,w,h,outarray2);
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

