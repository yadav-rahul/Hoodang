package com.sdsmdg.game.LeaderBoard.LocalDB;

/**
 * Created by Rahul Yadav on 6/24/2016.
 */
public class Profile {
    private int score;
    private String userName;

    public Profile() {
    }

    public Profile(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}