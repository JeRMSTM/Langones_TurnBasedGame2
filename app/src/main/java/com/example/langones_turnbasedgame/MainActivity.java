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

    //hp and mp bars
    ProgressBar heroHpBar,heroMpBar,monsHpBar,monsMpBar;
    //buttons
    Button fight,rest;
    //text
    TextView mheroName,mmonsName,menuText,mwinIndicator;
    //layout
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
    int fightMaxDMG = 50;
    int fightATKChance = 80;
    int fightATKManaCost = 0;
    //Rest
    int restHealMin = 20;
    int restHealMax = 50;
    int restHealChance = 50;
    int restHealManaCost = 40;
    int healing;
    //random number
    int random;
    //Mage Attack
    int mageAtkMin = 40;
    int mageAtkMax = 100;
    int mageATKChance = 55;


    //Button state
    boolean herofight = false;
    boolean heroRest = false;

    //Speed System inspired by Epic Seven
    int heroSPD = 100;
    int heroCurrentSPD;
    int monsSPD = 70;
    int monsCurrentSPD;
    //the number required for a character to take a turn
    int speedCap = 540;

    boolean heroWin = false;

    //implementation of Speed System
    public void speed(){
        while(heroCurrentSPD <= speedCap && monsCurrentSPD <= speedCap){
            heroCurrentSPD += heroSPD;
            monsCurrentSPD += monsSPD;
        }
        if (heroCurrentSPD == monsCurrentSPD) {
            Random randomizer = new Random();
            int rng = randomizer.nextInt(99);
            if (rng >= 50) {
                heroCurrentSPD -=15;
            } else {
                monsCurrentSPD -=15;
            }
        }
    }

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
    public void turnCheck(){
        if (heroCurrentSPD >= speedCap){
            showButton();
        }else {
            hideButton();
        }
    }

    //moving of turns
    public void next(){
        speed();
        turnCheck();
        battlePhase();
    }

    //function will reset the game
    public void reset() {
        if(heroWin){
            mwinIndicator.setVisibility(View.VISIBLE);
            mwinIndicator.setText("Hero Victory!");
            heroWin = false;
        }else {
            mwinIndicator.setVisibility(View.VISIBLE);
            mwinIndicator.setText("Don't give up!");
        }
        heroHp = heroMaxHp;
        heroMp = heroMaxMp;
        mageHp = mageMaxHp;
        mageMp = mageMaxMp;
        heroDamage = 0;
        heroCurrentSPD = 0;
        mageDamage = 0;
        monsCurrentSPD = 0;
    }

    public void monsAtk() {
        Random randomizer = new Random();
        int mageRNG = randomizer.nextInt(70);
        if (mageRNG < mageATKChance) {
            mageDamage = randomizer.nextInt(mageAtkMax - mageAtkMin) + mageAtkMin;
            heroHp -= mageDamage;
            menuText.setText(monsName + " has dealt " + mageDamage + " to " + heroName);
        } else {
            menuText.setText(monsName + " is observing you.");
        }
        monsCurrentSPD -= speedCap;
        progressbar();
    }

    public void battlePhase() {
        Random randomizer = new Random();
        random = randomizer.nextInt(100) + 1;
        if (heroCurrentSPD >= speedCap && heroCurrentSPD > monsCurrentSPD) {
            if (herofight) {
                //this will check the basic attack hit chance
                if (random < fightATKChance) {
                    int mpRegen = 10;
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

                heroCurrentSPD -= speedCap;
                herofight = false;
                hideButton();
                progressbar();
            }
            if (heroRest) {
                //checks if the healing will proc
                if (random < restHealChance) {
                    healing = randomizer.nextInt(restHealMax - restHealMin) + restHealMin;
                    heroHp += healing;
                    menuText.setText(heroName + " has healed himself " + healing + " HP");
                    heroMp -= restHealManaCost;
                    hideButton();
                    progressbar();
                    heroRest = false;
                }

            }
            //victory statement
            if (mageHp <= 0) {
                heroWin = true;
                reset();
                progressbar();
            }
        }else {
        monsAtk();
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

        //runs the speed, turnCheck, and battlePhase
        speed();
        turnCheck();
    }


    @Override
    public void onClick(View v) {
        //sets the victory dialogue to gone
        mwinIndicator.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.fight:
                herofight = true;
                battlePhase();
                break;
            case R.id.rest:
                //this will check if the hero was enough mana points
                if (heroMp - restHealManaCost >= 0) {
                    heroRest = true;
                    battlePhase();
                }else {
                    menuText.setText(heroName + "has failed to rest");
                }
                break;
            case R.id.menuBox:
                next();
                break;
        }
    }
}
