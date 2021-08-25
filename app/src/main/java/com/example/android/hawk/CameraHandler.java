package com.example.android.hawk;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class CameraHandler  {
    String encodedImage;  //The image will be converted to base64 string, so that it can be sent as attachment
    volatile boolean picReady;
    Bitmap bmp;

    CameraHandler(){
        encodedImage="PlaceHolder";
        picReady=false;  //This will turn true once the encoded string is ready
    }

    String takePic(){
        Camera camera=Camera.open(1); //1 indicates front camera, open instance of camera
        try{
            /**
             * Set a dummy preview texture, this will direct the output of preview to a SurfaceTexture
             * 10 is the texName, i.e. OpenGL texture object name, OpenGL is used to render graphics
             */
            camera.setPreviewTexture(new SurfaceTexture(10));
        }catch (Exception e){
            Log.e("Hawk.CameraHandler",e.getMessage());
        }
        /**
         * preview is mandatory before taking a picture
         * But displaying the preview is not mandatory
         */
        camera.startPreview();

        /***
         * Call takePicture method to click a picture
         * Camera.PictureCallback is an interface used to supply image data from a picture capture
         * Provide implementation for this interface
         */
        camera.takePicture(null, null, new Camera.PictureCallback() {
            /***
             * This method is called when image data is available after picture is taken
             * @param data The raw image is received in form of a byte array
             */
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Hawk.CameraHandler","onPictureCalled");
                //
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length); //Convert raw byte array to Bitmap format
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); //Write the Bitmap to ByteArrayOutputStream
                byte[] byteArray = byteArrayOutputStream .toByteArray();  //Get byte array from ByteArrayOutputStream
                //
                encodedImage=Base64.getEncoder().encodeToString(byteArray);   //Convert the byte array to Base64 encoded string
                camera.stopPreview();                                         //Stop the preview
                Log.d("EncodedImage","starthere#"+encodedImage+"###ENDED");
                picReady=true; //Indicates that encoded string is ready
                camera.release();
            }
        });
        while (!picReady){};
        /**
         * Clicking a picture is a relatively slow process, the above loop will prevent
         * the takePic() function from returning till the onPictureTaken has finished
         * Else the string returned might be empty/incomplete/corrupt
         */
        camera.release();
        return encodedImage;
    }
}
