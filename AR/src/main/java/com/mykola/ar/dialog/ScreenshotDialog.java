package com.mykola.ar.dialog;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mykola.ar.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ScreenshotDialog extends DialogFragment {


    public static ScreenshotDialog newInstance(Bitmap bitmap) {

        Bundle args = new Bundle();

        args.putParcelable(SCREENSHOT_PICTURE, bitmap);
        ScreenshotDialog fragment = new ScreenshotDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static final String SCREENSHOT_PICTURE = "SCREENSHOT_PICTURE";
    private ImageView scrennshotView;
    private Toolbar toolbar;

    private Bitmap b;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = (Bitmap) getArguments().get(SCREENSHOT_PICTURE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_screenshot, null);


        scrennshotView = (ImageView) v.findViewById(R.id.scrennshot_picture);
        scrennshotView.setImageBitmap(b);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String currentDateandTime = sdf.format(new Date());
                        String name = currentDateandTime + ".png";

                        switch (item.getItemId()) {
                            case R.id.save:
                                save(b, name);
                                break;
                            case R.id.share:
                                share(b, name);
                                break;
                        }

                        return true;
                    }
                });

        toolbar.inflateMenu(R.menu.screenshot_dialog_menu);
        toolbar.setTitle("Screenshot");


        Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setPositiveButton("OK", null)
                .create();


        return dialog;
    }

    private void save(Bitmap b, String name) {
        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + name;

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            b.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getContext(), "Saved as - " + name, Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            Toast.makeText(getContext(), "Saving error", Toast.LENGTH_SHORT).show();
        }
    }


    private void share(Bitmap b, String name) {

        File file = new File(getContext().getCacheDir(), name);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String shareBody = "Here is the share content body";
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share via"));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
