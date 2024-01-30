package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenGame implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    Texture imgStars;
    Texture imgShipsAtlas;
    TextureRegion[] imgShip = new TextureRegion[7];

    Stars[] stars = new Stars[2];
    Ship ship;

    public ScreenGame(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;

        imgStars = new Texture("stars.png");
        imgShipsAtlas = new Texture("ships_atlas.png");
        for (int i = 0; i < imgShip.length; i++) {
            imgShip[i] = new TextureRegion(imgShipsAtlas, i*400, 0, 400, 400);
        }

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship();
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
        ship.move();

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgStars, s.x, s.y, s.width, s.height);
        }
        batch.draw(imgShip[ship.phase], ship.getX(), ship.getY(), ship.width, ship.height);
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
        imgShipsAtlas.dispose();
    }
}
