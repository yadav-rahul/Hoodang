package com.sdsmdg.game.GameWorld;

import android.graphics.RectF;

/**
 * Created by Rahul Yadav on 5/24/2016.
 */
public interface Ball {
    RectF rectFBall = new RectF();
    RectF rectFSecondBall = new RectF();

    boolean initializeBallPosition(int x, int y);

    boolean initializeBallVelocity(int x, int y);

    boolean updateBall();

    boolean collide(int x);

    float velocityBooster(float x);
}
