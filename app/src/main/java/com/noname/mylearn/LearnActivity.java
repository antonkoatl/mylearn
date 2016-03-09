package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends ActionBarActivity implements LearnFragment.LearnFragmentListener {
    LockableViewPager mPager;
    LearnAdapter mAdapter;
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // извлекаем id текущего словаря
        long[] dict_ids = intent.getLongArrayExtra(MainActivity.DICT_IDS);

        mAdapter = new LearnAdapter(getSupportFragmentManager(), dict_ids, DBHelper.getInstance(this));

        mPager = (LockableViewPager) findViewById(R.id.pager);
        mPager.setSwipeLocked(true);
        mPager.setAdapter(mAdapter);
    }


    @Override
    public void nextPage(boolean delayed) {
        if (mAdapter.last_word == null) {
            mAdapter.notifyDataSetChanged();
        }
        if (delayed) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            }, 1200);
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }

    }

    @Override
    public List<Word> getCurrentWords() {
        return mAdapter.wordsToLearn;
    }

    @Override
    public void setDebugInfo() {
        TextView textView = (TextView) findViewById(R.id.learn_debug_info);
        if(textView != null)textView.setText((mAdapter != null && mAdapter.last_word2 != null) ? String.valueOf(mAdapter.last_word2.getStat()) : "");
    }

    @Override
    public void forceWord(Word word) {
        mAdapter.forcedWord = word;
        mAdapter.notifyDataSetChanged();
        mAdapter.wordsToLearn = null;
    }

    @Override
    public List<Word> loadWordsForLearn(int offset, int count, int state_from, int state_to, boolean asc) {
        return mAdapter.loadWordsForLearn(offset, count, state_from, state_to, asc);
    }

}
