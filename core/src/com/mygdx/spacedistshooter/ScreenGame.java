package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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

    Sound sndShot;
    Sound sndExplosion;

    Texture imgStars;
    Texture imgShot;
    Texture imgShipsAtlas;
    Texture imgFragmentAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];
    TextureRegion[] imgEnemy = new TextureRegion[12];
    TextureRegion[] imgFragment = new TextureRegion[2];

    Stars[] stars = new Stars[2];
    Ship ship;
    Array<Enemy> enemies = new Array<>();
    Array<Shot> shots = new Array<>();
    Array<Fragment> fragments = new Array<>();
    long timeSpawnLastEnemy, timeSpawnEnemyInterval = 1500;
    long timeSpawnLastShot, timeSpawnShotInterval = 800;
    int nFragments = 50;

    public ScreenGame(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        // проверяем, включены ли датчики гироскопа и акселерометра
        //isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        //isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;

        sndShot = Gdx.audio.newSound(Gdx.files.internal("blaster.wav"));
        sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        imgStars = new Texture("stars.png");
        imgShipsAtlas = new Texture("ships_atlas3.png");
        imgShot = new Texture("shoot_blaster_red.png");
        imgFragmentAtlas = new Texture("ships_fragment_atlas.png");
        for (int i = 0; i < imgShip.length; i++) {
            if(i<7) {
                imgShip[i] = new TextureRegion(imgShipsAtlas, i * 400, 0, 400, 400);
            } else {
                imgShip[i] = new TextureRegion(imgShipsAtlas, (13-i) * 400, 0, 400, 400);
            }
        }
        for (int i = 0; i < imgEnemy.length; i++) {
            if(i<7) {
                imgEnemy[i] = new TextureRegion(imgShipsAtlas, i*400, 1600, 400, 400);
            } else {
                imgEnemy[i] = new TextureRegion(imgShipsAtlas, (13-i)*400, 1600, 400, 400);
            }
        }
        imgFragment[0] = new TextureRegion(imgFragmentAtlas, 0, 0, 100, 100);
        imgFragment[1] = new TextureRegion(imgFragmentAtlas, 500, 0, 100, 100);

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
        /*else if (isAccelerometerAvailable){
            ship.vx = -Gdx.input.getAccelerometerX()*10;
        }
        else if (isGyroscopeAvailable) {
            ship.vx = Gdx.input.getGyroscopeY()*10;
        }*/

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
            }
        }
        spawnShot();
        for (int i=0; i<shots.size; i++){
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.removeIndex(i);
                continue;
            }
            for (int j = 0; j < enemies.size; j++) {
                if(shots.get(i).overlap(enemies.get(j))){
                    spawnFragments(enemies.get(j).x, enemies.get(j).y, enemies.get(j).width, enemies.get(j).height, 1);
                    shots.removeIndex(i);
                    enemies.removeIndex(j);
                    sndExplosion.play();
                    break;
                }
            }
        }
        for (int i = 0; i < fragments.size; i++) {
            fragments.get(i).move();
            if(fragments.get(i).outOfScreen()) {
                fragments.removeIndex(i);
            }
        }

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgStars, s.x, s.y, s.width, s.height);
        }
        for (Fragment f: fragments) {
            batch.draw(imgFragment[f.type], f.getX(), f.getY(), f.width/2, f.height/2, f.width, f.height, 1, 1, f.angle);
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
        imgFragmentAtlas.dispose();
        sndShot.dispose();
        sndExplosion.dispose();
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
            sndShot.play(0.2f);
        }
    }

    void spawnFragments(float x, float y, float width, float height, int type){
        for (int i = 0; i < nFragments; i++) {
            fragments.add(new Fragment(x, y, width, height, type));
        }
    }
}
