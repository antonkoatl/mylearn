package com.noname.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;
import android.view.ViewDebug;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
	public static final String dbName = "myDB";
    public static final int dbVersion = 1;

	public static final String dictTable = "dict_table";

	//dictionaries table
	public static final String dColId = "_id";
	public static final String dColName = "name";
	public static final String dWordsCount = "words_count";

    //words table
    public static final String wColId = "_id";
    public static final String wColWord = "word";
    public static final String wColTranslation = "translation";
    public static final String wColStatus = "status";
    public static final String wColLastTimestamp = "last_timestamp";

    // Используем этот класс как синглтон
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) instance = new DBHelper(context.getApplicationContext());
        return instance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DBHelper(Context context) {
		super(context, dbName, null, dbVersion);
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
                + dWordsCount + " integer"
                + ");");
	}

    private void createWordsTable(SQLiteDatabase db, long dict_id){
        String table_name = getWordsTableName(dict_id);
        db.execSQL("create table IF NOT EXISTS " + table_name + " ("
                + wColId + " integer primary key autoincrement,"
                + wColWord + " text unique,"
                + wColTranslation + " text,"
                + wColStatus + " integer,"
                + wColLastTimestamp + " long"
                + ");");
    }

    public String getWordsTableName(long dict_id) {
        return "words_" + String.valueOf(dict_id);
    }

	public long insertDict(Dictionary dict){
		ContentValues cv = new ContentValues();
		cv.put(dColName, dict.getName());
		cv.put(dWordsCount, dict.getWordsCount());

		SQLiteDatabase db = getWritableDatabase();
        long dict_id = db.insert(dictTable, null, cv);

        createWordsTable(db, dict_id);

		return dict_id;
	}

    public void insertWord(long dict_id, Word word) {
        String table_name = getWordsTableName(dict_id);
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(wColWord, word.getWord());
        cv.put(wColTranslation, word.getTranslationData());
        cv.put(wColStatus, word.getStat());
        Time time = new Time();
        time.setToNow();
        cv.put(wColLastTimestamp, time.toMillis(false));

        db.insert(table_name, null, cv);
        // инкрементируем количество слов
        changeWordsCount(dict_id, 1);
    }

    // изменение значения числа слов на delta
    public void changeWordsCount(long dict_id, int delta){
        SQLiteDatabase db = getWritableDatabase();

        String where = "_id = " + Long.toString(dict_id);

        Cursor cursor = db.query(dictTable, new String[]{"words_count"}, where, null, null, null, null);

        // Если словарь не найден по ид
        if(!cursor.moveToFirst()){return;}

        int wCount = cursor.getInt(cursor.getColumnIndex(dWordsCount));
        wCount = wCount + delta;

        ContentValues cv = new ContentValues();
        cv.put(dWordsCount, wCount);

        db.update(dictTable, cv, where, null);
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
                dict.setId(cursor.getLong(cursor.getColumnIndex(dColId)));
                dict.setName(cursor.getString(cursor.getColumnIndex(dColName)));
                dict.setWordsCount(cursor.getInt(cursor.getColumnIndex(dWordsCount)));

	        	result.add(dict);
	        	cursor_chk = cursor.moveToNext();
	        } 
	    }
		
		cursor.close();

        return result;
	}

    public List<Word> loadWords(long dict_id, int count, int offset){
        List<Word> result = new ArrayList<Word>();

        SQLiteDatabase db = getReadableDatabase();

        String order_by = wColWord + " DESC";

        Cursor cursor = db.query(getWordsTableName(dict_id), null, null, null, null, null, order_by);

        if(!cursor.moveToFirst()){return result;}

        boolean cursor_chk = true;
        for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();

        for (int i = 0; i < count; i++) {
            if(cursor_chk){
                Word word = new Word();
                word.setId(cursor.getLong(cursor.getColumnIndex(wColId)));
                word.setWord(cursor.getString(cursor.getColumnIndex(wColWord)));
                word.setTranslationFromData(cursor.getString(cursor.getColumnIndex(wColTranslation)));
                word.setStat(cursor.getInt(cursor.getColumnIndex(wColStatus)));

                result.add(word);
                cursor_chk = cursor.moveToNext();
            }
        }

        cursor.close();

        return result;
    }

    public List<Word> loadWordsForLearn(long dict_id, int count, int offset, int stat_from, int stat_to){
        Time time = new Time();
        time.setToNow();
        return loadWordsForLearn(dict_id, count, offset, stat_from, stat_to, time.toMillis(false));
    }

    public List<Word> loadWordsForLearn(long dict_id, int count, int offset, int stat_from, int stat_to, long timestamp){
        List<Word> result = new ArrayList<Word>();

        SQLiteDatabase db = getReadableDatabase();

        String selection = wColStatus + ">=? AND " + wColStatus + "<=? AND " + wColLastTimestamp + "<?";
        String[] selectionArgs = new String[] { String.valueOf(stat_from), String.valueOf(stat_to), String.valueOf(timestamp)};
        String order_by = wColLastTimestamp + " ASC";

        Cursor cursor = db.query(getWordsTableName(dict_id), null, selection, selectionArgs, null, null, order_by);

        if(!cursor.moveToFirst()){return result;}

        boolean cursor_chk = true;
        for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();

        for (int i = 0; i < count; i++) {
            if(cursor_chk){
                Word word = new Word();
                word.setId(cursor.getLong(cursor.getColumnIndex(wColId)));
                word.setWord(cursor.getString(cursor.getColumnIndex(wColWord)));
                word.setTranslationFromData(cursor.getString(cursor.getColumnIndex(wColTranslation)));

                result.add(word);
                cursor_chk = cursor.moveToNext();
            }
        }

        cursor.close();

        return result;
    }

    public Dictionary getDictById(long dict_id){
        Dictionary dict = null;
        SQLiteDatabase db = getReadableDatabase();

        String selection = dColId + "=?";
        String sel_args[] = {String.valueOf(dict_id)};

        Cursor cursor = db.query(dictTable, null, selection, sel_args, null, null, null);

        if(cursor.moveToFirst()){
            dict = new Dictionary();
            dict.setId(cursor.getLong(cursor.getColumnIndex(dColId)));
            dict.setName(cursor.getString(cursor.getColumnIndex(dColName)));
            dict.setWordsCount(cursor.getInt(cursor.getColumnIndex(dWordsCount)));
        }
        cursor.close();

        return dict;
    }

    public Word getWordById(long word_id, long dict_id){
        Word word = null;
        SQLiteDatabase db = getReadableDatabase();

        String table_name = getWordsTableName(dict_id);
        String selection = wColId + "=?";
        String sel_args[] = {String.valueOf(word_id)};

        Cursor cursor = db.query(table_name, null, selection, sel_args, null, null, null);

        if(cursor.moveToFirst()){
            word = new Word();
            word.setId(cursor.getLong(cursor.getColumnIndex(wColId)));
            word.setWord(cursor.getString(cursor.getColumnIndex(wColWord)));
            word.setTranslationFromData(cursor.getString(cursor.getColumnIndex(wColTranslation)));
        }
        cursor.close();

        return word;
    }

    public void updateWordById(Word word, long dict_id){
        String table_name = getWordsTableName(dict_id);
        SQLiteDatabase db = getWritableDatabase();
        String selection = wColId + "=?";
        String sel_args[] = {String.valueOf(word.getId())};

        ContentValues cv = new ContentValues();
        cv.put(wColWord, word.getWord());
        cv.put(wColTranslation, word.getTranslationData());
        cv.put(wColStatus, word.getStat());
        Time time = new Time();
        time.setToNow();
        cv.put(wColLastTimestamp, time.toMillis(false));

        db.update(table_name, cv, selection, sel_args);
    }

}
