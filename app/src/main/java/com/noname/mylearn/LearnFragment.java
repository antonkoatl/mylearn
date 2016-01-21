package com.noname.mylearn;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnFragment extends Fragment implements View.OnClickListener {
    static final String ARGUMENT_WORD = "arg_word";
    static final String ARGUMENT_DICT_ID = "arg_dict_id";

    static final int NEW = 0;
    static final int TEST = 1;
    static final int TYPE = 2;
    static final int CONTROL = 3;
    static final int NOWORD = 4;
    int type;

    Word word;
    long dictId;
    DBHelper dbHelper;

    ArrayList<Word> test_variants;

    // Интерфейс для работы с фрагментами
    public interface LearnFragmentListener {
        void nextPage(boolean delayed);
        List<Word> getCurrentWords();
        void setDebugInfo();
        void forceWord(Word word);
    }

    private LearnFragmentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LearnFragmentListener) activity;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
            throw new ClassCastException(activity.toString()
                    + " must implement LearnFragmentListener");
        }
    }

    static LearnFragment newInstance(Word word, long dict_id) {
        LearnFragment pageFragment = new LearnFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_WORD, word);
        arguments.putLong(ARGUMENT_DICT_ID, dict_id);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        word = getArguments().getParcelable(ARGUMENT_WORD);
        dictId = getArguments().getLong(ARGUMENT_DICT_ID);

        if (word == null) {
            type = NOWORD;
        } else {
            switch (word.getStat()) {
                case 0:
                    type = NEW;
                    break;
                case 1:
                    type = TEST;
                    break;
                case 2:
                    type = TYPE;
                    break;
                case 3:
                    type = TEST;
                    break;
                case 4:
                    type = TYPE;
                    break;
                case 5:
                    type = CONTROL;
                    break;
                case 6:
                    type = TEST;
                    break;
                case 7:
                    type = TYPE;
                    break;
                case 8:
                    type = CONTROL;
                    break;
                case 9:
                    type = CONTROL;
                    break;
            }
        }

        dbHelper = DBHelper.getInstance(this.getActivity());

        if (savedInstanceState != null) {
            test_variants = savedInstanceState.getParcelableArrayList("test_variants");
        }

        mListener.setDebugInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        switch (type) {
            case NEW:
                view = inflater.inflate(R.layout.learn_fragment_new, null);
                Button button_next = (Button) view.findViewById(R.id.learn_button_next);
                button_next.setOnClickListener(this);
                break;
            case TEST:
                view = inflater.inflate(R.layout.learn_fragment_test, null);

                if (test_variants == null) {
                    List<Word> variants = new ArrayList<>();

                    for (Word w : mListener.getCurrentWords()) {
                        if (!w.equals(word)) variants.add(w);
                    }

                    for (Word w : dbHelper.loadWordsForLearn(dictId, 10, 0, 0, 8, false)) {
                        if (!w.equals(word)) variants.add(w);
                    }

                    Collections.shuffle(variants);

                    test_variants = new ArrayList<>();
                    while (test_variants.size() < 3) {
                        Word w = variants.remove(0);
                        if (w.getTranslationData().equals(word.getTranslationData())) continue;
                        if (!test_variants.contains(w)) {
                            boolean fl = true;
                            for (Word w2 : test_variants) {
                                if (w2.getTranslationData().equals(w.getTranslationData()))
                                    fl = false;
                            }
                            if (fl) test_variants.add(w);
                        }
                    }

                    test_variants.add(word);

                    Collections.shuffle(test_variants);
                }

                Button button1 = (Button) view.findViewById(R.id.learn_button_choice1);
                button1.setOnClickListener(this);
                button1.setText(test_variants.get(0).getTranslationData());
                button1.setTag(test_variants.get(0).getId());
                Button button2 = (Button) view.findViewById(R.id.learn_button_choice2);
                button2.setOnClickListener(this);
                button2.setText(test_variants.get(1).getTranslationData());
                button2.setTag(test_variants.get(1).getId());
                Button button3 = (Button) view.findViewById(R.id.learn_button_choice3);
                button3.setOnClickListener(this);
                button3.setText(test_variants.get(2).getTranslationData());
                button3.setTag(test_variants.get(2).getId());
                Button button4 = (Button) view.findViewById(R.id.learn_button_choice4);
                button4.setOnClickListener(this);
                button4.setText(test_variants.get(3).getTranslationData());
                button4.setTag(test_variants.get(3).getId());
                break;
            case TYPE:
                view = inflater.inflate(R.layout.learn_fragment_type, null);
                Button button_next2 = (Button) view.findViewById(R.id.learn_button_next);
                button_next2.setOnClickListener(this);
                break;
            case CONTROL:
                view = inflater.inflate(R.layout.learn_fragment_control, null);
                Button button5 = (Button) view.findViewById(R.id.learn_button_choice1);
                button5.setOnClickListener(this);
                Button button6 = (Button) view.findViewById(R.id.learn_button_choice2);
                button6.setOnClickListener(this);
                Button button7 = (Button) view.findViewById(R.id.learn_button_choice3);
                button7.setOnClickListener(this);
                Button button8 = (Button) view.findViewById(R.id.learn_button_choice4);
                button8.setOnClickListener(this);
                break;
            case NOWORD:
                view = inflater.inflate(R.layout.learn_fragment_filler, null);
                TextView tv_w = (TextView) view.findViewById(R.id.learn_filler_text);
                tv_w.setText("Нет слов для изучения");
                break;
        }


        TextView tv_w = (TextView) view.findViewById(R.id.learn_word);
        if (tv_w != null) tv_w.setText(word.getWord());

        TextView tv_t = (TextView) view.findViewById(R.id.learn_translation);
        if (tv_t != null) tv_t.setText(word.getTranslationData());



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learn_button_next:
                switch (type) {
                    case NEW:
                        word.updateStat(Word.ST_SUCCESS);
                        dbHelper.updateWordById(word, dictId);
                        mListener.nextPage(false);
                        break;
                    case TYPE:
                        EditText editText = (EditText) ((View)v.getParent()).findViewById(R.id.learn_edit1);
                        String entered_var = editText.getText().toString().toLowerCase();
                        if (entered_var.equals(word.getWord())) {
                            word.updateStat(Word.ST_SUCCESS);
                            v.setBackgroundColor(getResources().getColor(R.color.right));
                        } else {
                            if (dbHelper.chkWord(entered_var, word.getTranslationData(), dictId) != null){
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Введите другое слово же!", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            } else {
                                word.updateStat(Word.ST_FAIL);
                                mListener.forceWord(word);
                                v.setBackgroundColor(getResources().getColor(R.color.wrong));
                            }
                        }
                        dbHelper.updateWordById(word, dictId);
                        mListener.nextPage(true);
                        break;
                }
                break;
            case R.id.learn_button_choice1:
            case R.id.learn_button_choice2:
            case R.id.learn_button_choice3:
            case R.id.learn_button_choice4:
                switch (type) {
                    case TEST:
                        if (v.getTag() == word.getId()) {
                            word.updateStat(Word.ST_SUCCESS);
                            v.setBackgroundColor(getResources().getColor(R.color.right));
                        } else {
                            word.updateStat(Word.ST_FAIL);
                            v.setBackgroundColor(getResources().getColor(R.color.wrong));
                            mListener.forceWord(word);
                        }
                        dbHelper.updateWordById(word, dictId);
                        mListener.nextPage(true);
                        break;
                    case CONTROL:
                        switch (v.getId()) {
                            case R.id.learn_button_choice1:
                                word.updateStat(Word.ST_FAIL);
                                mListener.forceWord(word);
                                break;
                            case R.id.learn_button_choice2:
                                word.updateStat(Word.ST_BAD);
                                break;
                            case R.id.learn_button_choice3:
                                word.updateStat(Word.ST_GOOD);
                                break;
                            case R.id.learn_button_choice4:
                                word.updateStat(Word.ST_SUCCESS);
                                break;
                        }
                        dbHelper.updateWordById(word, dictId);
                        mListener.nextPage(false);
                        break;
                }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("test_variants", test_variants);
    }
}
