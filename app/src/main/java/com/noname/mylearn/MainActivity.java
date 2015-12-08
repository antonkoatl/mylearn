package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements DictDialog.NoticeDialogListener {
    DialogFragment dlg1;
    Dictionary currentDict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent intent = new Intent(this, EditDict.class);
                intent.putExtra(EditDict.DICT, currentDict.getId());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void selectedDict(Dictionary dict) {
        currentDict = dict;
        TextView textLabel_wordsCount = (TextView) findViewById(R.id.wordsCountTextView);
        textLabel_wordsCount.setText("Количество слов: " + dict.getWordsCount());
    }
}
