/*
 *  ARSimpleNativeCarsActivity.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package com.mykola.ar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mykola.ar.callbacks.CallbackUpdate;
import com.mykola.ar.dialog.ControllModelDialog;
import com.mykola.ar.dialog.ModelsListDialog;
import com.mykola.ar.dialog.ScreenshotDialog;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ScreenShot;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class ARMainActivity extends ARActivity implements View.OnClickListener {
    public static float WIDTH, HEIGHT;

    private Thread t;
    private FrameLayout frameLayout;
    private ScreenShot screen = new ScreenShot(this);
    private NativeRenderer nativeRenderer = new NativeRenderer(screen);

    private OnToutchController toutchController;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGlobalLayout() {

            HEIGHT = frameLayout.getHeight();
            WIDTH = frameLayout.getWidth();

            screen.setHeight((int) HEIGHT);
            screen.setWidth((int) WIDTH);

            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        frameLayout = (FrameLayout) findViewById(R.id.mainLayout);


        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        toutchController = new OnToutchController();

    }


    public void onStop() {
        Native.nativeShutdown();
        super.onStop();
    }

    @Override
    protected ARRenderer supplyRenderer() {
        return nativeRenderer;
    }

    @Override
    protected View.OnTouchListener getToutch() {
        return toutchController;
    }

    @Override
    protected ScreenShot getScreenShot() {
        return screen;
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return frameLayout;


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.models:
                new ModelsListDialog().show(getSupportFragmentManager(), "MODELS_DIALOG");
                break;
            case R.id.controll_model:
                new ControllModelDialog().show(getSupportFragmentManager(), "CONTROLL_DIALOG");
                break;
            case R.id.makePhoto:
                takeScreenshot();
                break;

        }
    }

    @Override
    protected void takeScreenshot() {
        super.takeScreenshot();
        t = new Thread(r);
        t.start();
    }


    /* private void makePhoto() {
        v1 = frameLayout;
        v1.setDrawingCacheEnabled(true);
        v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
        v1.buildDrawingCache(true);

        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ImageView v = (ImageView) findViewById(R.id.preview);
        v.setImageBitmap(bitmap);

        v1.destroyDrawingCache();


    }*/


    private Runnable r = new Runnable() {
        @Override
        public void run() {
            boolean running = true;
            while (running)
                if (screen.isLoaded()) {
                    running = false;
                    screen.setLoaded(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScreenshotDialog.newInstance(screen.getResultBitmap()).show(getSupportFragmentManager(), "SCREENSHOT_DIALOG");
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    };


    @Override
    public void screenshot(Bitmap bitmap) {

    }
}