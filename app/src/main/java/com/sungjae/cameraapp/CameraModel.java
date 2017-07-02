package com.sungjae.cameraapp;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.info.CameraFaceInfo;
import com.sungjae.cameraapp.info.CameraFocusResult;
import com.sungjae.cameraapp.util.CameraGestureDetector;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;

import static android.R.attr.left;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraModel implements CameraMVP.Model {
    Camera camera;
    CameraFaceInfo cameraFaceInfo;
    CameraRepository repository;

    public CameraModel(Context context){
        repository = new CameraRepositoryImpl(context);
        cameraFaceInfo = CameraFaceInfo.BACK;
    }

    private int getCameraId(CameraFaceInfo cameraFaceInfo){
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
    public Observable<String> imageSave(byte[] imageBytes) {
        return repository.imageSave(imageBytes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("surfaceCreated", "surfaceCreated");
        try {
            Camera camera = getCameraInstance();
            camera.setPreviewDisplay( holder );
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("surfaceChanged", "surfaceChanged");

        if( holder == null ){
            Log.d("surfaceChanged", "surfaceChanged2");

            return;
        }
        Camera camera = getCameraInstance();

        camera.stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here

        try {
            camera.setPreviewDisplay( holder );
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Observable<Object> focusArea(final float x, final float y, int surfaceViewWidth, int surfaceViewHeight, final int areaSize) {
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
        parameters = getCameraInstance().getParameters();
        if ( parameters != null ) {
            ArrayList< Camera.Area > focusArea = new ArrayList<>();
            focusArea.add( new Camera.Area( focusRect, 500 ) );
            parameters.setFocusAreas( focusArea );
            getCameraInstance().setParameters( parameters );
        }

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Object> e) throws Exception {

                int left     = (int)( x - areaSize / 2 );
                int top      = (int)( y - areaSize / 2 );
                Point point = new Point(left, top);
                e.onNext(point);

                getCameraInstance().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if(success){
                            e.onNext(CameraFocusResult.SUCCESS);
                        }
                        else{
                            e.onNext(CameraFocusResult.FAIL);
                        }
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
