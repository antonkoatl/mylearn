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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EditDict extends ActionBarActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_ADDED = 200;
    public static final int RESULT_EDITED = 201;



    ArrayList<String>words = new ArrayList<String>();

    //ArrayAdapter<String> adapter;
    SimpleCursorAdapter scAdapter;
    long idDict;
    long idWord;



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
        idDict = intent.getLongExtra(MainActivity.DICT_ID, -1);

        // формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.wColWord, DBHelper.wColTranslation };
        int[] to = new int[] { R.id.TextViewWord, R.id.TextViewTranslation };

        // определяем адаптер
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);
        // указываем адаптеру свой биндер
        scAdapter.setViewBinder(new MyViewBinder());
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
                intent.putExtra(MainActivity.DICT_ID, idDict);
                startActivityForResult(intent, EditDict.REQUEST_CODE);
        }
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // вызываем активити для редактирования слова
            Intent intent = new Intent(EditDict.this, AddWord.class);
            intent.putExtra(AddWord.ACTION, AddWord.EDIT_WORD);
            // получаем id слова
            TextView c = (TextView) view.findViewById(R.id.TextViewWord);
            idWord = (long)c.getTag();

            intent.putExtra(MainActivity.DICT_ID, idDict);
            intent.putExtra(AddWord.WORD, idWord);
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

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if(view.getId() == R.id.TextViewWord || view.getId() == R.id.TextViewTranslation){
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                view.setTag(id);
                return false;
            }
            else
                return false;
        }
    }
}
