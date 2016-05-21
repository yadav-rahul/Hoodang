package com.sdsmdg.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sdsmdg.game.GameWorld.GameWorld;


/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback {

    //Surface holder help in monitoring the changes

    private final int boardWidth = (GameWorld.width) / 5;
    private final int boardHeight = (GameWorld.height)/50;
    private final float dT = 0.5f;
    private final Paint paint;
    String TAG = "com.sdsmdg.game";
    private RectF rectF;
    private GameWorld.RenderThread renderThread;

    private float vX, vY;
    private int xCenter, yCenter;

    public MyView(Context context) {
        super(context);
        Log.i(TAG, "Constructor of shape view starts");
        getHolder().addCallback(this);
        renderThread = new GameWorld.RenderThread(getHolder(), this);

        setFocusable(true);
        paint = new Paint();
        paint.setColor(0xFFFFFAAF);
        paint.setAlpha(192);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        rectF = new RectF();
        Log.i(TAG, "Constructor ends");
    }

    public boolean setBoardAtCenter(int x, int y) {
        //Default position at the start of the Game
        xCenter = x;
        yCenter = (int) (y - 2.5 * boardHeight);

        return true;
    }

    public boolean updateBoardCenter() {
        vX -= (GameWorld.aX) * dT;
        xCenter += (int) (dT * (vX + 0.5 * GameWorld.aX * dT));

        //yCenter = 20;

        if (xCenter < boardWidth / 2) {
            xCenter = boardWidth / 2;
            vX = 0;
        }
        if (xCenter > GameWorld.width - (boardWidth / 2)) {
            xCenter = (GameWorld.width - (boardWidth / 2));
            vX = 0;
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (rectF != null) {
            rectF.set(xCenter - (boardWidth / 2), yCenter + (boardHeight / 2), xCenter + (boardWidth / 2), yCenter - (boardHeight / 2));
            canvas.drawColor(0XFF000000);
            canvas.drawRect(rectF, paint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        renderThread.setRunning(true);
        renderThread.start();

        Log.i(TAG, "Surface created called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.i(TAG, "Surface changed called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.setRunning(false);

        Log.i(TAG, "Surface destroyed called");
    }


}
