package com.sungjae.cameraapp;

import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.sungjae.cameraapp.info.CameraFaceInfo;
import com.sungjae.cameraapp.util.CameraGestureDetector;

import io.reactivex.Observable;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public interface CameraRepository {
    Observable<String> imageSave(byte[] imageBytes);

}
