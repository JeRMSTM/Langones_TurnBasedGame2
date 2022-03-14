package com.example.langones_turnbasedgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;
import java.util.Random;

@SuppressLint("SetTextI18n")

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    ProgressBar heroHpBar,heroMpBar,monsHpBar,monsMpBar;
    Button fight,rest;
    TextView mheroName,mmonsName,menuText,mwinIndicator;
    ConstraintLayout menuBox;

    //Nature's Prophet Stats
    int heroHp = 500;
    int heroMaxHp = 500;
    int heroMp = 100;
    int heroMaxMp = 100;
    int heroDamage;
    //HeroHp
    int heroHpPercent,heroMpPercent;
    String heroName = "Furion";

    //Mage Stats
    String monsName = "Mage";
    int mageHp = 300;
    int mageMaxHp = 300;
    int mageMp = 100;
    int mageMaxMp = 100;
    int mageDamage;
    //MonsHp
    int monsHpPercent,monsMpPercent;

    /////Skill Info/////
    //Fight
    int fightMinDMG = 35;
    int fightMaxDMG = 60;
    int fightATKChance = 80;
    int fightATKManaCost = 0;
    //Rest
    int restHealMin = 60;
    int restHealMax = 110;
    int restHealChance = 65;
    int restHealManaCost = 50;
    int healing;
    //random number
    int random;
    //Mage Attack
    int mageAtkMin = 40;
    int mageAtkMax = 100;
    int mageATKChance = 60;
    //turn system
    int turn = 1;


    //Button state
    boolean herofight = false;
    boolean heroRest = false;


    boolean heroWin = false;


    //Progress Bars
    public void progressbar() {
        //formula used to get health and mana percentage
        heroHpPercent = heroHp * 100 / heroMaxHp;
        heroMpPercent = heroMp * 100 / heroMaxMp;
        monsHpPercent = mageHp * 100 / mageMaxHp;
        monsMpPercent = mageMp * 100 / mageMaxMp;
        //setting up hp and mp bar
        heroHpBar.setProgress(heroHpPercent, true);
        heroMpBar.setProgress(heroMpPercent, true);
        monsHpBar.setProgress(monsHpPercent, true);
        monsMpBar.setProgress(monsMpPercent, true);
    }

    public void showButton(){
        fight.setVisibility(View.VISIBLE);
        rest.setVisibility(View.VISIBLE);
        fight.setClickable(true);
        rest.setClickable(true);
        menuText.setVisibility(View.GONE);
        menuBox.setClickable(false);
    }

    public void hideButton(){
        fight.setVisibility(View.GONE);
        rest.setVisibility(View.GONE);
        fight.setClickable(false);
        rest.setClickable(false);
        menuText.setVisibility(View.VISIBLE);
        menuBox.setClickable(true);
    }

    //Checks which character gets the turn
    public void turnSystem(){
        if (turn ==1){
            showButton();
        }else {
            hideButton();
        }
    }

    //moving of turns
    public void next(){
        turnSystem();
        battleStart();
    }

    //function will reset the game
    public void reset() {
        if(heroWin){
            mwinIndicator.setVisibility(View.VISIBLE);
            mwinIndicator.setText("Hero Victory!");
            heroWin = false;
        }else {
            mwinIndicator.setVisibility(View.VISIBLE);
            mwinIndicator.setText("YOU DIED");
        }
        heroHp = heroMaxHp;
        heroMp = heroMaxMp;
        mageHp = mageMaxHp;
        mageMp = mageMaxMp;
        heroDamage = 0;
        mageDamage = 0;
        turn = 1;
    }

    public void mageAtk() {
        Random randomizer = new Random();
        int mageRNG = randomizer.nextInt(70);
        if (mageRNG < mageATKChance) {
            mageDamage = randomizer.nextInt(mageAtkMax - mageAtkMin) + mageAtkMin;
            heroHp -= mageDamage;
            menuText.setText(monsName + " has dealt " + mageDamage + " to " + heroName);
        } else {
            menuText.setText(monsName + " is observing you.");
        }
        progressbar();
    }

    public void battleStart() {
        Random randomizer = new Random();
        random = randomizer.nextInt(100);
        if (turn == 1) {
            if (herofight) {
                //this will check the basic attack hit chance
                if (random < fightATKChance) {
                    int mpRegen = 5;
                    heroDamage = randomizer.nextInt(fightMaxDMG - fightMinDMG) + fightMinDMG;
                    mageHp -= heroDamage;
                    heroMp += mpRegen;
                    menuText.setText(heroName + " has dealt " +heroDamage + " damage to " +  monsName);
                } else {
                    menuText.setText(heroName + " tried to attack but missed");
                }
                //this will check if the hero's MP is full
                if (heroHp != heroMaxHp && heroMp < heroMaxMp) {
                    heroMp += fightATKManaCost;
                }

                herofight = false;
                hideButton();
                progressbar();
                turn++;
            }
            if (heroRest) {
                //checks if the healing will proc
                if (random < restHealChance) {
                    int mpRegen = 2;
                    healing = randomizer.nextInt(restHealMax - restHealMin) + restHealMin;
                    heroHp += healing;
                    heroMp += mpRegen;
                    menuText.setText(heroName + " has healed himself " + healing + " HP");
                    heroMp -= restHealManaCost;

                } else {
                    menuText.setText(heroName + " tried to rest but the Mage is preventing you to do so");
                }
                heroRest = false;
                hideButton();
                progressbar();
                turn++;


            }
            //victory statement
            if (mageHp <= 0) {
                heroWin = true;
                reset();
                progressbar();
            }
        }else {
        mageAtk();
        turn--;
            }
        if (heroHp <= 0) {
            reset();
            progressbar();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        //Button call
        fight = findViewById(R.id.fight);
        rest = findViewById(R.id.rest);

        //Progress bar call
        heroHpBar = findViewById(R.id.heroHpBar);
        heroHpBar.setMax(100);
        heroMpBar = findViewById(R.id.heroMpBar);
        heroMpBar.setMax(100);
        monsHpBar = findViewById(R.id.monsHpBar);
        monsHpBar.setMax(100);
        monsMpBar = findViewById(R.id.monsMpBar);
        monsMpBar.setMax(100);

        //TextView call
        mheroName = findViewById(R.id.heroName);
        mmonsName = findViewById(R.id.monsName);
        mwinIndicator = findViewById(R.id.winIndicator);
        menuText = findViewById(R.id.menuText);

        //Button Listener
        fight.setOnClickListener(this);
        rest.setOnClickListener(this);

        //layout
        menuBox = findViewById(R.id.menuBox);
        menuBox.setOnClickListener(this);
        menuBox.setClickable(true);

        //runs the turn system and battleStart
        turnSystem();
    }


    @Override
    public void onClick(View v) {
        //sets the victory dialogue to gone
        mwinIndicator.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.fight:
                herofight = true;
                battleStart();
                break;
            case R.id.rest:
                //this will check if the hero was enough mana points
                if (heroMp - restHealManaCost >= 0) {
                    heroRest = true;
                    battleStart();
                }else {
                    menuText.setText("Insufficient MP");
                    hideButton();
                    heroRest = false;
                }
                break;
            case R.id.menuBox:
                next();
                break;
        }
    }
}
