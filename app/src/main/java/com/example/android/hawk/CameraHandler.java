package com.example.android.hawk;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Base64;

public class CameraHandler  {
    String encodedImage;
    volatile boolean picReady;
    Bitmap bmp;

    CameraHandler(){
        encodedImage="PlaceHolder";
        picReady=false;
    }

    String takePic(){
        Camera camera=Camera.open(1);
        try{
            camera.setPreviewTexture(new SurfaceTexture(10));
        }catch (Exception e){
            Log.e("Hawk.CameraHandler",e.getMessage());
        }
     //   Camera.Parameters params=camera.getParameters();
     //   params.setPreviewSize(640,480);
      //  camera.setParameters(params);
        camera.startPreview();

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Hawk.CameraHandler","onPictureCalled");
                //
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                //
                encodedImage=Base64.getEncoder().encodeToString(byteArray);
                camera.stopPreview();
                Log.d("EncodedImage","starthere#"+encodedImage+"###ENDED");
                picReady=true;
                camera.release();
            }
        });
        while (!picReady){};
        camera.release();
        return encodedImage;
    }
}
