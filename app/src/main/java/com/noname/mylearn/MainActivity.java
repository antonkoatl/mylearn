package com.noname.mylearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements DictDialog.NoticeDialogListener {
    public static final String DICT_ID = "dict_id";

    DialogFragment dlg1;
    Dictionary currentDict;

    //public static final String APP_PREFERENCES = "settings";
    SharedPreferences sPref;
    final String SAVED_ID_DICT = "saved_id";

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.getInstance(this);

        sPref = getPreferences(MODE_PRIVATE);
        Long idDict = sPref.getLong(SAVED_ID_DICT, -1);
        if(idDict != -1){
            selectedDict( dbHelper.getDictById(idDict) );
        }

        dlg1 = new DictDialog();


    }

    /**@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("wordsCount", currentDict.getWordsCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentDict.setWordsCount(savedInstanceState.getInt("wordsCount"));
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
                dlg1.show(getSupportFragmentManager(), "dlg1");
                break;
            case R.id.button_editDict:
                if(currentDict.getId() > 0) {
                    Intent intent = new Intent(this, EditDict.class);
                    intent.putExtra(DICT_ID, currentDict.getId());
                    startActivity(intent);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Выберите словарь же!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.button3:
                if(currentDict.getId() > 0) {
                    Intent intent = new Intent(this, LearnActivity.class);
                    intent.putExtra(DICT_ID, currentDict.getId());
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

    @Override
    public void selectedDict(Dictionary dict) {
        currentDict = dict;

        sPref = getPreferences(MODE_PRIVATE);
        Editor editor = sPref.edit();
        editor.putLong(SAVED_ID_DICT, currentDict.getId());
        editor.apply();

        updateDictInfo();

    }

    void updateDictInfo(){
        currentDict = dbHelper.getDictById(currentDict.getId());

        Time time = new Time();
        time.setToNow();
        long time_last_day = time.toMillis(false) - LearnAdapter.MILLIS_IN_DAY;
        long time_last_week = time.toMillis(false) - LearnAdapter.MILLIS_IN_WEEK;

        TextView textLabel_wordsCount = (TextView) findViewById(R.id.wordsCountTextView);
        textLabel_wordsCount.setText("Количество слов: " + dbHelper.countWords(currentDict.getId(), 10, 100) + "/" + currentDict.getWordsCount());

        TextView textLabel_wordsCount2 = (TextView) findViewById(R.id.main_words_today);
        int today = dbHelper.countWords(currentDict.getId(), 1, 5);
        today += dbHelper.countWords(currentDict.getId(), 6, 8, time_last_day);
        today += dbHelper.countWords(currentDict.getId(), 9, 9, time_last_week);
        textLabel_wordsCount2.setText("Учить сегодня: " + String.valueOf(today));

        TextView textLabel_wordsCount3 = (TextView) findViewById(R.id.main_words_tomorrow);
        int tomorrow = dbHelper.countWords(currentDict.getId(), 6, 8);
        tomorrow += dbHelper.countWords(currentDict.getId(), 9, 9, (long) (time_last_week*0.857));
        textLabel_wordsCount3.setText("Учить завтра: " + String.valueOf(tomorrow));

        TextView textLabel_wordsCount4 = (TextView) findViewById(R.id.main_words_new);
        int neww = dbHelper.countWords(currentDict.getId(), 0, 0);
        textLabel_wordsCount4.setText("Новых слов: " + String.valueOf(neww));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDictInfo();
    }
}
