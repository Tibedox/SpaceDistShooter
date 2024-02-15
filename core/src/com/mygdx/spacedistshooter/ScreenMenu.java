package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenMenu implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontLarge;

    Texture imgBackGround;

    SpaceButton btnPlay;
    SpaceButton btnSettings;
    SpaceButton btnAbout;
    SpaceButton btnExit;

    public ScreenMenu(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;
        fontLarge = spaceDS.fontLarge;

        imgBackGround = new Texture("bg1.jpg");

        btnPlay = new SpaceButton("PLAY", 200, 1500, fontLarge);
        btnSettings = new SpaceButton("SETTINGS", 200, 1350, fontLarge);
        btnAbout = new SpaceButton("ABOUT", 200, 1200, fontLarge);
        btnExit = new SpaceButton("EXIT", 200, 1050, fontLarge);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(btnPlay.hit(touch.x, touch.y)){
                spaceDS.setScreen(spaceDS.screenGame);
            }
            if(btnSettings.hit(touch.x, touch.y)){
                //spaceDS.setScreen(spaceDS.screenGame);
            }
            if(btnAbout.hit(touch.x, touch.y)){
                //spaceDS.setScreen(spaceDS.screenGame);
            }
            if(btnExit.hit(touch.x, touch.y)){
                Gdx.app.exit();
            }
        }

        // события игры

        // отрисовка
        ScreenUtils.clear(1, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        btnPlay.font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
        btnSettings.font.draw(batch, btnSettings.text, btnSettings.x, btnSettings.y);
        btnAbout.font.draw(batch, btnAbout.text, btnAbout.x, btnAbout.y);
        btnExit.font.draw(batch, btnExit.text, btnExit.x, btnExit.y);
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
        imgBackGround.dispose();
    }
}
