package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
    BitmapFont fontSmall;

    Texture imgBackGround;

    SpaceButton btnName;
    SpaceButton btnSound;
    SpaceButton btnClearRecords;
    SpaceButton btnBack;

    InputKeyboard keyboard;
    boolean isEnterName;

    public ScreenSettings(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;
        fontLarge = spaceDS.fontLarge;
        fontSmall = spaceDS.fontSmall;

        imgBackGround = new Texture("bg2.jpg");

        loadSettings();

        btnName = new SpaceButton("Name", 30, 1400, fontLarge);
        btnSound = new SpaceButton(spaceDS.isSoundOn?"Sound On":"Sound Off", 30, 1250, fontLarge);
        btnClearRecords = new SpaceButton("Clear Records", 30, 1100, fontLarge);
        btnBack = new SpaceButton("Back", 30, 950, fontLarge);

        keyboard = new InputKeyboard(fontSmall, SCR_WIDTH, SCR_HEIGHT/2, 10);
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

            if(isEnterName){
                if (keyboard.endOfEdit(touch.x, touch.y)) {
                    spaceDS.playerName = keyboard.getText();
                    isEnterName = false;
                }
            } else {
                if (btnName.hit(touch.x, touch.y)) {
                    isEnterName = true;
                }
                if (btnSound.hit(touch.x, touch.y)) {
                    spaceDS.isSoundOn = !spaceDS.isSoundOn;
                    if (spaceDS.isSoundOn) {
                        btnSound.setText("Sound On");
                    } else {
                        btnSound.setText("Sound Off");
                    }
                }
                if (btnClearRecords.hit(touch.x, touch.y)) {
                    spaceDS.screenGame.clearRecords();
                    btnClearRecords.setText("Records cleared");
                }
                if (btnBack.hit(touch.x, touch.y)) {
                    spaceDS.setScreen(spaceDS.screenMenu);
                }
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
        if(isEnterName) {
            keyboard.draw(batch);
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
        btnClearRecords.setText("Clear Records");
        saveSettings();
    }

    @Override
    public void dispose() {
        imgBackGround.dispose();
        keyboard.dispose();
    }

    private void saveSettings() {
        Preferences preferences = Gdx.app.getPreferences("SpaceDistShooterSettings");
        preferences.putBoolean("sound", spaceDS.isSoundOn);
        preferences.flush();
    }

    private void loadSettings() {
        Preferences preferences = Gdx.app.getPreferences("SpaceDistShooterSettings");
        if(preferences.contains("sound")){
            spaceDS.isSoundOn = preferences.getBoolean("sound");
        }
    }
}
