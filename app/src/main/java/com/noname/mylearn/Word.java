package com.noname.mylearn;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    private String word;
    private String translation;
    private long id = -1;
    private long dict_id = -1;

    // Статус слова при изучении
    public static final int S_NEW = 0;
    public static final int S_TEST1 = 1;
    public static final int S_TYPE1 = 2;
    public static final int S_TEST2 = 3;
    public static final int S_TYPE2 = 4;
    public static final int S_CNTRL = 5;
    public static final int S_SM_TEST1 = 6;
    public static final int S_SM_TYPE1 = 7;
    public static final int S_SM_CNTRL = 8;
    public static final int S_LM_CNTRL = 9;
    public static final int S_LM_LEARNED = 10;

    // Изменение статуста
    public static final int ST_SUCCESS = 1;
    public static final int ST_FAIL = 2;
    public static final int ST_GOOD = 3;
    public static final int ST_BAD = 4;

    private int stat = 0;

    public Word() {

    }

    public Word(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslationData() {
        return translation;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslationFromData(String string) {
        this.translation = string;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setDictId(long id) {
        this.dict_id = id;
    }

    public long getDictId() {
        return dict_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // упаковываем объект в Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeString(translation);
        dest.writeLong(id);
        dest.writeInt(stat);
        dest.writeLong(dict_id);
    }

    // распаковываем объект из Parcel
    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Word(Parcel parcel) {
        word = parcel.readString();
        translation = parcel.readString();
        id = parcel.readLong();
        stat = parcel.readInt();
        dict_id = parcel.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word1 = (Word) o;

        if (!getWord().equals(word1.getWord())) return false;
        return translation.equals(word1.translation);
    }

    @Override
    public int hashCode() {
        int result = getWord().hashCode();
        result = 31 * result + translation.hashCode();
        return result;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public void updateStat(int type){
        switch (type) {
            case ST_SUCCESS:
                setStat(getStat() + 1);
                break;
            case ST_FAIL:
                setStat(S_NEW);
                break;
            case ST_GOOD:
                switch (getStat()) {
                    case S_CNTRL:
                        setStat(S_TEST2);
                        break;
                    case S_SM_CNTRL:
                    case S_LM_CNTRL:
                        setStat(S_SM_TEST1);
                        break;
                }
                break;
            case ST_BAD:
                setStat(S_TEST1);
                break;
        }
    }
}
