package com.mykola.ar;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mykola.ar.model.Object3D;

import static com.mykola.ar.ARMainActivity.HEIGHT;
import static com.mykola.ar.ARMainActivity.WIDTH;

/**
 * Created by mykola on 24.05.17.
 */

public class OnToutchController implements View.OnTouchListener {

    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;

    private Object3D curentModel;

    public Object3D getCurentModel() {
        return curentModel;
    }

    public void setCurentModel() {
        this.curentModel = ObjectsHelper.getInstance().getSelectedModel();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // handle touch events here

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d("TAG", "old " + oldDist);
                if (oldDist > 5f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {

                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;

                    dx = dx / WIDTH;
                    dy = dy / HEIGHT;

                    float angleY = (float) Math.atan2(dy, 1);
                    float angleX = (float) Math.atan2(dx, 1);

                    Log.d("TAG", "dx = " + dx + "; dy = " + dy);


                    ObjectsHelper.getInstance().rotateModel(angleY, 1, 0, 0);
                    ObjectsHelper.getInstance().rotateModel(angleX, 0, 1, 0);


                } else if (mode == ZOOM) {
                    float newDist = spacing(event);

                    if (newDist > 5f) {

                        float scale = (newDist / oldDist);
                        oldDist = newDist;
                        Log.d("TAG", "scale " + scale);
                        ObjectsHelper.getInstance().scaleModel(scale);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = d - newRot;
                        ObjectsHelper.getInstance().rotateModel(r, 0, 0, 1);

                    }
                }
                break;
        }
        return true;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Radians
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        return (float) Math.atan2(delta_y, delta_x);

    }
}
