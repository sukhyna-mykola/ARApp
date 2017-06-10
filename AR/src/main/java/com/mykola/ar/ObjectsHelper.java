package com.mykola.ar;

import com.mykola.ar.model.Object3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mykola on 23.05.17.
 */

public class ObjectsHelper {
    private static ObjectsHelper helper;
    private List<Object3D> objs;
    private Object3D selectedModel;


    public static ObjectsHelper getInstance() {
        if (helper == null)
            helper = new ObjectsHelper();
        return helper;
    }

    private ObjectsHelper() {
        objs = new ArrayList<>();
    }

    public int addModel(String dataPath, String patternPath, float scale) {

        int result = -1;
        Object3D o = new Object3D(dataPath, patternPath, -1);
        if (!isModel(o)) {
            result = Native.nativeAddObj(o.getDataPath(), o.getPatternPath(), scale);

            if (result != -1) {
                o.setMarkerID(result);
                objs.add(o);

            }
        }
        return result;
    }

    private boolean isModel(Object3D o) {
        for (Object3D obj : objs) {
            if (o.getDataPath().equals(obj.getDataPath()))
                return true;
        }
        return false;
    }


    private int calkModelPositionOnList(Object3D o) {
        return objs.indexOf(o);
    }

    public void scaleModel(float scale) {
        if (selectedModel != null) {
            int position = calkModelPositionOnList(selectedModel);
            Native.nativeScaleModel(position, scale);
        }
    }


    public synchronized void translateModel(float x, float y, float z) {
        if (selectedModel != null)
            Native.nativeTranslateModel(calkModelPositionOnList(selectedModel), x, y, z);
    }

    public List<Object3D> getObjs() {
        return objs;
    }

    public Object3D getSelectedModel() {

        return selectedModel;
    }

    public void setSelectedModel(Object3D selectedModel) {
        this.selectedModel = selectedModel;
    }

    public synchronized void rotateModel(float angle, float x, float y, float z) {
        if (selectedModel != null)
            Native.nativeRotateModel(calkModelPositionOnList(selectedModel), angle, x, y, z);
    }

    public void init() {
        /// ObjectsHelper.getInstance().addModel("Data/models/Porsche_911_GT3.obj", "single;Data/hiro.patt;80", 0.03f);
        addModel("Data/models/table.obj", "single;Data/hiro.patt;80", 1.0f);
        //ObjectsHelper.getInstance().addModel("Data/models/wardrobe/Wardrobe.obj", "single;Data/hiro.patt;80", 1.0f);
        addModel("Data/models/bed.obj", "single;Data/marker5.patt;80", 1.0f);
        addModel("Data/models/chair.obj", "single;Data/kanji.patt;80", 1.0f);

        if (objs.size() > 0)
            setSelectedModel(ObjectsHelper.getInstance().getObjs().get(0));

    }
}
