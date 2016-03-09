package com.noname.mylearn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DictActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
    List<Dictionary> dicts;
    ArrayAdapter<String> adapter;
    List<String> data;
    ListView dictionaryList;
    SwitchCompat uiSwitch;
    Button buttonSelectDicts;
    long[] dict_ids;

    class MyAdapter extends ArrayAdapter<String>{
        private final Context context;
        public MyAdapter(Context context, List<String> data, int resource) {
            super(context, resource, data);
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
        setContentView(R.layout.activity_dict);

        // Находим элементы интерфейса
        uiSwitch = (SwitchCompat) findViewById(R.id.switch1);
        dictionaryList = (ListView) findViewById(R.id.DictionaryList);
        buttonSelectDicts = (Button) findViewById(R.id.button_select_dict);

        dicts = DBHelper.getInstance(this).loadDicts(100, 0); // Загрузка 100 словарей из бд

        // Список словарей
        data = new ArrayList<>();
        for (Dictionary dict: dicts) {
            data.add(dict.getName());
        }

        dict_ids = getIntent().getLongArrayExtra(MainActivity.DICT_IDS);
        if(dict_ids.length > 1){
            manySelectedDict();
            selectDictsInListview();
        } else {
            oneSelectedDict();
        }

        uiSwitch.setOnCheckedChangeListener(this);
    }

    private void selectDictsInListview() {
        for (long id: dict_ids) {
            for (int i = 0; i < dicts.size(); i++) {
                if (dicts.get(i).getId() == id) {
                    dictionaryList.setItemChecked(i, true);
                }
            }
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            manySelectedDict();
        } else {
            oneSelectedDict();
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
                dict.setId(DBHelper.getInstance(DictActivity.this).insertDict(dict));

                dicts.add(dict);
                data.add(dict.getName());

                adapter.notifyDataSetChanged();
        }
    }

    public void oneSelectedDict(){
        dictionaryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        uiSwitch.setChecked(false);
        buttonSelectDicts.setVisibility(View.INVISIBLE); // прячем кнопку при многословарном режиме
        // Адаптер для пунктов диалога
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        adapter = new MyAdapter(this , data, android.R.layout.simple_list_item_1);
        dictionaryList.setAdapter(adapter);

        dictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        dictionaryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        uiSwitch.setChecked(true);
        buttonSelectDicts.setVisibility(View.VISIBLE);
        // Адаптер для пунктов диалога
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, data);
        adapter = new MyAdapter(this , data, android.R.layout.simple_list_item_multiple_choice);
        dictionaryList.setAdapter(adapter);

        dictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Long> ids = new ArrayList<Long>();
                SparseBooleanArray checkedItems = dictionaryList.getCheckedItemPositions();
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) ids.add(dicts.get(checkedItems.keyAt(i)).getId());
                }
                dict_ids = new long[ids.size()];
                for (int i = 0; i < ids.size(); i++) dict_ids[i] = ids.get(i);
            }
        });
    }
}
