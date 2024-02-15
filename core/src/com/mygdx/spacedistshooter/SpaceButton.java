package com.mygdx.spacedistshooter;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class SpaceButton {
    BitmapFont font;
    String text;
    float x, y;
    float width, height;

    public SpaceButton(String text, float x, float y, BitmapFont font) {
        this.font = font;
        this.text = text;
        GlyphLayout layout = new GlyphLayout(font, text);
        width = layout.width;
        height = layout.height;
        this.x = x;
        this.y = y;
    }

    boolean hit(float tx, float ty) {
        return x<tx & tx<x+width & y>ty & ty>y-height;
    }
}
