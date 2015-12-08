package com.noname.mylearn;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditDict extends ActionBarActivity{
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_ADDED = 200;
    public static final int RESULT_EDITED = 201;




    String data[] = { "one", "two", "three", "four" };
    ArrayList<String>wordString = new ArrayList<String>();
    List<Word>word = new ArrayList<Word>();
    ArrayAdapter<String> adapter;
    //SimpleCursorAdapter scAdapter;
    long idDict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dict);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();
        //Находим элемент, отображающий список слов
        ListView WordList = (ListView) findViewById(R.id.WordList);
        TextView TextViewWord = (TextView) findViewById(R.id.TextViewWord);

        /** формируем столбцы сопоставления
        String[] from = new String[]{ DBHelper.wColWord, DBHelper.wColTranslation };
        int[] to = new int[] { R.id.TextViewWord, R.id.TextViewTranslation };**/

        // извлекаем id текущего словаря
        idDict = getIntent().getLongExtra("idDict", 0);
        // загружаем 100 слов в ArrayList
        word = DBHelper.getInstance(getApplicationContext()).loadWords(idDict, 100,0);
        // копируем строки слов
        for(int i = 0; i < word.size(); i++){
            wordString.add(word.get(i).getWord());
        }
        // определяем адаптер
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.TextViewWord, wordString);
        WordList.setAdapter(adapter);
    }

    public void buttonClick(View v){
        switch (v.getId()) {
            case R.id.button_addWord:
                // Вызываем активити для нового слова
                Intent intent = new Intent(this, AddWord.class);
                intent.putExtra(AddWord.ACTION, AddWord.ADD_WORD);
                intent.putExtra("idDict", idDict);
                startActivityForResult(intent, EditDict.REQUEST_CODE);
        }
    }


    // Получаем результаты вызванных активити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            // Результат добавления слова
            if(resultCode == EditDict.RESULT_ADDED){
                Word word = data.getParcelableExtra("word");
                wordString.add(word.getWord());
                adapter.notifyDataSetChanged();
            }

            //Результат редактирования слова
            if(resultCode == EditDict.RESULT_EDITED){
                Word word = data.getParcelableExtra("word");
            }
        }

    }
}
