package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DictDialog extends ActionBarActivity {
    List<Dictionary> dicts;
    ArrayAdapter<String> adapter;
    List<String> data;
    ListView DictionaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_dialog);
        data = new ArrayList<>(); // Список пунктов диалога

        dicts = DBHelper.getInstance(this).loadDicts(100, 0); // Загрузка 100 словарей из бд
        for (Dictionary dict: dicts) {
            data.add(dict.getName());
        }

        DictionaryList = (ListView) findViewById(R.id.DictionaryList);
        DictionaryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Адаптер для пунктов диалога
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, data);
        DictionaryList.setAdapter(adapter);
        DictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    public void selectOnClick(View v){
        switch (v.getId()) {
            case R.id.button_select_dict:
                // получаем Intent, который вызывал это Activity
                Intent intent = getIntent();
                // сохраняем id выбранного словаря
                intent.putExtra(MainActivity.DICT_ID, dicts.get(DictionaryList.getCheckedItemPosition()).getId());

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.button_add_dictionary:
                Dictionary dict = new Dictionary();
                dict.setName(String.valueOf(data.size()));
                dict.setId(DBHelper.getInstance(DictDialog.this).insertDict(dict));

                dicts.add(dict);
                data.add(dict.getName());

                adapter.notifyDataSetChanged();
        }
    }
}
