package com.noname.mylearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    public static final int REQUEST_CODE_SELECTED = 300;
    public static final String DICT_ID = "dict_id";
    public static final String DICT_IDS = "dict_id";

    long[] dictIds = new long[0];

    SharedPreferences sPref;
    public static final String SAVED_ID_DICT = "dict_ids";

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.getInstance(this);

        sPref = PreferenceManager.getDefaultSharedPreferences(this);

        String dictIdStr = sPref.getString(SAVED_ID_DICT, "");
        if (dictIdStr.length() > 0) {
            String[] dictIdsStr = dictIdStr.split(",");
            dictIds = new long[dictIdsStr.length];
            for (int i = 0; i < dictIdsStr.length; i++) {
                dictIds[i] = Long.parseLong(dictIdsStr[i]);
            }
            updateDictInfo();
        }
    }

    /**@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("wordsCount", currentDicts.getWordsCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentDicts.setWordsCount(savedInstanceState.getInt("wordsCount"));
    }**/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_dict:
                Intent intent = new Intent(this, DictActivity.class);
                intent.putExtra(DICT_IDS, dictIds);
                startActivityForResult(intent, REQUEST_CODE_SELECTED);
                break;
            case R.id.main_button_editDict:
                if(dictIds.length > 0) {
                    intent = new Intent(this, EditDictActivity.class);
                    intent.putExtra(DICT_ID, dictIds[0]);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Выберите словарь же!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.main_button_learn:
                if(dictIds.length > 0) {
                    intent = new Intent(this, LearnActivity.class);
                    intent.putExtra(DICT_IDS, dictIds);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Выберите словарь же!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            default:
                break;
        }
    }

    // Получаем результаты вызванных активити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode) {
                case REQUEST_CODE_SELECTED:
                    long[] idDict = data.getLongArrayExtra(DICT_ID);
                    selectedDicts(idDict);
                    break;
            }
        }
    }

    public void selectedDicts(long[] dicts) {
        dictIds = dicts;

        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sPref.edit();

        String dict_ids = "";
        for (long id: dicts) {
            if (dict_ids.length() > 0) {
                dict_ids += "," + id;
            } else {
                dict_ids += id;
            }
        }
        editor.putString(SAVED_ID_DICT, dict_ids);
        editor.apply();

        updateDictInfo();
    }

    // Обвновляет информацию о словаре из бд и отображает на форме
    void updateDictInfo(){
        if(dictIds == null) return;

        Dictionary[] currentDicts = dbHelper.getDictsById(dictIds);

        // строка с перечислением текущих словарей
        String currentDictString = "Текущие словари: ";

        // общее количество слов
        int wordsCount = 0, wordsLearned = 0, wordsLToday = 0, wordsLTomorrow = 0, wordsLNew = 0;
        for(Dictionary dict: currentDicts) {
            currentDictString += dict.getName() + ", ";
            wordsCount += dict.getWordsCount();
            wordsLearned += dbHelper.countWords(dict.getId(), Word.S_LM_LEARNED, 100);
            wordsLToday += dbHelper.countWordsLToday(dict.getId());
            wordsLTomorrow += dbHelper.countWordsLTomorrow(dict.getId());
            wordsLNew += dbHelper.countWordsNew(dict.getId());
        }

        currentDictString = currentDictString.substring(0, currentDictString.length() - 2);
        TextView textLabel_currentDict = (TextView) findViewById(R.id.main_text_dicts);
        textLabel_currentDict.setText(currentDictString);

        TextView textLabel_wordsCount = (TextView) findViewById(R.id.main_text_count);
        textLabel_wordsCount.setText("Выучено слов: " + wordsLearned + "/" + wordsCount);

        TextView textLabel_wordsCount2 = (TextView) findViewById(R.id.main_words_today);
        textLabel_wordsCount2.setText("Учить сегодня: " + wordsLToday);

        TextView textLabel_wordsCount3 = (TextView) findViewById(R.id.main_words_tomorrow);
        textLabel_wordsCount3.setText("Учить завтра: " + wordsLTomorrow);

        TextView textLabel_wordsCount4 = (TextView) findViewById(R.id.main_words_new);
        textLabel_wordsCount4.setText("Новых слов: " + wordsLNew);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDictInfo();
    }
}
