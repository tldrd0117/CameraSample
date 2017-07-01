package com.sungjae.cameraapp.anim;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.sungjae.cameraapp.R;


/**
 * Created by iseongjae on 2016. 10. 23..
 */

public class CameraAnimFactory {
    public static ViewPropertyAnimator createFlashScreenAnim( final View view ) {
        final ViewPropertyAnimator animator = view.animate().alpha( 1.0f );
        view.setAlpha( 0.0f );

        animator.setListener( new AbstractAnimatorListener() {
            @Override
            public void onAnimationStart( Animator animation ) {
                view.setVisibility( View.VISIBLE );
            }

            @Override
            public void onAnimationEnd( Animator animation ) {
                view.setVisibility( View.GONE );
                animator.setListener( null );
                view.setAlpha( 0.0f );

            }
        } );
        return animator;
    }

    public static ViewPropertyAnimator createAutoFocusAnim( final View view ) {
        view.setVisibility( View.VISIBLE );
        view.setScaleX( 1.0f );
        view.setScaleY( 1.0f );
        view.setBackgroundResource( R.drawable.focus_area_auto );
        return view.animate().setStartDelay( 0 ).scaleX( 0.7f ).scaleY( 0.7f );
    }


    public static ViewPropertyAnimator createAutoFocusSuccessAnim( final View view ) {
        view.setBackgroundResource( R.drawable.focus_area_auto_success );
        final ViewPropertyAnimator animator = view.animate();
        animator.setStartDelay( 600 ).setListener( new AbstractAnimatorListener() {
            @Override
            public void onAnimationStart( Animator animation ) {
                super.onAnimationStart( animation );
                view.setVisibility( View.GONE );
                animator.setListener( null );
            }
        } );
        return animator;
    }

    public static ViewPropertyAnimator createAutoFocusFailAnim( final View view ) {
        view.setBackgroundResource( R.drawable.focus_area_auto_fail );
        final ViewPropertyAnimator animator = view.animate();
        animator.setStartDelay( 600 ).setListener( new AbstractAnimatorListener() {
            @Override
            public void onAnimationStart( Animator animation ) {
                super.onAnimationStart( animation );
                view.setVisibility( View.GONE );
                animator.setListener( null );
            }
        } );
        return animator;
    }

}
