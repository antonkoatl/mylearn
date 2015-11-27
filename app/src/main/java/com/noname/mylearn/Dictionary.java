package com.noname.mylearn;

public class Dictionary {
    private String name = "Sample Dict";
    private long id = -1;
    private int wordsCount = 0;

    public String getName() {
        return name;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }
}
