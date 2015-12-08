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


    // БД
    DBHelper dbHelper;

    // Данные о слове
    Word word;
    long word_id;
    long dict_id;

    // Объекты для работы с интерфейсом
    EditText EditWord;
    EditText EditTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        // получаем данные
        action = intent.getIntExtra(ACTION, 0);
        word_id = intent.getLongExtra(WORD, -1);
        dict_id = intent.getLongExtra(EditDict.DICT, -1);

        // находим поля ввода
        EditWord = (EditText) findViewById(R.id.editText);
        EditTranslation = (EditText) findViewById(R.id.editText2);

        // получаем объект для работы с бд
        dbHelper = DBHelper.getInstance(this);

        // получаем слово для редактирования
        if (action == ADD_WORD) {
            word = new Word();
        }
        if (action == EDIT_WORD) {
            word = dbHelper.getWordById(word_id, dict_id);
            EditWord.setText(word.getWord());
            EditTranslation.setText(word.getTranslationData());
        }
    }

    public void saveOnClick(View v){
        if (action == ADD_WORD) {
            setResult(EditDict.RESULT_ADDED, null);
            word.setWord(EditWord.getText().toString());
            word.setTranslationFromData(EditTranslation.getText().toString());
            dbHelper.insertWord(dict_id, word);
        }
        if (action == EDIT_WORD) {
            setResult(EditDict.RESULT_EDITED, null);
            word.setWord(EditWord.getText().toString());
            word.setTranslationFromData(EditTranslation.getText().toString());
            dbHelper.updateWordById(word, dict_id);
        }
        finish();
    }
}
