package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenGame implements Screen {
    SpaceDistShooter spaceDS;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontSmall;
    BitmapFont fontLarge;

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
    long timeSpawnLastEnemy, timeSpawnEnemyInterval = 1500;
    Array<Shot> shots = new Array<>();
    long timeSpawnLastShot, timeSpawnShotInterval = 800;
    Array<Fragment> fragments = new Array<>();
    Player[] players = new Player[11];

    SpaceButton btnBack;

    int nFragments = 50;
    int nLives = 1;
    int kills;
    boolean isGameOver;

    public ScreenGame(SpaceDistShooter spaceDS) {
        this.spaceDS = spaceDS;

        // проверяем, включены ли датчики гироскопа и акселерометра
        //isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        //isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        batch = spaceDS.batch;
        camera = spaceDS.camera;
        touch = spaceDS.touch;
        fontSmall = spaceDS.fontSmall;
        fontLarge = spaceDS.fontLarge;

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

        btnBack = new SpaceButton("Back to Menu", SCR_HEIGHT/10, fontSmall);

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship(imgShip.length);

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Noname", 0);
        }
        loadRecords();
    }

    @Override
    public void show() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        touch.set(0, 0, 0);
        gameStart();
    }

    @Override
    public void render(float delta) {
        // касания
        if (Gdx.input.isTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            ship.touch(touch.x);

            if(isGameOver && btnBack.hit(touch.x, touch.y)){
                spaceDS.setScreen(spaceDS.screenMenu);
            }
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

        if(ship.isAlive) {
            ship.move();
            spawnEnemy();
            spawnShot();
        } else if(!isGameOver) {
            restartRound();
        }

        for (int i=0; i<enemies.size; i++){
            enemies.get(i).move();
            if(enemies.get(i).outOfScreen()){
                enemies.removeIndex(i);
                if(ship.isAlive) {
                    killShip();
                }
                break;
            }
            if(ship.isAlive && enemies.get(i).overlap(ship)){
                spawnFragments(enemies.get(i));
                enemies.removeIndex(i);
                killShip();
            }
        }

        for (int i=0; i<shots.size; i++){
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.removeIndex(i);
                continue;
            }
            for (int j = 0; j < enemies.size; j++) {
                if(shots.get(i).overlap(enemies.get(j))){
                    spawnFragments(enemies.get(j));
                    shots.removeIndex(i);
                    enemies.removeIndex(j);
                    kills++;
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
        if(ship.isAlive) {
            batch.draw(imgShip[ship.phase], ship.getX(), ship.getY(), ship.width, ship.height);
        }
        for (int i = 0; i < ship.lives; i++) {
            batch.draw(imgShip[0], SCR_WIDTH-100-100*i, SCR_HEIGHT-100, 80, 80);
        }
        fontSmall.draw(batch, "Kills: "+kills, 20, SCR_HEIGHT-20);
        if(isGameOver) {
            fontLarge.draw(batch, "GAME OVER", 0, SCR_HEIGHT / 4 * 3, SCR_WIDTH, Align.center, true);
            for (int i = 0; i < players.length-1; i++) {
                fontSmall.draw(batch, i+1+" "+players[i].name, 200, 1400-i*100);
                fontSmall.draw(batch, "............."+players[i].score, 200, 1400-i*100,  SCR_WIDTH-200*2, Align.right, true);
            }
            btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
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
            if(spaceDS.isSoundOn) {
                sndShot.play(0.2f);
            }
        }
    }

    void spawnFragments(SpaceObject o){
        if(spaceDS.isSoundOn) {
            sndExplosion.play();
        }
        for (int i = 0; i < nFragments; i++) {
            fragments.add(new Fragment(o.x, o.y, o.width, o.height, o.type));
        }
    }

    void killShip() {
        spawnFragments(ship);
        ship.isAlive = false;
        if(--ship.lives == 0) {
            gameOver();
        }
    }

    void restartRound() {
        if(shots.size == 0 & enemies.size == 0) {
            fragments.clear();
            ship.reSpawn();
        }
    }

    void gameStart() {
        ship.reSpawn();
        ship.lives = nLives;
        isGameOver = false;
        enemies.clear();
        shots.clear();
        fragments.clear();
        kills = 0;
    }

    void gameOver() {
        isGameOver = true;
        players[players.length-1].name = spaceDS.playerName;
        players[players.length-1].score = kills;
        sortRecords();
        saveRecords();
    }

    void sortRecords() {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < players.length - 1; i++) {
                if(players[i].score<players[i+1].score){
                    Player p = players[i];
                    players[i] = players[i+1];
                    players[i+1] = p;
                    flag = true;
                }
            }
        }
    }

    void saveRecords() {
        Preferences preferences = Gdx.app.getPreferences("SpaceDistShooterRecords");
        for (int i = 0; i < players.length; i++) {
            preferences.putString("name"+i, players[i].name);
            preferences.putInteger("score"+i, players[i].score);
        }
        preferences.flush();
    }

    void loadRecords() {
        Preferences prefs = Gdx.app.getPreferences("SpaceDistShooterRecords");
        for (int i = 0; i < players.length; i++) {
            if(prefs.contains("name"+i)) players[i].name = prefs.getString("name" + i);
            if(prefs.contains("score"+i)) players[i].score = prefs.getInteger("score" + i);
        }
    }

    void clearRecords() {
        for (int i = 0; i < players.length; i++) {
            players[i].name = "Noname";
            players[i].score = 0;
        }
    }
}
