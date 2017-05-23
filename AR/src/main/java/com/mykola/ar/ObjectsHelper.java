package com.mykola.ar;

import com.mykola.ar.model.Object3D;

import org.artoolkit.ar.base.ARToolKit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mykola on 23.05.17.
 */

public class ObjectsHelper {
    public static ObjectsHelper helper;
    private List<Object3D> objs;




    public static ObjectsHelper getInstance() {
        if (helper == null)
            helper = new ObjectsHelper();
        return helper;
    }

    private ObjectsHelper() {
        objs = new ArrayList<>();
    }

    public int addObj(String dataPath, String patternPath, float scale) {

        Object3D o = new Object3D(dataPath, patternPath, -1);

        int result = Native.nativeAddObj(o.getDataPath(), o.getPatternPath(),scale);

        if (result!=-1) {
            o.setMarkerID(result);
            objs.add(o);

        }
        return result;
    }

}
