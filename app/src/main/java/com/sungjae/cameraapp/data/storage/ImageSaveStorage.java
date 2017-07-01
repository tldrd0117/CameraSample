package com.sungjae.cameraapp.data.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Observable.just;

/**
 * Created by iseongjae on 2017. 7. 1..
 */

public class ImageSaveStorage {
    public static SimpleDateFormat shortDateFormatOutput = new SimpleDateFormat( "yyMMdd-HHmmssSSS" );
    public static final String DIR_NAME = "HELLOWORLD";
    public final static String ROOT_PATH = Environment.getExternalStorageDirectory() + "/" + DIR_NAME;

    public ImageSaveStorage(){
        File root = new File(ROOT_PATH);
        if(!root.exists()){
            root.mkdir();
        }
    }

    public Observable<String> saveImage(final byte[] imageBytes){
        return Observable.just(imageBytes).flatMap(new Function<byte[], ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull byte[] bytes) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String imagePath = getNewImagePath();
                File file = new File(imagePath);
                file.createNewFile();
                FileOutputStream outputStream ;
                outputStream= new FileOutputStream(new File(imagePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);

                return Observable.just(imagePath);
            }
        }).subscribeOn(Schedulers.io());

    }

    private String getNewImagePath(){
        Calendar calendar = Calendar.getInstance();
        return ROOT_PATH + "/"+"IMAGE_" + shortDateFormatOutput.format( calendar.getTime() );

    }

}
