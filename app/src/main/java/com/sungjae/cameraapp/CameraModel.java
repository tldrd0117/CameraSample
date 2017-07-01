package com.sungjae.cameraapp;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;

import com.sungjae.cameraapp.util.CameraGestureDetector;

import io.reactivex.Observable;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraModel implements CameraMVP.Model {

    CameraRepository repository;

    public CameraModel(Context context){
        repository = new CameraRepositoryImpl(context);
    }

    @Override
    public Camera getCameraInstance() {
        return repository.getCameraInstance();
    }

    @Override
    public void switchCamera() {
        repository.switchCamera();
    }

    @Override
    public void releaseCamera() {
        repository.releaseCamera();
    }

    @Override
    public CameraGestureDetector getCameraGestureDetector(CameraGestureDetector.CameraGestureListener listener) {
        return repository.getCameraGestureDetector(listener);
    }

    @Override
    public Observable<String> imageSave(byte[] imageBytes) {
        return repository.imageSave(imageBytes);
    }

}
