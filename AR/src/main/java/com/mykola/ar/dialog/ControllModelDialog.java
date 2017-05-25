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
import android.view.ViewGroup;

import com.mykola.ar.ObjectsHelper;
import com.mykola.ar.R;

import butterknife.Unbinder;


public class ControllModelDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;


    public static ControllModelDialog newInstance() {

        Bundle args = new Bundle();

        ControllModelDialog fragment = new ControllModelDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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


        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 300;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().setGravity(Gravity.RIGHT);

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
