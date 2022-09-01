package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ViewFlipper;

/**
 * activity class for first tutorial screeen
 */

public class tcn_verifone_tutorial_step_one extends AppCompatActivity implements Animation.AnimationListener {

    private Context context;
    private ViewFlipper flipper;

    private static final int TUTORIAL_STEP_TWO = 1;

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tutorial_step_one);

        context = this;
        Button backButton = findViewById(R.id.tutorial_back_button);
        CardView nextButton = findViewById(R.id.next_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,tcn_verifone_tutorial_step_two.class);
                startActivityForResult(intent,TUTORIAL_STEP_TWO);

            }
        });

        flipper = findViewById(R.id.image_flipper_one);
        flipper.setInAnimation(context, android.R.anim.fade_in);
        flipper.setOutAnimation(context, android.R.anim.fade_out);
        flipper.getInAnimation().setAnimationListener(this);
        flipper.setFlipInterval(1500);
        flipper.startFlipping();


    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            finish();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        // Ваш код
        if(flipper.indexOfChild(flipper.getCurrentView()) == flipper.getChildCount()){
            flipper.stopFlipping();
            finish();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

}