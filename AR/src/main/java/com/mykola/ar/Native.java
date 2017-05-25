package com.mykola.ar;

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

    public static synchronized native void nativeDrawFrame();


    public static synchronized native float nativeScaleModel(int position, float s);

    public static synchronized native void nativeTranslateModel(int position, float x, float y, float z);

    public static synchronized native void nativeRotateModel(int position, float angle, float x, float y, float z);

    public static native int nativeAddObj(String data, String pattern, float scale);

    public static native int nativeGetObjsNumber();
}
