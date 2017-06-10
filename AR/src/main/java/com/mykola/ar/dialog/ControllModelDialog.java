package com.mykola.ar.dialog;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.mykola.ar.ObjectsHelper;
import com.mykola.ar.R;


public class ControllModelDialog extends DialogFragment implements View.OnClickListener {


    public static ControllModelDialog newInstance() {

        Bundle args = new Bundle();

        ControllModelDialog fragment = new ControllModelDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_controll_model, null);

        v.findViewById(R.id.x_rotate_left).setOnClickListener(this);
        v.findViewById(R.id.x_rotate_right).setOnClickListener(this);
        v.findViewById(R.id.y_rotate_up).setOnClickListener(this);
        v.findViewById(R.id.y_rotate_down).setOnClickListener(this);
        v.findViewById(R.id.z_rotate_left).setOnClickListener(this);
        v.findViewById(R.id.z_rotate_right).setOnClickListener(this);

        v.findViewById(R.id.x_translate_left).setOnClickListener(this);
        v.findViewById(R.id.x_translate_right).setOnClickListener(this);
        v.findViewById(R.id.y_translate_down).setOnClickListener(this);
        v.findViewById(R.id.y_translate_up).setOnClickListener(this);

        v.findViewById(R.id.scale_minus).setOnClickListener(this);
        v.findViewById(R.id.scale_plus).setOnClickListener(this);


        Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null)
                .create();


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.RIGHT);


        return dialog;
    }

    private float angle = (float) (Math.PI / 20);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.z_rotate_left:
                ObjectsHelper.getInstance().rotateModel(angle, 0, 0, 1);
                break;
            case R.id.z_rotate_right:
                ObjectsHelper.getInstance().rotateModel(-angle, 0, 0, 1);
                break;
            case R.id.x_rotate_left:
                ObjectsHelper.getInstance().rotateModel(-angle, 0, 1, 0);
                break;
            case R.id.x_rotate_right:
                ObjectsHelper.getInstance().rotateModel(angle, 0, 1, 0);
                break;
            case R.id.y_rotate_up:
                ObjectsHelper.getInstance().rotateModel(-angle, 1, 0, 0);
                break;
            case R.id.y_rotate_down:
                ObjectsHelper.getInstance().rotateModel(angle, 1, 0, 0);
                break;
            case R.id.x_translate_left:
                ObjectsHelper.getInstance().translateModel(-1, 0, 0);
                break;
            case R.id.x_translate_right:
                ObjectsHelper.getInstance().translateModel(1, 0, 0);
                break;
            case R.id.y_translate_down:
                ObjectsHelper.getInstance().translateModel(0, -1, 0);
                break;
            case R.id.y_translate_up:
                ObjectsHelper.getInstance().translateModel(0, 1, 0);
                break;
            case R.id.scale_minus:
                ObjectsHelper.getInstance().scaleModel(0.9f);
                break;
            case R.id.scale_plus:
                ObjectsHelper.getInstance().scaleModel(1.1f);
                break;
        }
    }
}
