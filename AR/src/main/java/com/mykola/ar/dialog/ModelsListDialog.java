package com.mykola.ar.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mykola.ar.R;
import com.mykola.ar.adapter.ModelsListAdapter;
import com.mykola.ar.callbacks.CallbackUpdate;


public class ModelsListDialog extends DialogFragment implements CallbackUpdate {


    private RecyclerView list;
    private RecyclerView.Adapter adapter;

    Runnable r  = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            handler.postDelayed(this,500);
        }
    };

    private CallbackUpdate callbackUpdate;

    private Handler handler;

    public static ModelsListDialog newInstance() {

        Bundle args = new Bundle();

        ModelsListDialog fragment = new ModelsListDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_models_list, null);

        list = (RecyclerView) v.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ModelsListAdapter(this);

        list.setAdapter(adapter);
        handler = new Handler();



        handler.post(r);

        Dialog dialog = new AlertDialog.Builder(getContext()).
                setView(v)
             /*   .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null)*/
                .setTitle("Pick Model")
                .create();
        dialog.getWindow().setGravity(Gravity.RIGHT);


        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackUpdate) {
            callbackUpdate = (CallbackUpdate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CallbackUpdate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbackUpdate = null;
    }


    @Override
    public void update() {
        callbackUpdate.update();
        handler.removeCallbacks(r);
        dismiss();

    }
}
