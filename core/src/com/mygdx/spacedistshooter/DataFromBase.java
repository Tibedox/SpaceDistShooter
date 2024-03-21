package com.mygdx.spacedistshooter;

import com.google.gson.annotations.SerializedName;

public class DataFromBase {
    @SerializedName("id")
    int id;

    @SerializedName("playername")
    String name;

    @SerializedName("score")
    int score;

    @SerializedName("daterecord")
    String daterecord;
}
