package com.noname.mylearn;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnAdapter extends FragmentStatePagerAdapter {
    public static final int MILLIS_IN_WEEK = (int) (604800000*0.95);
    public static final int MILLIS_IN_DAY = (int) (86400000*0.8);

    DBHelper dbHelper;
    long[] dict_ids;

    ArrayList<Word> wordsToLearn;

    // следующее слово
    Word last_word;

    // текущее слово
    Word last_word2;

    Word forcedWord;

    public LearnAdapter(FragmentManager fm, long[] dict_ids, DBHelper dbHelper) {
        super(fm);
        this.dbHelper = dbHelper;
        this.dict_ids = dict_ids;
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

        if (forcedWord != null) {
            last_word = forcedWord;
            forcedWord = null;
        } else {
            last_word2 = last_word;
            last_word = wordsToLearn.size() > 0 ? wordsToLearn.remove(0) : null;
        }
        return LearnFragment.newInstance(last_word);
    }

    // Функция составления списка слов на изучение
    void makeWordsList(){
        ArrayList<Word> words = new ArrayList<>();
        Time time = new Time();
        time.setToNow();

        long time_last_week = time.toMillis(false) - MILLIS_IN_WEEK;
        List<Word> words_week = loadWordsForLearn(3, 0, Word.S_LM_CNTRL, Word.S_LM_CNTRL, time_last_week);
        words.addAll(words_week);

        if (words.size() < 10) {
            long time_last_day = time.toMillis(false) - MILLIS_IN_DAY;
            List<Word> words_day = loadWordsForLearn(3, 0, Word.S_SM_TEST1, Word.S_LM_CNTRL, time_last_day);
            words.addAll(words_day);
        }

        if (words.size() < 10) {
            List<Word> words_now = loadWordsForLearn(3, 0, Word.S_TEST1, Word.S_CNTRL);
            words.addAll(words_now);
        }

        if (words.size() < 10) {
            List<Word> words_new = loadWordsForLearn(2, 0, Word.S_NEW, Word.S_NEW);
            words.addAll(words_new);
        }

        // Не добавлять последнее слово - изменится статус
        int ind = words.indexOf(last_word);
        if (ind != -1) words.remove(ind);

        Collections.shuffle(words);
        wordsToLearn = words;
    }

    public List<Word> loadWordsForLearn(int count, int offset, int stat_from, int stat_to){
        Time time = new Time();
        time.setToNow();
        return loadWordsForLearn(count, offset, stat_from, stat_to, time.toMillis(false), true);
    }

    public List<Word> loadWordsForLearn(int count, int offset, int stat_from, int stat_to, boolean asc){
        Time time = new Time();
        time.setToNow();
        return loadWordsForLearn(count, offset, stat_from, stat_to, time.toMillis(false), asc);
    }

    public List<Word> loadWordsForLearn(int count, int offset, int stat_from, int stat_to, long timestamp){
        return loadWordsForLearn(count, offset, stat_from, stat_to, timestamp, true);
    }

    public List<Word> loadWordsForLearn(int count, int offset, int stat_from, int stat_to, long timestamp, boolean asc) {
        List<Word> words = new ArrayList<>();
        for (long id: dict_ids) {
            words.addAll(dbHelper.loadWordsForLearn(id, count, offset, stat_from, stat_to, timestamp, asc));
        }
        if (words.size() > count) {
            Collections.shuffle(words);

            while (words.size() > count) words.remove(0);
        }
        return words;
    }

    @Override
    public int getItemPosition(Object object)
    {
        if(forcedWord != null && ((LearnFragment) object).word != null && ((LearnFragment) object).word.equals(last_word))
            return POSITION_NONE;
        if(((LearnFragment) object).type == LearnFragment.NOWORD)
            return POSITION_NONE;

        return POSITION_UNCHANGED;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = (Bundle) super.saveState();

        // Key must not start with 'f'
        state.putParcelableArrayList("la_wordsToLearn", wordsToLearn);
        state.putParcelable("la_last_word", last_word);
        state.putParcelable("la_last_word2", last_word2);
        state.putParcelable("la_forcedWord", forcedWord);

        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        Bundle bundle = (Bundle)state;

        wordsToLearn = bundle.getParcelableArrayList("la_wordsToLearn");
        last_word = bundle.getParcelable("la_last_word");
        last_word2 = bundle.getParcelable("la_last_word2");
        forcedWord = bundle.getParcelable("la_forcedWord");
    }



}
