package com.mygdx.spacedistshooter;

public class SpaceObject {
    float x, y;
    float vx, vy;
    float width, height;

    void move() {
        x += vx;
        y += vy;
    }
}
