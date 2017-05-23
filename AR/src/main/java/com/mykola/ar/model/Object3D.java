package com.mykola.ar.model;

/**
 * Created by mykola on 23.05.17.
 */

public class Object3D {

    private float scale;

    private int markerID;
    private String dataPath;
    private String patternPath;


    public Object3D(String dataPath, String patternPath, int markerID) {
        this.dataPath = dataPath;
        this.patternPath = patternPath;
        this.markerID = markerID;
    }



    public String getDataPath() {
        return dataPath;
    }

    public int getMarkerID() {
        return markerID;
    }

    public float getScale() {
        return scale;
    }



    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setMarkerID(int markerID) {
        this.markerID = markerID;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getPatternPath() {
        return patternPath;
    }
}
