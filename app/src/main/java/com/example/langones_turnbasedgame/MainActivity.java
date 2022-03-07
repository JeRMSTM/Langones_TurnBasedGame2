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
    int monsHp = 300;
    int monsMaxHp = 300;
    int monsMp = 100;
    int monsMaxMp = 100;
    int monsDamage;
    //MonsHp
    int monsHpPercent,monsMpPercent;

    /////Skill Info/////
    //Fight
    int basicAttackMin = 35;
    int basicAttackMax = 50;
    int basicAttackChance = 80;
    int basicAttackManaCost = 0;
    //Rest
    int basicHealMin = 20;
    int basicHealMax = 90;
    int basicHealChance = 50;
    int basicHealManaCost = 30;
    int healing;
    //random number
    int random;
    //monsFight
    int monsAtkMin = 40;
    int monsAtkMax = 100;
    int monsAtkChance = 55;


    //Button state
    boolean heroBasicAttack = false;
    boolean heroRest = false;

    //Speed System inspired by Epic Seven
    int heroSpd = 100;
    int heroCurrentSpd;
    int monsSpd = 70;
    int monsCurrentSpd;
    //the number required for a character to take a turn
    int speedCap = 540;

    boolean heroWin = false;

    //implementation of Speed System
    public void speed(){
        while(heroCurrentSpd <= speedCap && monsCurrentSpd <= speedCap){
            heroCurrentSpd += heroSpd;
            monsCurrentSpd += monsSpd;
        }
        if (heroCurrentSpd == monsCurrentSpd) {
            Random randomizer = new Random();
            int rng = randomizer.nextInt(99);
            if (rng >= 50) {
                heroCurrentSpd -=15;
            } else {
                monsCurrentSpd -=15;
            }
        }
    }

    //Progress Bars
    public void bar() {
        //formula used to get health and mana percentage
        heroHpPercent = heroHp * 100 / heroMaxHp;
        heroMpPercent = heroMp * 100 / heroMaxMp;
        monsHpPercent = monsHp * 100 / monsMaxHp;
        monsMpPercent = monsMp * 100 / monsMaxMp;
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
        if (heroCurrentSpd >= speedCap){
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
        monsHp = monsMaxHp;
        monsMp = monsMaxMp;
        heroDamage = 0;
        heroCurrentSpd = 0;
        monsDamage = 0;
        monsCurrentSpd = 0;
    }

    public void monsAtk() {
        Random randomizer = new Random();
        int monsRNG = randomizer.nextInt(70);
        if (monsRNG < monsAtkChance) {
            monsDamage = randomizer.nextInt(monsAtkMax - monsAtkMin) + monsAtkMin;
            heroHp -= monsDamage;
            menuText.setText(monsName + " has dealt " + monsDamage + " to " + heroName);
        } else {
            menuText.setText(monsName + " is observing you.");
        }
        monsCurrentSpd -= speedCap;
        bar();
    }

    public void battlePhase() {
        Random randomizer = new Random();
        random = randomizer.nextInt(100) + 1;
        if (heroCurrentSpd >= speedCap && heroCurrentSpd > monsCurrentSpd) {
            if (heroBasicAttack) {
                //this will check the basic attack hit chance
                if (random < basicAttackChance) {
                    int mpRegen = 10;
                    heroDamage = randomizer.nextInt(basicAttackMax - basicAttackMin) + basicAttackMin;
                    monsHp -= heroDamage;
                    heroMp += mpRegen;
                    menuText.setText(heroName + " has dealt " +heroDamage + " damage to " +  monsName);
                } else {
                    menuText.setText(heroName + " tried to attack but missed");
                }
                //this will check if the hero's MP is full
                if (heroHp != heroMaxHp && heroMp < heroMaxMp) {
                    heroMp += basicAttackManaCost;
                }

                heroCurrentSpd -= speedCap;
                heroBasicAttack = false;
                hideButton();
                bar();
            }
            if (heroRest) {
                //checks if the healing will proc
                if (random < basicHealChance) {
                    healing = randomizer.nextInt(basicHealMax - basicHealMin) + basicHealMin;
                    heroHp += healing;
                    menuText.setText(heroName + " has healed himself " + healing + " HP");
                    heroMp -= basicHealManaCost;
                    hideButton();
                    bar();
                    heroRest = false;
                }

            }
            //victory statement
            if (monsHp <= 0) {
                heroWin = true;
                reset();
                bar();
            }
        }else {
        monsAtk();
            }
        if (heroHp <= 0) {
            reset();
            bar();
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
                heroBasicAttack = true;
                battlePhase();
                break;
            case R.id.rest:
                //this will check if the hero was enough mana points
                if (heroMp - basicHealManaCost >= 0) {
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
