package com.noname.mylearn;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnAdapter extends FragmentPagerAdapter {
    DBHelper dbHelper;
    long dictId;
    List<Word> wordsToLearn;

    public LearnAdapter(FragmentManager fm, long dictId, DBHelper dbHelper) {
        super(fm);
        this.dbHelper = dbHelper;
        this.dictId = dictId;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Fragment getItem(int position) {
        if (wordsToLearn == null || wordsToLearn.size() == 0) {
            makeWordsList();
        }

        Word current_word = wordsToLearn.remove(0);


        return LearnFragment.newInstance(current_word);
    }

    void makeWordsList(){
        List<Word> words = new ArrayList<>();
        Time time = new Time();
        time.setToNow();
        long time_last_week = time.toMillis(false) - 604800000;
        List<Word> words_week = dbHelper.loadWordsForLearn(dictId, 10, 0, 9, 9, time_last_week);
        words.addAll(words_week);

        if (words.size() < 10) {
            long time_last_day = time.toMillis(false) - 86400000;
            List<Word> words_day = dbHelper.loadWordsForLearn(dictId, 10, 0, 6, 9, time_last_day);
            words.addAll(words_day);
        }

        if (words.size() < 10) {
            List<Word> words_now = dbHelper.loadWordsForLearn(dictId, 10, 0, 1, 5);
            words.addAll(words_now);
        }

        if (words.size() < 10) {
            List<Word> words_new = dbHelper.loadWordsForLearn(dictId, 10 - words.size(), 0, 0, 0);
            words.addAll(words_new);
        }

        Collections.shuffle(words);
        wordsToLearn = words;
    }
}
