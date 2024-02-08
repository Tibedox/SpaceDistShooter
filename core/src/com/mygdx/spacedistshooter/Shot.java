package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;

public class Shot extends SpaceObject{

    public Shot(float x, float y) {
        width = height = 200;
        this.x = x;
        this.y = y;
        vy = 8f;
    }

    boolean outOfScreen(){
        return y > SCR_HEIGHT+height/2;
    }

    boolean hitEnemy(Enemy enemy){
        return Math.abs(x-enemy.x) < width/3 + enemy.width/3 &
               Math.abs(y-enemy.y) < height/3 + enemy.height/3;
    }
}
