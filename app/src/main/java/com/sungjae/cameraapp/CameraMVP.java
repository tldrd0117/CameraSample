package com.sungjae.cameraapp;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.info.CameraFaceInfo;
import com.sungjae.cameraapp.util.CameraGestureDetector;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public interface CameraMVP {
    interface View{
        SurfaceHolder getHolder();
        void checkPermission(Completable completable);
        void startFocusAnimation(int left, int top);
        void startSuccessFocusAnimation();
        void startFailFocusAnimation();
        void startShutterAnimation();
    }

    interface Presenter{
        void setView(View view);
        boolean checkCameraHardware(Context context);
        void setupPreview();
        void takePicture();
        void releaseCamera();
        boolean focusCamera(MotionEvent event, int surfaceViewWidth, int surfaceViewHeight);

    }

    interface Model{
        Camera getCameraInstance( );
        void switchCamera();
        void releaseCamera();
        CameraGestureDetector getCameraGestureDetector(CameraGestureDetector.CameraGestureListener listener);
        Observable<String> imageSave(byte[] imageBytes);

    }
}
