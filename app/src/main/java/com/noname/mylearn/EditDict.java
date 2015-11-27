package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EditDict extends ActionBarActivity{
    public static final int REQUEST_CODE = 100;
    public static final int RESULT_ADDED = 200;
    public static final int RESULT_EDITED = 201;




    String data[] = { "one", "two", "three", "four" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dict);

        // получаем Intent, который вызывал это Activity
        Intent intent = getIntent();

        ListView WordList = (ListView) findViewById(R.id.WordList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        WordList.setAdapter(adapter);
    }

    public void buttonClick(View v){
        switch (v.getId()) {
            case R.id.button_addWord:
                // Вызываем активити для нового слова
                Intent intent = new Intent(this, AddWord.class);
                intent.putExtra(AddWord.ACTION, AddWord.ADD_WORD);
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
            }

            //Результат редактирования слова
            if(resultCode == EditDict.RESULT_EDITED){
                Word word = data.getParcelableExtra("word");
            }
        }

    }
}
