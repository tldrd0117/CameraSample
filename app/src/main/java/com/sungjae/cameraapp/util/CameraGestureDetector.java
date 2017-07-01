package com.sungjae.cameraapp.util;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Created by iseongjae on 2016. 10. 23..
 */

public class CameraGestureDetector {
    private static final String TAG = "CameraZoomMultiTouch";
    private int[] global_num;
    private int[] global_x;
    private int[] global_y;
    private int   firstDistance;
    private int   moveDistance;
    private int   maxZoom;
    private int   distanceComp;
    public  int   allMoveDistance;
    private int   areaSize;
    private boolean actionPointerUp = false;
    CameraGestureListener cameraGestureListener;

    public CameraGestureDetector(Context context, int minZoom, int maxZoom ) {
        this.maxZoom = maxZoom;
        global_num = new int[2];
        global_x = new int[2];
        global_y = new int[2];
        distanceComp = (int)SizeUtil.getPxFromDp( 3, context );
        areaSize = (int)SizeUtil.getPxFromDp(100, context);
    }

    public void setCameraGestureListener(CameraGestureListener cameraGestureListener) {
        this.cameraGestureListener = cameraGestureListener;
    }

    public boolean onTouchEvent(final MotionEvent event, int surfaceViewWidth, int surfaceViewHeight ) {
        int pointer_count = event.getPointerCount(); //현재 터치 발생한 포인트 수를 얻는다.

        if ( pointer_count > 2 ) {
            pointer_count = 2; //3개 이상의 포인트를 터치했더라도 2개까지만 처리를 한다.
        }

        int action = MotionEventCompat.getActionMasked( event );

        switch ( action & MotionEvent.ACTION_MASK ) {
            case MotionEvent.ACTION_DOWN: //한 개 포인트에 대한 DOWN을 얻을 때.
                return true;
            case MotionEvent.ACTION_POINTER_DOWN: //두 개 이상의 포인트에 대한 DOWN을 얻을 때.
                actionPointerUp = false;
                if ( pointer_count > 1 ) {
                    for ( int i = 0; i < pointer_count; i++ ) {
                        global_num[i] = event.getPointerId( i ); //터치한 순간부터 부여되는 포인트 고유번호.
                        global_x[i] = (int)( event.getX( i ) );
                        global_y[i] = (int)( event.getY( i ) );
                    }
                    firstDistance = (int)Math.sqrt( Math.pow( global_x[0] - global_x[1], 2 ) + Math.pow( global_y[0] - global_y[1], 2 ) );
                    cameraGestureListener.onZoomStart();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if ( pointer_count > 1 ) {

                    for ( int i = 0; i < pointer_count; i++ ) {
                        global_num[i] = event.getPointerId( i );
                        global_x[i] = (int)( event.getX( i ) );
                        global_y[i] = (int)( event.getY( i ) );
                    }
                    moveDistance = (int)Math.sqrt( Math.pow( global_x[0] - global_x[1], 2 ) + Math.pow( global_y[0] - global_y[1], 2 ) );
                    int comp = moveDistance - firstDistance;
                    allMoveDistance += comp;
                    if ( comp > 0 && distanceComp != 0 ) {
                        firstDistance = moveDistance;
                        if ( allMoveDistance > distanceComp * maxZoom ) {
                            allMoveDistance = distanceComp * maxZoom;
                        }
                        float zoom = (float)allMoveDistance / distanceComp;
                        cameraGestureListener.zoom( (int)( zoom ) );

                    }
                    else if ( comp < 0 && firstDistance != 0 ) {
                        firstDistance = moveDistance;
                        if ( allMoveDistance < 0 ) {
                            allMoveDistance = 0;
                        }
                        float zoom = (float)allMoveDistance / distanceComp;
                        cameraGestureListener.zoom( (int)( zoom ) );

                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionPointerUp = true;
                cameraGestureListener.onZoomFinish();
                //                cameraObject.focusArea(CameraPreview.this.getWidth() / 2, CameraPreview.this.getHeight() / 2,
                //                        getContext(), CameraPreview.this.getWidth(), CameraPreview.this.getHeight(), 0);
                return false;
            case MotionEvent.ACTION_UP:
                if ( !actionPointerUp ) {

                    cameraGestureListener.focusArea( event.getX(), event.getY(), surfaceViewWidth, surfaceViewHeight, areaSize );
                    //                    cameraObject.focusArea(event.getX(), event.getY(), getContext(),
                    //                            CameraPreview.this.getWidth(), CameraPreview.this.getHeight(), 0);
                }
                else {
                    actionPointerUp = false;
                }
                return false;
        }
        return true;
    }

    public interface CameraGestureListener {
        void onZoomStart();

        void onZoomFinish();

        void zoom(int zoomValue);

        void focusArea(float x, float y, int surfaceViewWidth, int surfaceViewHeight, int areaSize );
    }
}
