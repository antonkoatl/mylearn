package com.noname.mylearn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class DictDialog extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
    List<Dictionary> dicts;
    ArrayAdapter<String> adapter;
    List<String> data;
    ListView DictionaryList;
    long[] dict_ids;

    class MyAdapter extends ArrayAdapter<String>{
        private final Context context;
        public MyAdapter(Context context, List<String> data) {
            super(context, android.R.layout.simple_list_item_1, data);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_dialog);
        data = new ArrayList<>(); // Список пунктов диалога

        dicts = DBHelper.getInstance(this).loadDicts(100, 0); // Загрузка 100 словарей из бд
        for (Dictionary dict: dicts) {
            data.add(dict.getName());
        }

        Switch switch1 = (Switch) findViewById(R.id.switch1);

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String dictIdStr = sPref.getString(MainActivity.SAVED_ID_DICT, "");
        System.out.println("saved id " + dictIdStr);
        if (dictIdStr.length() > 0) {
            String[] dictIdsStr = dictIdStr.split(",");
            long[] dictIds = new long[dictIdsStr.length];
            for (int i = 0; i < dictIdsStr.length; i++) {
                dictIds[i] = Long.parseLong(dictIdsStr[i]);
            }
            if(dictIds.length > 1){
                switch1.setChecked(true);
                manySelectedDict();

            }
            else {
                switch1.setChecked(false);
                oneSelectedDict();
            }
        }
        else {
            switch1.setChecked(false);
            oneSelectedDict();
        }
        switch1.setOnCheckedChangeListener(this);
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Button button_select_dict = (Button) findViewById(R.id.button_select_dict);

        if(isChecked) {
            manySelectedDict();
            button_select_dict.setVisibility(View.VISIBLE);
        }
        // прячем кнопку при многословарном режиме
        else {
            oneSelectedDict();
            button_select_dict.setVisibility(View.INVISIBLE);
        }
    }
    public void selectOnClick(View v){
        switch (v.getId()) {
            case R.id.button_select_dict:
                // получаем Intent, который вызывал это Activity
                Intent intent = getIntent();
                // сохраняем ids выбранных словарей
                intent.putExtra(MainActivity.DICT_ID, dict_ids);
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
    public void oneSelectedDict(){
        DictionaryList = (ListView) findViewById(R.id.DictionaryList);
        // Адаптер для пунктов диалога
        adapter = new MyAdapter(this , data);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        DictionaryList.setAdapter(adapter);

        DictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                // сохраняем id выбранного словаря
                intent.putExtra(MainActivity.DICT_ID, new long[]{dicts.get(position).getId()});

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    public void manySelectedDict(){
        DictionaryList = (ListView) findViewById(R.id.DictionaryList);
        DictionaryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Адаптер для пунктов диалога
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, data);
        DictionaryList.setAdapter(adapter);

        DictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int checkCount = DictionaryList.getCheckedItemCount();
                dict_ids = new long[checkCount];
                SparseBooleanArray checkedItem = DictionaryList.getCheckedItemPositions();
                int index = 0;
                for(int i = 0; i < checkedItem.size(); i++){
                    boolean chk = checkedItem.valueAt(i);
                    if(chk) {
                        dict_ids[index] = dicts.get(DictionaryList.getCheckedItemPositions().keyAt(i)).getId();
                        index++;
                    }
                }
            }
        });
    }
}
