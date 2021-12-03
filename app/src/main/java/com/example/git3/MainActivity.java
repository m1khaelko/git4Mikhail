package com.example.git3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  MainActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    TextView mTextView;
    Button button;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editTextTextPersonName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);

                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                // часть слова
                final Call<GitResult> call =
                        gitHubService.getUsers(String.valueOf(editText.getText()));

                call.enqueue(new Callback<GitResult>() {
                    @Override
                    public void onResponse(Call<GitResult> call, Response<GitResult> response) {
                        // response.isSuccessful() is true if the response code is 2xx
                        if (response.isSuccessful()) {
                            GitResult result = response.body();

                            // Получаем json из github-сервера и конвертируем его в удобный вид
                            // Покажем только первого пользователя
                            String user = "Аккаунт Github: " + result.getItems().get(0).getLogin();
                            mTextView.setText(user);
                            Log.i("Git", String.valueOf(result.getItems().size()));

                            mProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            int statusCode = response.code();

                            // handle request errors yourself
                            ResponseBody errorBody = response.errorBody();
                            try {
                                mTextView.setText(errorBody.string());
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GitResult> call, Throwable throwable) {
                        mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                    }
                });
            }
        });
    }
}