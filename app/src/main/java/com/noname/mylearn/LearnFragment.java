package com.noname.mylearn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LearnFragment extends Fragment {
    static final String ARGUMENT_WORD = "arg_word";

    static final int NEW = 0;
    static final int TEST = 1;
    static final int TYPE = 2;
    static final int CONTROL = 3;

    Word word;
    int type;

    static LearnFragment newInstance(Word word) {
        LearnFragment pageFragment = new LearnFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_WORD, word);



        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        word = getArguments().getParcelable(ARGUMENT_WORD);

        switch (word.getStat()){
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        switch (type) {
            case NEW:
                view = inflater.inflate(R.layout.learn_fragment_new, null);
                break;
            case TEST:
                view = inflater.inflate(R.layout.learn_fragment_test, null);
                break;
            case TYPE:
                view = inflater.inflate(R.layout.learn_fragment_type, null);
                break;
            case CONTROL:
                view = inflater.inflate(R.layout.learn_fragment_control, null);
                break;
        }


        TextView tv_w = (TextView) view.findViewById(R.id.learn_word);
        if (tv_w != null) tv_w.setText(word.getWord());

        TextView tv_t = (TextView) view.findViewById(R.id.learn_translation);
        if (tv_t != null) tv_t.setText(word.getTranslationData());

        return view;
    }
}
