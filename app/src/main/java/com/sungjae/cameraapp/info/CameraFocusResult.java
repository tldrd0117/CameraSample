package com.sungjae.cameraapp.info;

/**
 * Created by iseongjae on 2017. 7. 2..
 */

public enum  CameraFocusResult {
    SUCCESS(0), FAIL(1);

    int value;
    CameraFocusResult( int value ){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
