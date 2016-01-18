package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.List;

public class LearnActivity extends ActionBarActivity implements LearnFragment.LearnFragmentListener {
    LockableViewPager mPager;
    LearnAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // извлекаем id текущего словаря
        long idDict = intent.getLongExtra(MainActivity.DICT_ID, -1);

        mAdapter = new LearnAdapter(getSupportFragmentManager(), idDict, DBHelper.getInstance(this));

        mPager = (LockableViewPager) findViewById(R.id.pager);
        mPager.setSwipeLocked(true);
        mPager.setAdapter(mAdapter);
    }


    @Override
    public void nextPage() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

    @Override
    public List<Word> getCurrentWords() {
        return mAdapter.wordsToLearn;
    }
}
