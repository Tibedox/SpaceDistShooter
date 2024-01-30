package com.mygdx.spacedistshooter;

public class SpaceObject {
    float x, y;
    float vx, vy;
    float width, height;

    void move() {
        x += vx;
        y += vy;
    }

    float getX(){
        return x-width/2;
    }

    float getY(){
        return y-height/2;
    }
}
