package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;

import com.badlogic.gdx.math.MathUtils;

public class Fragment extends SpaceObject{
    int type;
    float angle, speedRotation;

    public Fragment(float x, float y, float width, float height, int type) {
        this.type = type;
        this.width = MathUtils.random(width/20, width/3);
        this.height = MathUtils.random(height/20, height/3);
        this.x = x;
        this.y = y;
        float a = MathUtils.random(0f, 360f);
        float v = MathUtils.random(1f, 7f);
        vx = v*MathUtils.sin(a);
        vy = v*MathUtils.cos(a);
        speedRotation = MathUtils.random(-5f, 5f);
    }

    @Override
    void move() {
        super.move();
        angle+=speedRotation;
    }

    boolean outOfScreen(){
        return y < -height/2 || x < -height/2 || y > SCR_HEIGHT+height/2 || x > SCR_WIDTH+width/2;
    }
}
