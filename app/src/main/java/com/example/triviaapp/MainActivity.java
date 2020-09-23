package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String MESSAGE_ID = "highScore" ;
    private TextView questionTextView;
    private Button trueButton;
    private Button falseButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList ;
    private TextView score;
    private TextView highestScore;
    private int scoreNumber = 0;
    private TextView showHighestScore;
    private int index;
    private Random rand = new Random();
    private TextView timerTextView;
    private Button playAgainButton;
    private Button goButton;
    private ConstraintLayout gameLayout1;
    private ConstraintLayout gameLayout2;
    private int value = 0;
    private int check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        questionTextView = findViewById(R.id.questionTextview);
        score = findViewById(R.id.scoreTextView);
        highestScore = findViewById(R.id.highestScoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        playAgainButton = findViewById(R.id.playAgainButton);
        goButton = findViewById(R.id.goButton);
        gameLayout1 = findViewById(R.id.gameLayout1);
        gameLayout2 = findViewById(R.id.gameLayout2);
        showHighestScore = findViewById(R.id.showHighScoreTextView);
        showHighestScore.setVisibility(View.INVISIBLE);


        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        questionList =  new ArrayList<Question>();
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                Log.d("Inside", questionArrayList.toString());

                questionTextView.setText(questionArrayList.get(rand.nextInt(questionArrayList.size())).getAnswer());
                score.setText("Your score:" + scoreNumber + " / " + questionList.size());
            }
        });
        questionTextView.setMovementMethod(new ScrollingMovementMethod());

        gameLayout1.setVisibility(View.INVISIBLE);
        gameLayout2.setVisibility(View.INVISIBLE);
        setHighScore();
        //Log.d("InsideQuestion", questionList.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.falseButton:
                checkAnswer(false);
                questionUpdate();
                break;
            case R.id.trueButton:
                checkAnswer(true);
                questionUpdate();

        }
    }

    private void checkAnswer(boolean b) {
        boolean answer = questionList.get(index).isAnswerTrue();
        int toastMessageId = 0;
        if (b == answer){
            fadeView();
            toastMessageId = R.string.Correct;
            scoreNumber ++;
            score.setText( scoreNumber + " / " + questionList.size());
        }else{
            shakeAnimation();
            toastMessageId = R.string.Wrong;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }


    private void questionUpdate() {
        check = index;
        index = rand.nextInt(questionList.size());
        if(index == check){
            index = rand.nextInt(questionList.size());
        }
        questionTextView.setText(questionList.get(index).getAnswer());
        questionTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.startAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public  void setHighScore() {
        if (scoreNumber > value) {
            SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highScore", scoreNumber);
            editor.apply();//saving to disk!
            showHighestScore.setText("Your highest score: " + scoreNumber );
            showHighestScore.setVisibility(View.VISIBLE);
        }
        //Get Data back from shared preferences
        SharedPreferences getShareData = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
        value = getShareData.getInt("highScore", 0);
        highestScore.setText("Highest Score: " + value);
    }



    public  void  playAgain (View view){
        showHighestScore.setVisibility(View.INVISIBLE);
        gameLayout2.setVisibility(View.VISIBLE);
        scoreNumber = 0;
        timerTextView.setText(R.string.timer);
        score.setText(Integer.toString(scoreNumber)+ " / " + questionList.size());
        playAgainButton.setVisibility(View.INVISIBLE);
        new CountDownTimer(31000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("00:" + String.valueOf(millisUntilFinished/1000) + "s");
            }

            @Override
            public void onFinish() {
                setHighScore();
                playAgainButton.setVisibility(View.VISIBLE);
                gameLayout2.setVisibility(View.INVISIBLE);
            }
        }.start();

    }
    public void start(View view) {
        goButton.setVisibility(View.INVISIBLE);
        gameLayout1.setVisibility(View.VISIBLE);
        gameLayout2.setVisibility(View.VISIBLE);
        playAgain(findViewById(R.id.timerTextView));
    }
}
