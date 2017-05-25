package com.mykola.ar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykola.ar.ObjectsHelper;
import com.mykola.ar.R;
import com.mykola.ar.callbacks.CallbackUpdate;
import com.mykola.ar.model.Object3D;

import org.artoolkit.ar.base.ARToolKit;

import java.util.List;


public class ModelsListAdapter extends RecyclerView.Adapter<ModelsListAdapter.ViewHolder> {

    private List<Object3D> data;
    private CallbackUpdate callbackUpdate;


    public ModelsListAdapter(CallbackUpdate callbackUpdate) {
        this.callbackUpdate = callbackUpdate;
        this.data = ObjectsHelper.getInstance().getObjs();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Object3D model = data.get(position);

        holder.title.setText(model.getDataPath());

        if (model.getMarkerID() == ObjectsHelper.getInstance().getSelectedModel().getMarkerID()) {
            holder.selected.setImageResource(R.drawable.selected);
        }else {
            holder.selected.setImageResource(R.drawable.un_selected);
        }


        if (!ARToolKit.getInstance().queryMarkerVisible(model.getMarkerID())) {
            holder.title.setEnabled(false);
        } else holder.title.setEnabled(true);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectsHelper.getInstance().setSelectedModel(model);
                callbackUpdate.update();
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView title;
        public ImageView selected;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.title);
            selected = (ImageView) view.findViewById(R.id.selected);


        }

    }


}
