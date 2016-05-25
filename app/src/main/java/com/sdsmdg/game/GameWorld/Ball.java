package com.sdsmdg.game.GameWorld;

import android.graphics.RectF;

/**
 * Created by Rahul Yadav on 5/24/2016.
 */
public interface Ball {
    int ballRadius = GameWorld.width / 70;
    RectF rectFBall = new RectF();

    public boolean initializeBall(int xBallCenter, int yBallCenter);

    public boolean updateBall();
}
