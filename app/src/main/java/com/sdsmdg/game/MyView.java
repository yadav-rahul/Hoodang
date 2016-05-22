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
    private final int boardHeight = (GameWorld.height) / 50;
    private final float dT = 0.3f;
    private final Paint paint;
    String TAG = "com.sdsmdg.game";
    private RectF rectFB1, rectFB2;
    private GameWorld.RenderThread renderThread;

    private float vB1X, vB2X;
    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;

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

        rectFB1 = new RectF();
        rectFB2 = new RectF();
        Log.i(TAG, "Constructor ends");
    }

    public boolean setBoardOneAtCenter(int x, int y) {
        //Default position at the start of the Game
        xB1Center = x;
        yB1Center = (y - boardHeight);

        return true;
    }

    public boolean setBoardTwoAtCenter(int x, int y) {
        //Default position at the start of the Game
        xB2Center = x;
        yB2Center = (y + boardHeight);

        return true;
    }


    public boolean updateB1Center() {
        if (Math.abs(GameWorld.aB1X) < 1) {
            vB1X = 0;
        } else {
            if (GameWorld.aB1X < 0) {
                vB1X = GameWorld.width / 36;
            } else {
                vB1X = -GameWorld.width / 36;
            }
        }

        xB1Center += (int) (vB1X * dT);


        if (xB1Center < boardWidth / 2) {
            xB1Center = boardWidth / 2;
            vB1X = 0;
        }
        if (xB1Center > GameWorld.width - (boardWidth / 2)) {
            xB1Center = (GameWorld.width - (boardWidth / 2));
            vB1X = 0;
        }

        return true;
    }

    public boolean updateB2Center() {

        if (Math.abs(GameWorld.aB1X) < 1) {
            vB2X = 0;
        } else {
            if (GameWorld.aB1X < 0) {
                vB2X = GameWorld.width / 36;
            } else {
                vB2X = -GameWorld.width / 36;
            }
        }

        xB2Center += (int) (vB2X * dT);


        if (xB2Center < boardWidth / 2) {
            xB2Center = boardWidth / 2;
            vB2X = 0;
        }
        if (xB2Center > GameWorld.width - (boardWidth / 2)) {
            xB2Center = (GameWorld.width - (boardWidth / 2));
            vB2X = 0;
        }


        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0XFF000000);
        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (boardWidth / 2), yB1Center + (boardHeight / 2), xB1Center + (boardWidth / 2), yB1Center - (boardHeight / 2));

            canvas.drawRect(rectFB1, paint);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (boardWidth / 2), yB2Center + (boardHeight / 2), xB2Center + (boardWidth / 2), yB2Center - (boardHeight / 2));

            canvas.drawRect(rectFB2, paint);
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
