package com.app.mg.aoe.upc.Activities;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.app.mg.aoe.upc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AvatarSelection extends AppCompatActivity {

    ViewFlipper v_flipper;
    Button prev_Button, next_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);

        prev_Button = findViewById(R.id.prev_button);
        next_Button = findViewById(R.id.next_button);

        /*
        addSlide(AppIntroFragment.newInstance("Batman", "Batman", R.drawable.batman, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Captain America", "Captain America", R.drawable.captain, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Black Widow", "Black Widow", R.drawable.blackwidow, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Iron Man", "Iron Man", R.drawable.ironman, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Tomb Raider", "Tomb Raider", R.drawable.tombraider, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Wonder Women", "Wonder Women", R.drawable.wonderwomen, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        */

        int images[] = {R.drawable.batman, R.drawable.captain, R.drawable.blackwidow, R.drawable.ironman, R.drawable.tombraider, R.drawable.wonderwomen};

        v_flipper = findViewById(R.id.v_flipper);

        //try
        //v_flipper.addView(this.addContentView(this,););
        for(int i=0; i<images.length; i++){
            setFlipperImage(images[i]);
        }
        //end try

        for(int image: images){
            flipperImages(image);
        }

        prev_Button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //v_flipper.setInAnimation(AvatarSelection.this, android.R.anim.slide_out_right);
                //v_flipper.setOutAnimation(AvatarSelection.this, android.R.anim.slide_in_left);

                v_flipper.showPrevious();
            }
        });

        next_Button.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //v_flipper.setInAnimation(AvatarSelection.this, android.R.anim.slide_in_left);
                //v_flipper.setOutAnimation(AvatarSelection.this, android.R.anim.slide_out_right);

                v_flipper.showNext();
            }
        }));
    }

    public void flipperImages(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        /*
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(3000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this, android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this, android.R.anim.slide_out_right);
        */

        v_flipper.showPrevious();
    }

    private void setFlipperImage(int res){
        ImageView avatar = new ImageView(getApplicationContext());
        avatar.setBackgroundResource(res);
        v_flipper.addView(avatar);
    }

    /*
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }
    */
}