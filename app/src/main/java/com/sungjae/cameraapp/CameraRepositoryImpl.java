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

    ImageSaveStorage imageSaveStorage;

    public CameraRepositoryImpl(Context context){
        imageSaveStorage = new ImageSaveStorage();
    }

    @Override
    public Observable<String> imageSave(final byte[] imageBytes) {
        return imageSaveStorage.saveImage(imageBytes);
    }

}
