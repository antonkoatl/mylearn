package com.noname.mylearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    public static final int REQUEST_CODE_SELECTED = 300;
    public static final String DICT_ID = "dict_id";

    Dictionary[] currentDicts;

    //public static final String APP_PREFERENCES = "settings";
    SharedPreferences sPref;
    final String SAVED_ID_DICT = "dict_ids";

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.getInstance(this);

        sPref = getPreferences(MODE_PRIVATE);
        String dictIdStr = sPref.getString(SAVED_ID_DICT, "");
        if (dictIdStr.length() > 0) {
            String[] dictIdsStr = dictIdStr.split(",");
            long[] dictIds = new long[dictIdsStr.length];
            for (int i = 0; i < dictIdsStr.length; i++) {
                dictIds[i] = Long.parseLong(dictIdsStr[i]);
            }
            if (dictIds.length > 0) {
                selectedDicts(dbHelper.getDictsById(dictIds));
            }
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
            case R.id.button_dict:
                Intent intent = new Intent(this, DictDialog.class);
                startActivityForResult(intent, REQUEST_CODE_SELECTED);
                break;
            case R.id.button_editDict:
                if(currentDicts != null) {
                    intent = new Intent(this, EditDict.class);
                    intent.putExtra(DICT_ID, currentDicts[0].getId());
                    startActivity(intent);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Выберите словарь же!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.button3:
                if(currentDicts != null) {
                    intent = new Intent(this, LearnActivity.class);
                    intent.putExtra(DICT_ID, currentDicts[0].getId());
                    startActivity(intent);
                }
                else {
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
                    selectedDicts(dbHelper.getDictsById(idDict));
                    break;
            }
        }
    }
    public void selectedDicts(Dictionary[] dicts) {
        currentDicts = dicts;

        sPref = getPreferences(MODE_PRIVATE);
        Editor editor = sPref.edit();

        String dict_ids = "";
        for (Dictionary dict: dicts) {
            if (dict_ids.length() > 0) {
                dict_ids += "," + String.valueOf(dict.getId());
            } else {
                dict_ids += String.valueOf(dict.getId());
            }
        }
        editor.putString(SAVED_ID_DICT, dict_ids);
        editor.apply();

        updateDictInfo();
    }

    // Обвновляет информацию о словаре из бд и отображает на форме
    void updateDictInfo(){
        if(currentDicts == null) return;
        currentDicts[0] = dbHelper.getDictById(currentDicts[0].getId());

        TextView textLabel_currentDict = (TextView) findViewById(R.id.currentDictTextView);
        textLabel_currentDict.setText("Текущий словарь: " + currentDicts[0].getName());

        Time time = new Time();
        time.setToNow();
        long time_last_day = time.toMillis(false) - LearnAdapter.MILLIS_IN_DAY;
        long time_last_week = time.toMillis(false) - LearnAdapter.MILLIS_IN_WEEK;

        TextView textLabel_wordsCount = (TextView) findViewById(R.id.wordsCountTextView);
        textLabel_wordsCount.setText("Количество слов: " + dbHelper.countWords(currentDicts[0].getId(), 10, 100) + "/" + currentDicts[0].getWordsCount());

        TextView textLabel_wordsCount2 = (TextView) findViewById(R.id.main_words_today);
        int today = dbHelper.countWords(currentDicts[0].getId(), 1, 5);
        today += dbHelper.countWords(currentDicts[0].getId(), 6, 8, time_last_day);
        today += dbHelper.countWords(currentDicts[0].getId(), 9, 9, time_last_week);
        textLabel_wordsCount2.setText("Учить сегодня: " + String.valueOf(today));

        TextView textLabel_wordsCount3 = (TextView) findViewById(R.id.main_words_tomorrow);
        int tomorrow = dbHelper.countWords(currentDicts[0].getId(), 6, 8);
        tomorrow += dbHelper.countWords(currentDicts[0].getId(), 9, 9, (long) (time_last_week*0.857));
        textLabel_wordsCount3.setText("Учить завтра: " + String.valueOf(tomorrow));

        TextView textLabel_wordsCount4 = (TextView) findViewById(R.id.main_words_new);
        int neww = dbHelper.countWords(currentDicts[0].getId(), 0, 0);
        textLabel_wordsCount4.setText("Новых слов: " + String.valueOf(neww));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDictInfo();
    }
}
