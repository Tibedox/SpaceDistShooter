package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;
import static com.mygdx.spacedistshooter.SpaceDistShooter.TYPE_ENEMY;

import com.badlogic.gdx.math.MathUtils;

public class Enemy extends SpaceObject{

    public Enemy(int nPhases) {
        type = TYPE_ENEMY;
        width = height = 200;
        x = MathUtils.random(width/2, SCR_WIDTH-width/2);
        y = MathUtils.random(SCR_HEIGHT+height, SCR_HEIGHT*2);
        vy = MathUtils.random(-7f, -3f);
        this.nPhases = nPhases;
        timePhaseInterval = 40;
    }

    @Override
    void move() {
        super.move();
        changePhase();
    }

    boolean outOfScreen(){
        return y < -height / 2;
    }
}
