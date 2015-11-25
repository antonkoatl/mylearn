package com.noname.mylearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EditDict extends ActionBarActivity{
    String data[] = { "one", "two", "three", "four" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dict);

        ListView WordList = (ListView) findViewById(R.id.WordList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        WordList.setAdapter(adapter);
    }

    public void buttonClick(View v){
        switch (v.getId()) {
            case R.id.button_addWord:
                Intent intent = new Intent(this, AddWord.class);
                startActivity(intent);
        }
    }
}
