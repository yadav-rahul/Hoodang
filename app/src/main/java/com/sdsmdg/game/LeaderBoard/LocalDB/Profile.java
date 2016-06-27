package com.sdsmdg.game.LeaderBoard.LocalDB;

/**
 * Created by Rahul Yadav on 6/24/2016.
 */
public class Profile {
    private int score;
    private String userName;
    private int token;

    public Profile() {
    }

    public Profile(String userName, int score, int token) {
        this.score = score;
        this.token = token;
        this.userName = userName;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
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