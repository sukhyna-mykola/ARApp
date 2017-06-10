package com.mykola.ar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ScreenshotActivity extends AppCompatActivity {
    public static final String SCREENSHOT_PICTURE = "SCREENSHOT_PICTURE";
    private ImageView scrennshotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        scrennshotView = (ImageView) findViewById(R.id.scrennshot_picture);

        Bitmap b = (Bitmap) getIntent().getExtras().get(SCREENSHOT_PICTURE);

        scrennshotView.setImageBitmap(b);
    }
}
