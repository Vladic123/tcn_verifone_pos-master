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
 * activity class for second tutorial screeen
 */


public class tcn_verifone_tutorial_step_two extends AppCompatActivity implements Animation.AnimationListener{
    private ViewFlipper flipper;
    private Context context;

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tutorial_step_two);

        context = this;

        Button backButton = findViewById(R.id.back_button);
        CardView nextButton = findViewById(R.id.done_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });


        flipper = findViewById(R.id.image_flipper);
        flipper.setInAnimation(context, android.R.anim.fade_in);
        flipper.setOutAnimation(context, android.R.anim.fade_out);
        flipper.getInAnimation().setAnimationListener(this);
        flipper.setFlipInterval(1500);
        flipper.startFlipping();

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