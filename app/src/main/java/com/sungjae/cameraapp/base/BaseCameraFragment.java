package com.sungjae.cameraapp.base;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

/**
 * Created by iseongjae on 2017. 7. 1..
 */

public class BaseCameraFragment extends Fragment {
    public static final int REQUEST_CODE_PERMISSION = 33;
    List<Completable> completableList = new ArrayList<>();
    protected void checkCameraPermission(Completable completable) {
        String[] permissionArray = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> deniedList = new ArrayList<>();
        for (String permission : permissionArray) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(getActivity(), permission)) {
                deniedList.add(permission);
            }
        }
        if (deniedList.size() == 0) {
            completable.subscribe();
            return;
        }
        completableList.add(completable);
        if (completableList.size() == 1) {
            requestPermissions(deniedList.toArray(new String[deniedList.size()]), REQUEST_CODE_PERMISSION);
        }
    }

    protected void checkWritePermission(Completable completable) {
        String[] permissionArray = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> deniedList = new ArrayList<>();
        for (String permission : permissionArray) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(getActivity(), permission)) {
                deniedList.add(permission);
            }
        }
        if (deniedList.size() == 0) {
            completable.subscribe();
            return;
        }
        completableList.add(completable);
        if (completableList.size() == 1) {
            requestPermissions(deniedList.toArray(new String[deniedList.size()]), REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( REQUEST_CODE_PERMISSION != requestCode ){
            return;
        }
        if( permissions.length == grantResults.length ){
            for( int result : grantResults ){
                if( result == PackageManager.PERMISSION_DENIED){
                    return;
                }
            }
            for( Completable completable : completableList){
                completable.subscribe();
            }
            completableList.clear();
        }
    }
}
