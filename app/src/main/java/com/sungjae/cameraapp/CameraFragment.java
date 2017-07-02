package com.sungjae.cameraapp;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sungjae.cameraapp.anim.CameraAnimFactory;
import com.sungjae.cameraapp.base.BaseCameraFragment;
import com.sungjae.cameraapp.util.CameraGestureDetector;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by iseongjae on 2017. 6. 30..
 */

public class CameraFragment extends BaseCameraFragment implements CameraMVP.View{

    @BindView(R.id.captureBtn)
    ImageButton captureBtn;

    @BindView(R.id.rootLayout)
    FrameLayout rootLayout;

    @BindView(R.id.focusView)
    View focusView;

    @BindView(R.id.flashView)
    View flashView;

    SurfaceView surfaceView;

    CameraMVP.Presenter presenter;
    CameraGestureDetector cameraGestureDetector;
    OrientationEventListener orientationEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter = new CameraPresenter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCameraPermission(Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                setupViews();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()));


    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.releaseCamera();
        if(orientationEventListener != null) {
            orientationEventListener.disable();
        }
    }

    public void setupViews(){
        if(surfaceView != null){
            rootLayout.removeView(surfaceView);
        }
        surfaceView = new SurfaceView(getActivity());
        surfaceView.setKeepScreenOn( true );
        surfaceView.setWillNotDraw( false );
        rootLayout.addView(surfaceView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        presenter.setView(CameraFragment.this);
        presenter.setupPreview();
        cameraGestureDetector = new CameraGestureDetector( getActivity(), 0 ,0 );

        cameraGestureDetector.setFocusListener(new CameraGestureDetector.FocusListener() {
            @Override
            public void focusArea(float x, float y, int surfaceViewWidth, int surfaceViewHeight, int areaSize) {
                presenter.focusArea( x, y, surfaceViewWidth, surfaceViewHeight, areaSize);
            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.takePicture();
            }
        });
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return cameraGestureDetector.onTouchEvent(event, surfaceView.getWidth(), surfaceView.getHeight());
            }
        });
        orientationEventListener = new OrientationEventListener( getContext(), SensorManager.SENSOR_DELAY_UI ) {
            @Override
            public void onOrientationChanged( int orientation ) {
                Log.d("orientation", "orientation : " + orientation);
            }
        };
        orientationEventListener.enable();
    }

    @Override
    public SurfaceHolder getHolder() {
        return surfaceView.getHolder();
    }

    @Override
    public void checkPermission( Completable completable ) {
        checkCameraPermission(completable);
    }

    @Override
    public void startFocusAnimation(int left, int top) {
        focusView.setTranslationX(left);
        focusView.setTranslationY(top);
        CameraAnimFactory.createAutoFocusAnim(focusView).start();
    }

    @Override
    public void startSuccessFocusAnimation() {
        CameraAnimFactory.createAutoFocusSuccessAnim(focusView).start();
    }

    @Override
    public void startFailFocusAnimation() {
        CameraAnimFactory.createAutoFocusFailAnim(focusView).start();
    }

    @Override
    public void startShutterAnimation() {
        CameraAnimFactory.createFlashScreenAnim(flashView).start();
    }

}
