package com.noname.mylearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = getPreferences(MODE_PRIVATE);
        Long idDict = sPref.getLong(SAVED_ID_DICT, -1);
        if(idDict != -1){
            selectedDict( DBHelper.getInstance(this).getDictById(idDict) );
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

        TextView textLabel_wordsCount = (TextView) findViewById(R.id.wordsCountTextView);
        textLabel_wordsCount.setText("Количество слов: " + dict.getWordsCount());
    }
}
