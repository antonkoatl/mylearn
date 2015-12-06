package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

public class AddWord extends ActionBarActivity {
    // Действие для которого была вызвана активити
    public static final String ACTION = "action";
    public static final int ADD_WORD = 1;
    public static final int EDIT_WORD = 2;
    int action = 0;

    // Слово для редактирования
    public static final String WORD = "word";
    Word word;
    EditText EditWord;
    EditText EditTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // получаем действие
        action = intent.getIntExtra(ACTION, 0);

        //находим поля ввода
        EditWord = (EditText) findViewById(R.id.editText);
        EditTranslation = (EditText) findViewById(R.id.editText2);

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
            word.setWord(EditWord.getText().toString());
            word.setTranslationFromData(EditTranslation.getText().toString());
        }
        if (action == EDIT_WORD) {
            setResult(EditDict.RESULT_EDITED, intent_result);
        }

        intent_result.putExtra("word", word); // Возвращаем созданное/отредактированное слово
        finish();
    }
}
