package com.sdsmdg.game.GameWorld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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
    public static float vSecondBallX, vSecondBallY;


    private final int boardWidth2 = (Launcher.width) / 5;
    private final int boardHeight = (Launcher.height) / 50;
    private final float dT = 0.3f;
    private final Paint paintB1, paintB2, paintBall, paintSecondBall, paintInvisible, textPaint, circlePaint;
    String TAG = "com.sdsmdg.game";
    private int giftVelocity = Launcher.width / 25;
    private int giftTopPosition = 0;
    private int giftLeftPosition = Launcher.width / ((new Random().nextInt(10)) + 1);
    private boolean showGift = false;
    private SinglePlayer singlePlayer;
    private RectF rectFB1, rectFB2, rectFInvisibleB1, rectFInvisibleB2;
    private SinglePlayer.RenderThread renderThread;
    private float vB1X, vB2X;

    public static int xBallCenter, yBallCenter;
    public static int xSecondBallCenter, ySecondBallCenter;

    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;
    private Context context;
    private int[] ballDirection = new int[]{-1, 1};
    private long time;
    private int typeOfGift;
    private Bitmap bitmap;
    private Bitmap leftButton, rightButton;
    public static int numberOfHits = 1;
    public static int score = 0;
    private Canvas canvas;
    private int touchPosition;
    private Bitmap backgroundBitmap, scaledBitmap, rotatedBitmap;
    private boolean touchAction;
    private MediaPlayer mp;
    public static boolean showSecondBall = false;
    private boolean tokenToUpdateB2Center = false;
    private boolean moveToCenter = false;

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

        paintSecondBall = new Paint();
        paintSecondBall.setColor(0xFF5CB85C);
        paintSecondBall.setAlpha(255);
        paintSecondBall.setStyle(Paint.Style.FILL);
        paintSecondBall.setAntiAlias(true);


        paintInvisible = new Paint();
        paintInvisible.setColor(0xFFFFFFFF);
        paintInvisible.setAlpha(0);
        paintInvisible.setStyle(Paint.Style.FILL);
        paintInvisible.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAlpha(255);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Launcher.width / 10);

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAlpha(255);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);


        rectFB1 = new RectF();
        rectFB2 = new RectF();
        rectFInvisibleB1 = new RectF();
        rectFInvisibleB2 = new RectF();

        setBoardOneAtCenter(Launcher.width / 2, Launcher.height);
        setBoardTwoAtCenter(Launcher.width / 2, 0);
        initializeBallPosition(Launcher.width, Launcher.height);
        initializeSecondBallPosition();
        leftButton = BitmapFactory.decodeResource(getResources(), R.drawable.left_button);
        rightButton = BitmapFactory.decodeResource(getResources(), R.drawable.right_button);
        mp = MediaPlayer.create(singlePlayer.getApplicationContext(), R.raw.strike_sound);
        takeBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, Launcher.height
                , Launcher.width, true);
        rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
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
        score = 0;
        xBallCenter = x / 2;
        yBallCenter = y / 2;
        return true;
    }

    public boolean initializeSecondBallPosition() {
        xSecondBallCenter = Launcher.width / 2;
        ySecondBallCenter = Launcher.height / 2;
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

        vSecondBallX = (ballDirection[new Random().nextInt(ballDirection.length)]) * x / 25;
        vSecondBallY = -y / (25 + 9);
        return true;
    }

    public boolean initializeSecondBallVelocity() {
        vSecondBallX = (ballDirection[new Random().nextInt(ballDirection.length)]) * (SinglePlayer.width) / 25;
        vSecondBallY = -(SinglePlayer.height) / (25 + 9);
        return true;
    }

    public boolean update(boolean check) {
        if (check) {
            updateB1Center();
            updateBall();
            smartUpdateB2Center();
            if (numberOfHits % 4 == 0) {
                numberOfHits++;
                bitmap = dropGift();
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
            Gift.showGift(3);
        } else if (giftTopPosition > Launcher.height) {
            giftTopPosition = 0;
            giftLeftPosition = Launcher.width / ((new Random().nextInt(10)) + 1);
            showGift = false;
        }

        return true;
    }

    public boolean smartUpdateB2Center() {
        if (showSecondBall) {
            //Do update the center of the board for the ball closer to it !
            if (ySecondBallCenter < 4 * Launcher.height / 5 && vSecondBallY < 0 && vBallY > 0) {
                if (xSecondBallCenter >= xB2Center - boardWidth2 / 6 && xSecondBallCenter <= xB2Center + boardWidth2 / 6) {
                    tokenToUpdateB2Center = true;
                }
                updateB2CenterNew(tokenToUpdateB2Center);

            } else if (vBallY < 0 && yBallCenter < 4 * Launcher.height / 5) {
                if (xBallCenter >= xB2Center - boardWidth2 / 6 && xBallCenter <= xB2Center + boardWidth2 / 6) {
                    tokenToUpdateB2Center = true;
                }
                updateB2Center(tokenToUpdateB2Center);
            }
        } else {
            if (vBallY < 0 && yBallCenter < 4 * Launcher.height / 5) {
                if (xBallCenter >= xB2Center - boardWidth2 / 6 && xBallCenter <= xB2Center + boardWidth2 / 6) {
                    tokenToUpdateB2Center = true;
                }
                updateB2Center(tokenToUpdateB2Center);
            }
        }
        if (moveToCenter) {
            //check the present board location and update it to the center of the game world
            if (Math.abs((xB2Center) - (Launcher.width / 2)) < boardWidth2 / 8) {
                //Do nothing
            } else {
                if (xB2Center >= Launcher.width / 2) {
                    //move in left direction
                    xB2Center -= Math.abs(vSecondBallX * dT);
                } else {
                    //move in right direction
                    xB2Center += Math.abs(vSecondBallX * dT);
                }
            }

        }

        return true;
    }

    public Bitmap dropGift() {
        typeOfGift = Gift.getTypeOfGift();
        Log.i(TAG, "dropGift called and type of gift is : " + typeOfGift);
        switch (typeOfGift) {
            case 1:
                return BitmapFactory.decodeResource(getResources(), R.drawable.speed_gift);
            case 2:
                return BitmapFactory.decodeResource(getResources(), R.drawable.size_gift);
            case 3:
                return BitmapFactory.decodeResource(getResources(), R.drawable.multi_ball_gift);
        }
        return null;
    }

    @Override
    public boolean updateBall() {
        time = (System.currentTimeMillis() / 1000) - Launcher.startTime + 1;

        if (showSecondBall) {
            if (time % 20 == 0) {
                vSecondBallY = velocityBooster(vSecondBallY);
            }
            xSecondBallCenter += vSecondBallX * dT;
            ySecondBallCenter += vSecondBallY * dT;

            if (xSecondBallCenter < radius) {
                xSecondBallCenter = radius;
                vSecondBallX = -vSecondBallX;
            } else if (xSecondBallCenter > Launcher.width - radius) {
                xSecondBallCenter = Launcher.width - radius;
                vSecondBallX = -vSecondBallX;
            } else if (ySecondBallCenter < 0) {
                showSecondBall = false;
                initializeSecondBallPosition();
                initializeSecondBallVelocity();
            } else if (ySecondBallCenter > Launcher.height) {
                showSecondBall = false;
                initializeSecondBallPosition();
                initializeSecondBallVelocity();
            }
        }

        if (time % 20 == 0) {
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
            showSecondBall = false;
            singlePlayer.popDialog(score);
        }
        return true;
    }

    @Override
    public boolean collide(int x) {
        if(Launcher.isSound){
            mp.start();
        }

        if (x == 1) {
            numberOfHits++;
            score++;
            yBallCenter = (int) (Launcher.height - (4 * boardHeight / 2) - radius);
            vBallY = -vBallY;
            if (Math.abs(xBallCenter - xB1Center) < (Math.abs(radius))) {
                vBallX = (float) (vBallX / 1.005);
            } else {
                vBallX = (float) (1.05 * vBallX);
            }

        } else if (x == 2) {
            yBallCenter = radius + (4 * boardHeight / 2);
            vBallY = -vBallY;
            if ((xB2Center < Launcher.width / 3 && vBallX > 0) || (xB2Center > Launcher.width / 3 && vBallX < 0)) {
                vBallX = -vBallX;
            }
            tokenToUpdateB2Center = false;
            moveToCenter = true;
        }
        if (x == 3) {
            score++;
            numberOfHits++;
            ySecondBallCenter = (int) (Launcher.height - (4 * boardHeight / 2) - radius);
            vSecondBallY = -vSecondBallY;
            if (Math.abs(xSecondBallCenter - xB1Center) < (Math.abs(radius))) {
                vSecondBallX = (float) (vSecondBallX / 1.05);
            } else {
                vSecondBallX = (float) (1.05 * vSecondBallX);
            }
            score++;
        } else if (x == 4) {
            ySecondBallCenter = radius + (4 * boardHeight / 2);
            vSecondBallY = -vSecondBallY;
            tokenToUpdateB2Center = false;
            moveToCenter = true;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPosition = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchAction = true;
                break;
            case MotionEvent.ACTION_UP:
                touchAction = false;
                break;
        }
        return true;
    }

    @Override
    public boolean updateB1Center() {
        if (Launcher.sensorMode) {
            if (Math.abs(SinglePlayer.aB1X) < 1) {
                vB1X = 0;
            } else {
                if (SinglePlayer.aB1X < 0) {
                    vB1X = Launcher.width / 25;
                } else {
                    vB1X = -Launcher.width / 25;
                }
            }
        } else {
            if (touchAction) {
                if (touchPosition < Launcher.width / 2) {
                    vB1X = -Launcher.width / 25;
                } else if (touchPosition > Launcher.width / 2) {
                    vB1X = Launcher.width / 25;
                }
            } else {
                vB1X = 0;
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
    public boolean updateB2Center(boolean token) {
        if (token) {
            xB2Center = xBallCenter;
            if (xB2Center < boardWidth2 / 2) {
                xB2Center = boardWidth2 / 2;
                vB2X = 0;
            }
            if (xB2Center > Launcher.width - (boardWidth2 / 2)) {
                xB2Center = (Launcher.width - (boardWidth2 / 2));
                vB2X = 0;
            }
        }
        return true;
    }

    public boolean updateB2CenterNew(boolean token) {
        if (token) {
            xB2Center = xSecondBallCenter;
            if (xB2Center < boardWidth2 / 2) {
                xB2Center = boardWidth2 / 2;
                vB2X = 0;
            }
            if (xB2Center > Launcher.width - (boardWidth2 / 2)) {
                xB2Center = (Launcher.width - (boardWidth2 / 2));
                vB2X = 0;
            }
        }

        return true;
    }

    public void takeBitmap() {
        int[] number = new int[]{1, 2, 3};
        int index = (number[new Random().nextInt(number.length)]);
        switch (index) {
            case 1:
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blue_back);
                textPaint.setColor(0xFF605E5F);
                break;
            case 2:
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.green_back);
                textPaint.setColor(0xFF3F8522);
                break;
            case 3:
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_back);
                textPaint.setColor(0xFF748E69);
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;

        //Randomly draw any of the three available background
        //canvas.drawColor(0XFFFFFFFF);
        canvas.drawBitmap(rotatedBitmap, 0, 0, null);
        canvas.drawCircle(Launcher.width / 2, Launcher.height / 2, Launcher.width / 8
                , circlePaint);

        canvas.drawText(String.valueOf(score), Launcher.width / 2,
                Launcher.height / 2 + Launcher.width / 40, textPaint);

        if (Launcher.showButtons) {
            canvas.drawBitmap(leftButton, 10, (Launcher.height - (Launcher.height / 8)), paintBall);
            canvas.drawBitmap(rightButton, (Launcher.width - (Launcher.width / 5)),
                    (Launcher.height - (Launcher.height / 8)), paintBall);
        }

        if (showGift) {
            canvas.drawBitmap(bitmap, giftLeftPosition, giftTopPosition, paintBall);
        }

        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (boardWidth1 / 2), yB1Center + (boardHeight / 2), xB1Center + (boardWidth1 / 2), yB1Center - (boardHeight / 2));
            rectFInvisibleB1.set(xB1Center - (boardWidth1 / 2), yB1Center - (boardHeight / 2), xB1Center + (boardWidth1 / 2), yB1Center - (3 * boardHeight / 2));
            canvas.drawOval(rectFInvisibleB1, paintInvisible);
            canvas.drawOval(rectFB1, paintB1);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (boardWidth2 / 2), yB2Center + (boardHeight / 2), xB2Center + (boardWidth2 / 2), yB2Center - (boardHeight / 2));
            rectFInvisibleB2.set(xB2Center - (boardWidth2 / 2), yB2Center + (3 * boardHeight / 2), xB2Center + (boardWidth2 / 2), yB2Center + (boardHeight / 2));
            canvas.drawOval(rectFInvisibleB2, paintInvisible);
            canvas.drawOval(rectFB2, paintB2);
        }
        if (Ball.rectFBall != null) {
            Ball.rectFBall.set(xBallCenter - radius, yBallCenter - radius, xBallCenter + radius, yBallCenter + radius);

            canvas.drawOval(Ball.rectFBall, paintBall);
        }
        if (Ball.rectFSecondBall != null && showSecondBall == true) {
            Ball.rectFSecondBall.set(xSecondBallCenter - radius, ySecondBallCenter - radius, xSecondBallCenter + radius,
                    ySecondBallCenter + radius);

            canvas.drawOval(Ball.rectFSecondBall, paintSecondBall);
        }
        if (rectFB1 != null) {
            if (rectFBall.intersect(rectFInvisibleB1)) {
                collide(1);
            } else if (rectFBall.intersect(rectFInvisibleB2)) {
                collide(2);
            }
        }
        if (rectFB1 != null && showSecondBall == true) {
            if (rectFSecondBall.intersect(rectFInvisibleB1)) {
                collide(3);
            } else if (rectFSecondBall.intersect(rectFInvisibleB2)) {
                collide(4);
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
