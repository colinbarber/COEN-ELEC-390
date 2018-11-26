package com.example.roumeliotis.tagit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;

public class FullScreenImageActivity extends Activity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        mImageView = findViewById(R.id.image_fullscreen);

        //get image
        Bundle extras = getIntent().getExtras();
        Bitmap currentImage = (Bitmap) extras.getParcelable("BitmapImage");
        //set in image view
        if(currentImage != null){
            mImageView.setImageBitmap(currentImage);
        }
    }

}
