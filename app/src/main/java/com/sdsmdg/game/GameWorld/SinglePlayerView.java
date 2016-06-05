package com.sdsmdg.game.GameWorld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Rahul Yadav on 6/4/2016.
 */
public class SinglePlayerView extends SurfaceView implements SurfaceHolder.Callback, Ball, BoardOne, BoardTwo {


    private final int boardWidth = (SinglePlayer.width) / 5;
    private final int boardHeight = (SinglePlayer.height) / 50;
    private final float dT = 0.3f;
    private final Paint paintB1, paintB2, paintBall;
    String TAG = "com.sdsmdg.game";
    private SinglePlayer singlePlayer;
    private float vBallX, vBallY;
    private RectF rectFB1, rectFB2, rectInvisible;
    private SinglePlayer.RenderThread renderThread;
    private float vB1X, vB2X;
    private int xBallCenter, yBallCenter;
    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;
    private Context context;

    public SinglePlayerView(Context context, SinglePlayer singlePlayer) {
        super(context);
        this.singlePlayer = singlePlayer;

        getHolder().addCallback(this);
        renderThread = new SinglePlayer.RenderThread(getHolder(), this);

        setFocusable(true);
        paintB1 = new Paint();
        paintB1.setColor(0xFF3F51B5);
        paintB1.setAlpha(255);
        paintB1.setStyle(Paint.Style.FILL);
        paintB1.setAntiAlias(true);

        paintB2 = new Paint();
        paintB2.setColor(0xFF3F51B5);
        paintB2.setAlpha(255);
        paintB2.setStyle(Paint.Style.FILL);
        paintB2.setAntiAlias(true);

        paintBall = new Paint();
        paintBall.setColor(0xFFFF4081);
        paintBall.setAlpha(255);
        paintBall.setStyle(Paint.Style.FILL);
        paintBall.setAntiAlias(true);

        rectFB1 = new RectF();
        rectFB2 = new RectF();

        setBoardOneAtCenter(SinglePlayer.width / 2, SinglePlayer.height);
        setBoardTwoAtCenter(SinglePlayer.width / 2, 0);
        initializeBallPosition(SinglePlayer.width, SinglePlayer.height);
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

    @Override
    public boolean initializeBallPosition(int x, int y) {
        Log.i(TAG,"Ball position initialized");
        xBallCenter = x / 2;
        yBallCenter = y / 2;
        initializeBallVelocity(x, y);
        return true;
    }

    @Override
    public boolean initializeBallVelocity(int x, int y) {
        Log.i(TAG,"Ball velocity initialized");
        vBallX = x / 25;
        vBallY = y / 30;
        return true;
    }

    public boolean update() {
        updateB1Center();
        updateB2Center();
        updateBall();
        return true;
    }

    @Override
    public boolean updateBall() {
        xBallCenter += vBallX * dT;
        yBallCenter += vBallY * dT;

        if (xBallCenter < Ball.radius) {
            xBallCenter = Ball.radius;
            vBallX = -vBallX;
        } else if (xBallCenter > SinglePlayer.width - Ball.radius) {
            xBallCenter = SinglePlayer.width - Ball.radius;
            vBallX = -vBallX;
        } else if (yBallCenter < Ball.radius) {
            //P2 missed the ball
            //   popDialog(1);
            yBallCenter = Ball.radius;
            vBallY = -vBallY;

        } else if (yBallCenter > SinglePlayer.height - Ball.radius) {
            //P1 missed the ball
            singlePlayer.popDialog(2);

        }
        return true;
    }

    @Override
    public boolean collide(int x) {
        if (x == 1) {
            yBallCenter = (int) (SinglePlayer.height - boardHeight - Ball.radius);
            vBallY = -vBallY;
        } else if (x == 2) {
            yBallCenter = Ball.radius + boardHeight;
            vBallY = -vBallY;
        }
        return true;
    }

    @Override
    public boolean updateB1Center() {
        if (Math.abs(SinglePlayer.aB1X) < 1) {
            vB1X = 0;
        } else {
            if (SinglePlayer.aB1X < 0) {
                vB1X = SinglePlayer.width / 36;
            } else {
                vB1X = -SinglePlayer.width / 36;
            }
        }

        xB1Center += (int) (vB1X * dT);

        if (xB1Center < boardWidth / 2) {
            xB1Center = boardWidth / 2;
            vB1X = 0;
        }
        if (xB1Center > SinglePlayer.width - (boardWidth / 2)) {
            xB1Center = (SinglePlayer.width - (boardWidth / 2));
            vB1X = 0;
        }

        return true;
    }


    @Override
    public boolean updateB2Center() {
        //TODO Automatically update center of Board 2

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0XFFFFFFFF);
        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (boardWidth / 2), yB1Center + (boardHeight / 2), xB1Center + (boardWidth / 2), yB1Center - (boardHeight / 2));

            canvas.drawRect(rectFB1, paintB1);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (boardWidth / 2), yB2Center + (boardHeight / 2), xB2Center + (boardWidth / 2), yB2Center - (boardHeight / 2));

            canvas.drawRect(rectFB2, paintB2);
        }
        if (Ball.rectFBall != null) {
            Ball.rectFBall.set(xBallCenter - Ball.radius, yBallCenter - Ball.radius, xBallCenter + Ball.radius, yBallCenter + Ball.radius);

            canvas.drawOval(Ball.rectFBall, paintBall);
        }
        if (rectFB1 != null) {
            if (rectFBall.intersect(rectFB1)) {
                collide(1);
            } else if (rectFBall.intersect(rectFB2)) {
                collide(2);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        renderThread.setRunning(true);
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.setRunning(false);
    }
}
