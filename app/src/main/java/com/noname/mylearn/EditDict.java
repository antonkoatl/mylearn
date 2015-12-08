package com.noname.mylearn;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class EditDict extends ActionBarActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_ADDED = 200;
    public static final int RESULT_EDITED = 201;

    public static final String DICT = "dict";

    ArrayList<String>words = new ArrayList<String>();

    //ArrayAdapter<String> adapter;
    SimpleCursorAdapter scAdapter;
    long idDict;



    static class MyCursorLoader extends CursorLoader {
        DBHelper db;
        long idDict;

        public MyCursorLoader(Context context, DBHelper db, long idDict) {
            super(context);
            this.db = db;
            this.idDict = idDict;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getReadableDatabase().query( db.getWordsTableName(idDict), getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dict);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();
        // Находим элемент, отображающий список слов
        ListView WordList = (ListView) findViewById(R.id.WordList);

        // извлекаем id текущего словаря
        idDict = intent.getLongExtra(DICT, -1);

        // загружаем 100 слов в ArrayList
        // word = DBHelper.getInstance(getApplicationContext()).loadWords(idDict, 100,0);
        // копируем строки слов
        /*for(int i = 0; i < word.size(); i++){
            wordString.add(word.get(i).getWord());
        }*/

        // формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.wColWord, DBHelper.wColTranslation };
        int[] to = new int[] { R.id.TextViewWord, R.id.TextViewTranslation };

        // определяем адаптер
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);
        //adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.TextViewWord, wordString);
        WordList.setAdapter(scAdapter);
        WordList.setOnItemClickListener(onItemClickListener);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void buttonClick(View v){
        switch (v.getId()) {
            case R.id.button_addWord:
                // Вызываем активити для нового слова
                Intent intent = new Intent(this, AddWord.class);
                intent.putExtra(AddWord.ACTION, AddWord.ADD_WORD);
                intent.putExtra(DICT, idDict);
                startActivityForResult(intent, EditDict.REQUEST_CODE);
        }
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Вызываем активити для редактирования слова
            Intent intent = new Intent(EditDict.this, AddWord.class);
            intent.putExtra(AddWord.ACTION, AddWord.EDIT_WORD);
            // TODO: получить id слова
            //intent.putExtra(AddWord.WORD, words.get(position).getId());
            intent.putExtra(DICT, idDict);
            startActivityForResult(intent, EditDict.REQUEST_CODE);
        }
    };

    // Получаем результаты вызванных активити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            // Обновить список после добавления/редактирования слова
            if(resultCode == EditDict.RESULT_ADDED || resultCode == EditDict.RESULT_EDITED){
                getSupportLoaderManager().getLoader(0).forceLoad();
                //scAdapter.notifyDataSetChanged();
            }
        }

    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, DBHelper.getInstance(getApplicationContext()), idDict);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
