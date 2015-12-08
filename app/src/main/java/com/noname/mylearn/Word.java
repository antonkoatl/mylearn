package com.noname.mylearn;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    private String word;
    private String translation;
    private long id = -1;

    public Word() {

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
        id = parcel.readInt();
    }
}
