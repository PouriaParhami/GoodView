package com.redfirelab.android.wpmobileapp.ultilities;

/**
 * Created by Pouria on 11/21/2017.
 * wpMApp project.
 */

public class WordpressCategoryData {

    private int id;
    private int count;
    private String name;

    public WordpressCategoryData(int id, int count, String name) {
        this.id = id;
        this.count = count;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
