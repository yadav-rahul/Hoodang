package com.sdsmdg.game.LeaderBoard.API;

import com.sdsmdg.game.LeaderBoard.model.Scores;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by Rahul Yadav on 6/23/2016.
 */
public interface dbapi {

    @GET("scores/?format=json")
    Call<List<Scores>> getScores();

    //Since Retrofit 1.9 uses Gson by default, the FooRequest instances
    // will be serialized as JSON as the sole body of the request.
    @FormUrlEncoded
    @POST("scores/")
    Call<Scores> addScore(
            @Field("name") String name,
            @Field("score") int score
    );
    

}
