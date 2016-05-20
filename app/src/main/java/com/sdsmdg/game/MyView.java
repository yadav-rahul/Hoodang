package com.sdsmdg.game;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback {

    private final Paint paint;
    private RectF rectF;
    private RenderThread renderThread;

    private float vX;
    private float vY;

    public MyView(Context context) {
        super(context);

        getHolder().addCallback(this);
        renderThread = new RenderThread(getHolder(),this);

        setFocusable(true);
        paint = new Paint();
        paint.setColor(0xFFFFFAAF);
        paint.setAlpha(192);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        rectF= new RectF();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    private class RenderThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private MyView myView;
        public RenderThread(SurfaceHolder surfaceHolder, MyView myView){
            this.surfaceHolder = surfaceHolder;
            this.myView = myView;
        }
    }
}
