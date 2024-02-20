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

public class ScreenSettings implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontLarge;

    Texture imgBackGround;

    SpaceButton btnName;
    SpaceButton btnSound;
    SpaceButton btnClearRecords;
    SpaceButton btnBack;

    public ScreenSettings(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;
        fontLarge = spaceDS.fontLarge;

        imgBackGround = new Texture("bg2.jpg");

        btnName = new SpaceButton("Name", 50, 1400, fontLarge);
        btnSound = new SpaceButton("Sound On", 50, 1250, fontLarge);
        btnClearRecords = new SpaceButton("Clear Records", 50, 1100, fontLarge);
        btnBack = new SpaceButton("Back to Menu", 50, 950, fontLarge);
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

            if(btnName.hit(touch.x, touch.y)){
                //spaceDS.setScreen(spaceDS.screenGame);
            }
            if(btnSound.hit(touch.x, touch.y)){
                spaceDS.isSoundOn = !spaceDS.isSoundOn;
                if(spaceDS.isSoundOn){
                    btnSound.setText("Sound On");
                } else {
                    btnSound.setText("Sound Off");
                }
            }
            if(btnClearRecords.hit(touch.x, touch.y)){
                //spaceDS.setScreen(spaceDS.screenGame);
            }
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
        btnName.font.draw(batch, btnName.text, btnName.x, btnName.y);
        btnSound.font.draw(batch, btnSound.text, btnSound.x, btnSound.y);
        btnClearRecords.font.draw(batch, btnClearRecords.text, btnClearRecords.x, btnClearRecords.y);
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
