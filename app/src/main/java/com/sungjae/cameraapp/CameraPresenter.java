package com.sungjae.cameraapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.util.CameraGestureDetector;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraPresenter implements CameraMVP.Presenter, SurfaceHolder.Callback, CameraGestureDetector.CameraGestureListener {
    CameraMVP.View view;
    CameraMVP.Model model;

    public CameraPresenter(Context context){
        model = new CameraModel( context);
    }

    @Override
    public void setView(CameraMVP.View view) {
        this.view = view;
    }

    @Override
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setupPreview() {
        view.getHolder().addCallback(this);
    }

    @Override
    public void takePicture() {
        model.getCameraInstance().takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                view.startShutterAnimation();
                model.imageSave(data).subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d("path", s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

    @Override
    public void releaseCamera() {
        model.releaseCamera();
    }

    @Override
    public boolean focusCamera(MotionEvent event, int surfaceViewWidth, int surfaceViewHeight) {
        CameraGestureDetector detector = model.getCameraGestureDetector(this);
        return detector.onTouchEvent(event, surfaceViewWidth, surfaceViewHeight);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.d("surfaceCreated", "surfaceCreated");
        try {
            Camera camera = model.getCameraInstance();
            camera.setPreviewDisplay( holder );
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("surfaceChanged", "surfaceChanged");

        if( view.getHolder() == null ){
            Log.d("surfaceChanged", "surfaceChanged2");

            return;
        }
        Camera camera = model.getCameraInstance();

        camera.stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here

        try {
            camera.setPreviewDisplay( view.getHolder() );
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onZoomStart() {

    }

    @Override
    public void onZoomFinish() {

    }

    @Override
    public void zoom(int zoomValue) {

    }

    @Override
    public void focusArea(float x, float y, int surfaceViewWidth, int surfaceViewHeight, int areaSize) {
        Log.d("focusArea", "x : " + x + " y : " + y + " width : " + surfaceViewWidth + " height : " + surfaceViewHeight);

        float widthRatio = 2000 / (float)surfaceViewWidth;
        float heightRatio = 2000 / (float)surfaceViewHeight;

        int newX = (int) (x * widthRatio) - 1000;
        int newY = (int) (y * heightRatio) - 1000;

        int focusTop = newY - areaSize/2;
        int focusLeft = newX - areaSize/2;
        int focusBottom = newY + areaSize/2;
        int focusRight = newX + areaSize/2;

        if( focusTop < -1000){
            focusTop = -1000;
        }

        if( focusLeft < -1000){
            focusLeft = -1000;
        }

        if( focusBottom > 1000){
            focusBottom = 1000;
        }

        if( focusRight > 1000){
            focusRight  = 1000;
        }

        Rect focusRect = new Rect(focusLeft, focusTop, focusRight, focusBottom);

        Camera.Parameters parameters        = null;
        parameters = model.getCameraInstance().getParameters();
        if ( parameters != null ) {
            ArrayList< Camera.Area > focusArea = new ArrayList<>();
            focusArea.add( new Camera.Area( focusRect, 500 ) );
            parameters.setFocusAreas( focusArea );
            model.getCameraInstance().setParameters( parameters );
        }

        model.getCameraInstance().autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    view.startSuccessFocusAnimation();
                }
                else{
                    view.startFailFocusAnimation();
                }
            }
        });

        int left     = (int)( x - areaSize / 2 );
        int top      = (int)( y - areaSize / 2 );
        view.startFocusAnimation(left, top);
    }
}
