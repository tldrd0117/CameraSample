package com.sungjae.cameraapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.info.CameraFocusResult;
import com.sungjae.cameraapp.util.CameraGestureDetector;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraPresenter implements CameraMVP.Presenter, SurfaceHolder.Callback {
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
    public void surfaceCreated(final SurfaceHolder holder) {
        model.surfaceCreated(view.getHolder());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        model.surfaceChanged(view.getHolder(), format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void focusArea(float x, float y, int surfaceViewWidth, int surfaceViewHeight, int areaSize) {
        model.focusArea(x, y, surfaceViewWidth, surfaceViewHeight, areaSize).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if( o instanceof Point){
                    Point point = (Point)o;
                    view.startFocusAnimation( point.x, point.y );
                }
                else if( o instanceof CameraFocusResult){
                    CameraFocusResult cameraFocusResult = (CameraFocusResult) o;
                    if(CameraFocusResult.SUCCESS.getValue() ==cameraFocusResult.getValue()) {
                        view.startSuccessFocusAnimation();
                    }
                    else {
                        view.startFailFocusAnimation();
                    }
                }
            }
        });
    }
}
