package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenGame implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    Texture imgStars;

    Stars[] stars = new Stars[2];

    public ScreenGame(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;

        imgStars = new Texture("stars.png");

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания

        // события игры
        for (Stars s: stars) {
            s.move();
        }

        // отрисовка
        ScreenUtils.clear(1, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgStars, s.x, s.y, s.width, s.height);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        imgStars.dispose();
    }
}