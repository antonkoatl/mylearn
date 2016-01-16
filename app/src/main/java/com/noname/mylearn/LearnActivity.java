package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class LearnActivity extends ActionBarActivity {
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // извлекаем id текущего словаря
        long idDict = intent.getLongExtra(MainActivity.DICT_ID, -1);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new LearnAdapter(getSupportFragmentManager(), idDict, DBHelper.getInstance(this)));
    }
}
