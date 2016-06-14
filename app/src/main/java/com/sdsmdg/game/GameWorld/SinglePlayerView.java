package com.sdsmdg.game.GameWorld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sdsmdg.game.Gifts.Gift;
import com.sdsmdg.game.Launcher;
import com.sdsmdg.game.R;

import java.util.Random;

/**
 * Created by Rahul Yadav on 6/4/2016.
 */
public class SinglePlayerView extends SurfaceView implements SurfaceHolder.Callback, Ball, BoardOne, BoardTwo {

    public static int radius;
    public static int boardWidth1;
    public static float vBallX, vBallY;
    private final int boardWidth2 = (Launcher.width) / 5;
    private final int boardHeight = (Launcher.height) / 50;
    private final float dT = 0.3f;
    private final Paint paintB1, paintB2, paintBall;
    String TAG = "com.sdsmdg.game";
    private int giftVelocity = Launcher.width / 25;
    private int giftTopPosition = 0;
    private int giftLeftPosition = Launcher.width / ((new Random().nextInt(10)) + 1);
    private boolean showGift = false;
    private SinglePlayer singlePlayer;
    private RectF rectFB1, rectFB2, rectInvisible;
    private SinglePlayer.RenderThread renderThread;
    private float vB1X, vB2X;
    private int xBallCenter, yBallCenter;
    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;
    private Context context;
    private int[] ballDirection = new int[]{-1, 1};
    private long time;

    public SinglePlayerView(Context context, SinglePlayer singlePlayer) {
        super(context);
        this.singlePlayer = singlePlayer;

        getHolder().addCallback(this);
        renderThread = new SinglePlayer.RenderThread(getHolder(), this);
        setFocusable(true);
        boardWidth1 = (Launcher.width) / 5;
        radius = Launcher.width / 25;
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

        setBoardOneAtCenter(Launcher.width / 2, Launcher.height);
        setBoardTwoAtCenter(Launcher.width / 2, 0);
        initializeBallPosition(Launcher.width, Launcher.height);
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
        xBallCenter = x / 2;
        yBallCenter = y / 2;
        return true;

    }

    @Override
    public float velocityBooster(float velocity) {
        //Here time is the time passed in Game
        return (float) (velocity * (1 + (1 * 0.004)));
    }

    @Override
    public boolean initializeBallVelocity(int x, int y) {
        vBallX = (ballDirection[new Random().nextInt(ballDirection.length)]) * x / 25;
        vBallY = -y / (25 + 9);
        return true;
    }

    public boolean update(boolean check) {
        if (check) {
            updateB1Center();
            updateBall();
            smartUpdateB2Center();
            if (time % 15 == 0) {
                showGift = true;
            }
            if (showGift) {
                updateGift();
            }
        }
        return true;
    }

    public boolean updateGift() {
        giftTopPosition += giftVelocity * dT;
        if (giftTopPosition + 90 >= Launcher.height - boardHeight && Math.abs(giftLeftPosition + 22 - xB1Center) < (boardWidth1 / 2 + (22))) {
            Log.i(TAG, "Gift just collide with Board");
            giftLeftPosition = Launcher.width / ((new Random().nextInt(10)) + 1);
            giftTopPosition = 0;
            showGift = false;
            Gift.showGift();
        } else if (giftTopPosition > Launcher.height) {
            giftTopPosition = 0;
            giftLeftPosition = Launcher.width / ((new Random().nextInt(10)) + 1);
            showGift = false;
        }

        return true;
    }

    public boolean smartUpdateB2Center() {
        if (vBallY < 0 && yBallCenter < Launcher.height / 2) {
            updateB2Center();
        }
        return true;
    }

    public Bitmap dropGift() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gift);
        return bitmap;
    }

    @Override
    public boolean updateBall() {

        time = (System.currentTimeMillis() / 1000) - Launcher.startTime + 1;
        if (time % 20 == 0) {
            vBallX = velocityBooster(vBallX);
            vBallY = velocityBooster(vBallY);
        }
        xBallCenter += vBallX * dT;
        yBallCenter += vBallY * dT;

        if (xBallCenter < radius) {
            xBallCenter = radius;
            vBallX = -vBallX;
        } else if (xBallCenter > Launcher.width - radius) {
            xBallCenter = Launcher.width - radius;
            vBallX = -vBallX;
        } else if (yBallCenter < radius) {
            yBallCenter = radius;
            vBallY = -vBallY;
        } else if (yBallCenter > Launcher.height) {
            //P1 missed the ball
            singlePlayer.popDialog(2);
        }
        return true;
    }

    @Override
    public boolean collide(int x) {
        if (x == 1) {
            yBallCenter = (int) (Launcher.height - boardHeight - radius);
            vBallY = -vBallY;
        } else if (x == 2) {
            yBallCenter = radius + boardHeight;
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
                vB1X = Launcher.width / 25;
            } else {
                vB1X = -Launcher.width / 25;
            }
        }
        xB1Center += (int) (vB1X * dT);
        if (xB1Center < boardWidth1 / 2) {
            xB1Center = boardWidth1 / 2;
            vB1X = 0;
        }
        if (xB1Center > Launcher.width - (boardWidth1 / 2)) {
            xB1Center = (Launcher.width - (boardWidth1 / 2));
            vB1X = 0;
        }
        return true;
    }

    @Override
    public boolean updateB2Center() {
        //TODO Automatically update center of Board 2

        xB2Center = xBallCenter;
        if (xB2Center < boardWidth2 / 2) {
            xB2Center = boardWidth2 / 2;
            vB2X = 0;
        }
        if (xB2Center > Launcher.width - (boardWidth2 / 2)) {
            xB2Center = (Launcher.width - (boardWidth2 / 2));
            vB2X = 0;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0XFFFFFFFF);

        if (showGift) {
            canvas.drawBitmap(dropGift(), giftLeftPosition, giftTopPosition, paintBall);
        }

        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (boardWidth1 / 2), yB1Center + (boardHeight / 2), xB1Center + (boardWidth1 / 2), yB1Center - (boardHeight / 2));

            canvas.drawOval(rectFB1, paintB1);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (boardWidth2 / 2), yB2Center + (boardHeight / 2), xB2Center + (boardWidth2 / 2), yB2Center - (boardHeight / 2));

            canvas.drawOval(rectFB2, paintB2);
        }
        if (Ball.rectFBall != null) {
            Ball.rectFBall.set(xBallCenter - radius, yBallCenter - radius, xBallCenter + radius, yBallCenter + radius);

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
