package com.sungjae.cameraapp.info;

/**
 * Created by iseongjae on 2017. 7. 1..
 */

public enum CameraFaceInfo {
    BACK(0), FRONT(1);
    int value;
    CameraFaceInfo( int value ){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setOtherSideValue(){
        if( value == BACK.getValue()){
            value = FRONT.getValue();
        }
        else{
            value = BACK.getValue();
        }
    }
}
