package com.sdsmdg.game.LeaderBoard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sdsmdg.game.GameWorld.SinglePlayer;
import com.sdsmdg.game.LeaderBoard.API.dbapi;
import com.sdsmdg.game.LeaderBoard.LocalDB.DBHandler;
import com.sdsmdg.game.LeaderBoard.model.Scores;
import com.sdsmdg.game.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeaderBoard extends AppCompatActivity {

    private final String BASE_URL = "http://rauhly247.pythonanywhere.com/";
    public String TAG = "com.sdsmdg.game";
    ArrayAdapter adapter;
    Button postButton;
    List<Scores> scoresList;
    ListView scoreListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        scoreListView = (ListView) findViewById(R.id.scoreListView);
        postButton = (Button) findViewById(R.id.post_button);
        getScores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBHandler dbHandler = new DBHandler(getApplicationContext());
        if (dbHandler.checkDatabase()) {
            postButton.setEnabled(false);
        }
    }

    public void getScores() {
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbapi api = retrofit.create(dbapi.class);
        Call<List<Scores>> call = api.getScores();
        call.enqueue(new Callback<List<Scores>>() {
            @Override
            public void onResponse(Call<List<Scores>> call, Response<List<Scores>> response) {
                if (response.body() != null) {
                    scoresList = response.body();
                    loading.dismiss();
                    showList();
                } else {
                    Toast.makeText(LeaderBoard.this, "Be the first to post ur Score !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Scores>> call, Throwable t) {

            }
        });
    }

    public void postButtonClicked(View view) {
        final DBHandler dbHandler = new DBHandler(getApplicationContext());
        if (dbHandler.getToken() == 1) {
            Log.i(TAG, "New User");
            final ProgressDialog loading = ProgressDialog.show(this, "Sending Data", "Please wait...", false, false);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            dbapi api = retrofit.create(dbapi.class);
            Call<Scores> addScore = api.addScore(dbHandler.getUserName(), dbHandler.getPastHighScore());
            addScore.enqueue(new Callback<Scores>() {
                @Override
                public void onResponse(Call<Scores> call, Response<Scores> response) {
                    loading.dismiss();
                    if (response.body() != null) {
                        updateList();
                        dbHandler.changeToken(0);
                    } else {
                        //TODO Add a method to replace the user name.
                        Toast.makeText(LeaderBoard.this, "User with this name already exists !", Toast.LENGTH_LONG).show();
                        SinglePlayer.changeUserName = true;

                    }
                }

                @Override
                public void onFailure(Call<Scores> call, Throwable t) {
                    Toast.makeText(LeaderBoard.this, "Please try again !", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.i(TAG, "Old User");
            final ProgressDialog loading = ProgressDialog.show(this, "Updating Score", "Please wait...", false, false);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            dbapi api = retrofit.create(dbapi.class);

            Scores scores = new Scores(dbHandler.getUserName(), dbHandler.getPastHighScore());
            Call<String> updateScore = api.updateScore(scores);
            updateScore.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    // Log.i(TAG, response.body());
                    loading.dismiss();
                    updateList();
                    Toast.makeText(LeaderBoard.this, "HighScore has been successfully updated !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(LeaderBoard.this, "Try again later !", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateList() {
        final ProgressDialog loading = ProgressDialog.show(this, "Updating List", "Please wait...", false, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbapi api = retrofit.create(dbapi.class);
        Call<List<Scores>> call = api.getScores();
        call.enqueue(new Callback<List<Scores>>() {
            @Override
            public void onResponse(Call<List<Scores>> call, Response<List<Scores>> response) {
                if (response.body() != null) {
                    scoresList = response.body();
                    loading.dismiss();
                    showList();
                }
            }

            @Override
            public void onFailure(Call<List<Scores>> call, Throwable t) {

            }
        });
    }

    public void showList() {
        adapter = new CustomAdapter(this, scoresList);
        scoreListView.setAdapter(adapter);
    }
}
