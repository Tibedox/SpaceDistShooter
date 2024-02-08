package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenGame implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    boolean isGyroscopeAvailable;
    boolean isAccelerometerAvailable;

    Texture imgStars;
    Texture imgShipsAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];
    TextureRegion[] imgEnemy = new TextureRegion[12];
    Texture imgShot;

    Stars[] stars = new Stars[2];
    Ship ship;
    Array<Enemy> enemies = new Array<>();
    Array<Shot> shots = new Array<>();
    long timeSpawnLastEnemy, timeSpawnEnemyInterval = 1500;
    long timeSpawnLastShot, timeSpawnShotInterval = 800;

    public ScreenGame(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        // проверяем, включены ли датчики гироскопа и акселерометра
        isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;

        imgStars = new Texture("stars.png");
        imgShipsAtlas = new Texture("ships_atlas3.png");
        imgShot = new Texture("shoot_blaster_red.png");
        for (int i = 0; i < imgShip.length; i++) {
            imgShip[i] = new TextureRegion(imgShipsAtlas, i*400, 0, 400, 400);
            if(i>6) {
                imgShip[i] = new TextureRegion(imgShipsAtlas, (13-i) * 400, 0, 400, 400);
            }
        }
        for (int i = 0; i < imgEnemy.length; i++) {
            imgEnemy[i] = new TextureRegion(imgShipsAtlas, i*400, 1600, 400, 400);
            if(i>6) {
                imgEnemy[i] = new TextureRegion(imgShipsAtlas, (13-i)*400, 1600, 400, 400);
            }
        }

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship(imgShip.length);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания
        if (Gdx.input.isTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            ship.touch(touch.x);
        }
        else if (isAccelerometerAvailable){
            ship.vx = -Gdx.input.getAccelerometerX()*10;
        }
        else if (isGyroscopeAvailable) {
            ship.vx = Gdx.input.getGyroscopeY()*10;
        }

        // события игры
        for (Stars s: stars) {
            s.move();
        }
        ship.move();
        spawnEnemy();
        for (int i=0; i<enemies.size; i++){
            enemies.get(i).move();
            if(enemies.get(i).outOfScreen()){
                enemies.removeIndex(i);
                //Gdx.app.exit();
            }
        }
        spawnShot();
        for (int i=0; i<shots.size; i++){
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.removeIndex(i);
            }
            for (int j = 0; j < enemies.size; j++) {
                if(shots.get(i).hitEnemy(enemies.get(j))){
                    shots.removeIndex(i);
                    enemies.removeIndex(j);
                    break;
                }
            }
        }

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgStars, s.x, s.y, s.width, s.height);
        }
        for (Enemy e: enemies) {
            batch.draw(imgEnemy[e.phase], e.getX(), e.getY(), e.width, e.height);
        }
        for (Shot s: shots) {
            batch.draw(imgShot, s.getX(), s.getY(), s.width, s.height);
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
        imgShot.dispose();
    }

    void spawnEnemy(){
        if(TimeUtils.millis() > timeSpawnLastEnemy+timeSpawnEnemyInterval){
            timeSpawnLastEnemy = TimeUtils.millis();
            enemies.add(new Enemy(imgEnemy.length));
        }
    }

    void spawnShot(){
        if(TimeUtils.millis() > timeSpawnLastShot+timeSpawnShotInterval){
            timeSpawnLastShot = TimeUtils.millis();
            shots.add(new Shot(ship.x, ship.y));
        }
    }
}
