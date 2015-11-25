package com.noname.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
	public static final String dbName = "myDB";

	public static final String dictTable = "dict_table";

	//dictionaries
	public static final String dColId = "_id";
	public static final String dColName = "name";
	public static final String dWordsCount = "words_count";

    //words
    public static final String wColId = "_id";
    public static final String wColWord = "word";
    public static final String wColTranslation = "translation";

	public DBHelper(Context context) {
		super(context, "myDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTablesDb(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	private void createTablesDb(SQLiteDatabase db){
		db.execSQL("create table IF NOT EXISTS " + dictTable + " ("
                + dColId + " integer primary key autoincrement,"
                + dColName + " text,"
                + dWordsCount + " integer,"
                + ");");
	}

    private void createWordsTable(SQLiteDatabase db, String table_name){
        db.execSQL("create table IF NOT EXISTS " + table_name + " ("
                + wColId + " integer primary key autoincrement,"
                + wColWord + " text unique,"
                + wColTranslation + " text,"
                + ");");
    }

	public void insertDict(Dictionary dict){
		ContentValues cv = new ContentValues();
		cv.put(dColName, dict.getName());
		cv.put(dWordsCount, dict.getWordsCount());

		SQLiteDatabase db = getWritableDatabase();
		db.insert(dictTable, null, cv);
	}

    public void insertWord(Dictionary dict, Word word) {
        String table_name = "words_" + String.valueOf(dict.getId());
        SQLiteDatabase db = getWritableDatabase();
        createWordsTable(db, table_name);

        ContentValues cv = new ContentValues();
        cv.put(wColWord, word.getWord());
        cv.put(wColTranslation, word.getTranslationData());

        db.insert(table_name, null, cv);
    }

	public List<Dictionary> loadDicts(int count, int offset){
		List<Dictionary> result = new ArrayList<Dictionary>();
		
		SQLiteDatabase db = getReadableDatabase();
		
		String order_by = dColName + " DESC";
		
		Cursor cursor = db.query(dictTable, null, null, null, null, null, order_by);
		
		if(!cursor.moveToFirst()){return result;}
		
		boolean cursor_chk = true;
		for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();
		
		for (int i = 0; i < count; i++) {
			if(cursor_chk){
	        	Dictionary dict = new Dictionary();
                dict.setName( cursor.getString( cursor.getColumnIndex(dColName) ) );
                dict.setId( cursor.getInt(cursor.getColumnIndex(dColId)) );
                dict.setWordsCount( cursor.getInt(cursor.getColumnIndex(dColId)) );

	        	result.add(dict);
	        	cursor_chk = cursor.moveToNext();
	        } 
	    }
		
		cursor.close();
		
		return result;
	}

    public List<Word> loadWords(Dictionary dict, int count, int offset){
        List<Word> result = new ArrayList<Word>();

        SQLiteDatabase db = getReadableDatabase();

        String order_by = wColWord + " DESC";

        Cursor cursor = db.query(dictTable, null, null, null, null, null, order_by);

        if(!cursor.moveToFirst()){return result;}

        boolean cursor_chk = true;
        for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();

        for (int i = 0; i < count; i++) {
            if(cursor_chk){
                Word word = new Word();
                word.setWord(cursor.getString(cursor.getColumnIndex(wColWord)));
                word.setTranslationFromData(cursor.getString(cursor.getColumnIndex(wColTranslation)));

                result.add(word);
                cursor_chk = cursor.moveToNext();
            }
        }

        cursor.close();

        return result;
    }

}
