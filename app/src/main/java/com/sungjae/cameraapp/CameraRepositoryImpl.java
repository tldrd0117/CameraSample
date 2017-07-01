package com.sungjae.cameraapp;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.data.storage.ImageSaveStorage;
import com.sungjae.cameraapp.info.CameraFaceInfo;
import com.sungjae.cameraapp.util.CameraGestureDetector;
import com.sungjae.cameraapp.util.SizeUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraRepositoryImpl implements CameraRepository {
    Camera camera;
    CameraFaceInfo cameraFaceInfo;
    CameraGestureDetector cameraGestureDetector;
    ImageSaveStorage imageSaveStorage;

    public CameraRepositoryImpl(Context context){
        cameraFaceInfo = CameraFaceInfo.BACK;
        cameraGestureDetector = new CameraGestureDetector(context, 0 , 0);
        imageSaveStorage = new ImageSaveStorage();
    }

    @Override
    public int getCameraId(CameraFaceInfo cameraFaceInfo){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for( int i = 0; i < cameraCount; ++i ){
            if( cameraFaceInfo.getValue() == cameraInfo.facing){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Camera getCameraInstance( ){
        if( camera != null ){
            return camera;
        }
        Camera c = null;
        try {
            c = Camera.open(getCameraId(cameraFaceInfo)); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        camera = c;
        return c; // returns null if camera is unavailable
    }

    @Override
    public void switchCamera() {
        cameraFaceInfo.setOtherSideValue();
        camera.release();
        camera = null;
    }

    @Override
    public void releaseCamera() {
        if( camera == null ){
            return;
        }
        camera.release();
        camera = null;
    }

    @Override
    public Observable<String> imageSave(final byte[] imageBytes) {
        return imageSaveStorage.saveImage(imageBytes);
    }

    @Override
    public CameraGestureDetector getCameraGestureDetector(CameraGestureDetector.CameraGestureListener listener) {
        cameraGestureDetector.setCameraGestureListener(listener);
        return cameraGestureDetector;
    }

}
