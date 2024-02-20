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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenAbout implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontLarge;
    BitmapFont fontSmall;

    Texture imgBackGround;

    SpaceButton btnBack;
    String textAbout=
            "Это космическая аркада,\n" +
            "суперигра, в которую\n" +
            "можно играть,\n" +
            "или не играть.\n" +
            "Сделана в IT школе.";

    public ScreenAbout(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;
        fontLarge = spaceDS.fontLarge;
        fontSmall = spaceDS.fontSmall;

        imgBackGround = new Texture("bg3.jpg");

        btnBack = new SpaceButton("Back to Menu", SCR_HEIGHT/10, fontLarge);
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

            if(btnBack.hit(touch.x, touch.y)){
                spaceDS.setScreen(spaceDS.screenMenu);
            }
        }

        // события игры

        // отрисовка
        ScreenUtils.clear(1, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        fontLarge.draw(batch, "Об Игре", 0, SCR_HEIGHT/10*9, SCR_WIDTH, Align.center, true);
        fontSmall.draw(batch, textAbout, 100, SCR_HEIGHT/10*6);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
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
