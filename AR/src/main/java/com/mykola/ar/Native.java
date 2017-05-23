package com.mykola.ar;

import org.artoolkit.ar.base.FPSCounter;

/**
 * Created by mykola on 23.05.17.
 */

public class Native {


    // Load the native libraries.
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("ARWrapper");
        System.loadLibrary("Native");
    }



    public static native void nativeInitialise();

    public static native void nativeShutdown();

    public static native void nativeSurfaceCreated();

    public static native void nativeSurfaceChanged(int w, int h);

    public static native void nativeDrawFrame();

    public static native float scale(float s);

    public static native void translate(float x, float y, float z);

    public static native int nativeAddObj(String data, String pattern,float scale);

    public static native int nativeGetObjsNumber();
}
