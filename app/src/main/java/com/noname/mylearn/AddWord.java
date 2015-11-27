package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class AddWord extends ActionBarActivity {
    // Действие для которого была вызвана активити
    public static final String ACTION = "action";
    public static final int ADD_WORD = 1;
    public static final int EDIT_WORD = 2;
    int action = 0;

    // Слово для редактирования
    public static final String WORD = "word";
    Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // получаем действие
        action = intent.getIntExtra(ACTION, 0);

        // получаем слово для редактирования
        if (action == ADD_WORD) {
            word = new Word();
        }
        if (action == EDIT_WORD) {
            word = intent.getParcelableExtra(WORD);
        }

    }

    public void SaveOnClick(View v){
        Intent intent_result = new Intent();

        if (action == ADD_WORD) {
            setResult(EditDict.RESULT_ADDED, intent_result);
        }
        if (action == EDIT_WORD) {
            setResult(EditDict.RESULT_EDITED, intent_result);
        }

        intent_result.putExtra("word", word); // Возвращаем созданное/отредактированное слово
        finish();
    }
}
