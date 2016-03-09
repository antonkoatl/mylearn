package com.noname.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

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

    private void createTestDict(SQLiteDatabase db){
        String[][] words = {
                {"have", "иметь"},
                {"be", "быть"},
                {"do", "делать"},
                {"say", "говорить"},
                {"go", "идти"},
                {"get", "получать"},
                {"know", "знать"},
                {"see", "видеть"},
                {"come", "приходить"},
                {"think", "думать"},
                {"take", "брать"},
                {"make", "делать"},
                {"want", "хотеть"},
                {"tell", "говорить"},
                {"turn", "поворачивать"},
                {"open", "открывать"},
                {"give", "давать"},
                {"ask", "спрашивать"},
                {"move", "двигать"},
                {"stand", "стоять"},
        };

        Dictionary dict = new Dictionary();
        dict.setName("Test");
        dict.setWordsCount(words.length);

        ContentValues cv = new ContentValues();
        cv.put(dColName, dict.getName());
        cv.put(dWordsCount, dict.getWordsCount());
        long dict_id = db.insert(dictTable, null, cv);
        createWordsTable(db, dict_id);

        for(String[] w: words){
            Word word = new Word();
            word.setWord(w[0]);
            word.setTranslationFromData(w[1]);

            String table_name = getWordsTableName(dict_id);

            cv = new ContentValues();
            cv.put(wColWord, w[0]);
            cv.put(wColTranslation, w[1]);
            cv.put(wColStatus, 0);
            Time time = new Time();
            time.setToNow();
            cv.put(wColLastTimestamp, time.toMillis(false));

            db.insert(table_name, null, cv);
        }
    }

	private void createTablesDb(SQLiteDatabase db){
		db.execSQL("create table IF NOT EXISTS " + dictTable + " ("
                + dColId + " integer primary key autoincrement,"
                + dColName + " text,"
                + dWordsCount + " integer"
                + ");");

        createTestDict(db);
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

        word.setDictId(dict_id);

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

        // String order_by = dColName + " DESC";
		
		Cursor cursor = db.query(dictTable, null, null, null, null, null, null);
		
		if(!cursor.moveToFirst()){return result;}
		
		boolean cursor_chk = true;
		for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();
		
		for (int i = 0; i < count; i++) {
			if(cursor_chk){
	        	Dictionary dict = readDictFromCursor(cursor);

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
                Word word = readWordFromCursor(cursor, dict_id);

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
        return loadWordsForLearn(dict_id, count, offset, stat_from, stat_to, time.toMillis(false), true);
    }

    public List<Word> loadWordsForLearn(long dict_id, int count, int offset, int stat_from, int stat_to, boolean asc){
        Time time = new Time();
        time.setToNow();
        return loadWordsForLearn(dict_id, count, offset, stat_from, stat_to, time.toMillis(false), asc);
    }

    public List<Word> loadWordsForLearn(long dict_id, int count, int offset, int stat_from, int stat_to, long timestamp){
        return loadWordsForLearn(dict_id, count, offset, stat_from, stat_to, timestamp, true);
    }

    public List<Word> loadWordsForLearn(long dict_id, int count, int offset, int stat_from, int stat_to, long timestamp, boolean asc){
        List<Word> result = new ArrayList<Word>();

        SQLiteDatabase db = getReadableDatabase();

        String selection = wColStatus + ">=? AND " + wColStatus + "<=? AND " + wColLastTimestamp + "<=?";
        String[] selectionArgs = new String[] { String.valueOf(stat_from), String.valueOf(stat_to), String.valueOf(timestamp)};
        String order_by = asc ? wColLastTimestamp + " ASC" : wColLastTimestamp + " DESC";

        Cursor cursor = db.query(getWordsTableName(dict_id), null, selection, selectionArgs, null, null, order_by);

        if(!cursor.moveToFirst()){return result;}

        boolean cursor_chk = true;
        for (int i = 0; i < offset; i++) cursor_chk = cursor.moveToNext();

        for (int i = 0; i < count; i++) {
            if(cursor_chk){
                Word word = readWordFromCursor(cursor, dict_id);

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

        if (cursor.moveToFirst()) {
            dict = readDictFromCursor(cursor);
        }

        cursor.close();

        return dict;
    }

    public Dictionary[] getDictsById(long[] dict_ids) {
        Dictionary[] dicts = new Dictionary[dict_ids.length];

        for (int i = 0; i < dict_ids.length; i++) {
            dicts[i] = getDictById(dict_ids[i]);
        }

        return dicts;
    }

    public Word getWordById(long word_id, long dict_id){
        Word word = null;
        SQLiteDatabase db = getReadableDatabase();

        String table_name = getWordsTableName(dict_id);
        String selection = wColId + "=?";
        String sel_args[] = {String.valueOf(word_id)};

        Cursor cursor = db.query(table_name, null, selection, sel_args, null, null, null);

        if(cursor.moveToFirst()){
            word = readWordFromCursor(cursor, dict_id);
        }
        cursor.close();

        return word;
    }

    private Word readWordFromCursor(Cursor cursor, long dict_id) {
        Word word = new Word();
        word.setId(cursor.getLong(cursor.getColumnIndex(wColId)));
        word.setWord(cursor.getString(cursor.getColumnIndex(wColWord)));
        word.setStat(cursor.getInt(cursor.getColumnIndex(wColStatus)));
        word.setTranslationFromData(cursor.getString(cursor.getColumnIndex(wColTranslation)));
        word.setDictId(dict_id);
        return word;
    }

    private Dictionary readDictFromCursor(Cursor cursor) {
        Dictionary dict = new Dictionary();
        dict.setId(cursor.getLong(cursor.getColumnIndex(dColId)));
        dict.setName(cursor.getString(cursor.getColumnIndex(dColName)));
        dict.setWordsCount(cursor.getInt(cursor.getColumnIndex(dWordsCount)));
        return dict;
    }

    public void updateWord(Word word) {
        String table_name = getWordsTableName(word.getDictId());
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

    public void deleteWordById(long word_id, long dict_id) {
        String table_name = getWordsTableName(dict_id);
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = wColId + "=?";
        String[] whereArgs = new String[] { String.valueOf(word_id) };

        db.delete(table_name, whereClause, whereArgs);

        changeWordsCount(dict_id, -1);
    }

    public Word getWord(String word, String translation, long dict_id) {
        Word result = null;

        SQLiteDatabase db = getReadableDatabase();

        String selection = wColWord + "=? AND " + wColTranslation + "=?";
        String[] selectionArgs = new String[] { word, translation};

        Cursor cursor = db.query(getWordsTableName(dict_id), null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()) {
            result = readWordFromCursor(cursor, dict_id);
        }
        cursor.close();

        return result;
    }

    public int countWords(long dict_id, int stat_from, int stat_to) {
        Time time = new Time();
        time.setToNow();
        return countWords(dict_id, stat_from, stat_to, time.toMillis(false));
    }

    public int countWords(long dict_id, int stat_from, int stat_to, long timestamp) {
        int result = 0;
        SQLiteDatabase db = getReadableDatabase();

        String selection = wColStatus + ">=? AND " + wColStatus + "<=? AND " + wColLastTimestamp + "<=?";
        String[] selectionArgs = new String[] { String.valueOf(stat_from), String.valueOf(stat_to), String.valueOf(timestamp)};

        Cursor cursor = db.query(getWordsTableName(dict_id), null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){
            result = cursor.getCount();
        } else {
            result = 0;
        }

        cursor.close();

        return result;
    }

    public int countWordsLToday(long dictId) {
        Time time = new Time();
        time.setToNow();
        long time_last_day = time.toMillis(false) - LearnAdapter.MILLIS_IN_DAY;
        long time_last_week = time.toMillis(false) - LearnAdapter.MILLIS_IN_WEEK;

        int today = countWords(dictId, Word.S_TEST1, Word.S_CNTRL);
        today += countWords(dictId, Word.S_SM_TEST1, Word.S_SM_CNTRL, time_last_day);
        today += countWords(dictId, Word.S_LM_CNTRL, Word.S_LM_CNTRL, time_last_week);
        return today;
    }

    public int countWordsLTomorrow(long dictId) {
        Time time = new Time();
        time.setToNow();
        long time_last_week = time.toMillis(false) - LearnAdapter.MILLIS_IN_WEEK;

        int tomorrow = countWords(dictId, Word.S_SM_TEST1, Word.S_SM_CNTRL);
        tomorrow += countWords(dictId, Word.S_LM_CNTRL, Word.S_LM_CNTRL, (long) (time_last_week*0.857));
        return tomorrow;
    }

    public int countWordsNew(long dictId) {
        return countWords(dictId, Word.S_NEW, Word.S_NEW);
    }
}
