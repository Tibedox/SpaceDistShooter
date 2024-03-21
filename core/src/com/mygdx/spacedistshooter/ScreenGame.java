package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    List<DataFromBase> db = new ArrayList<>();

    SpaceButton btnBack;
    SpaceButton btnSwitchRecords;
    boolean isShowGlobalRecords;

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

        btnSwitchRecords = new SpaceButton("Global/Local records", SCR_HEIGHT/10, fontSmall);
        btnBack = new SpaceButton("Back to Menu", SCR_HEIGHT/10-100, fontSmall);

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
        }
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if(isGameOver && btnBack.hit(touch.x, touch.y)){
                spaceDS.setScreen(spaceDS.screenMenu);
            }
            if(isGameOver && btnSwitchRecords.hit(touch.x, touch.y)){
                isShowGlobalRecords = ! isShowGlobalRecords;
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

            if(isShowGlobalRecords){
                fontSmall.draw(batch, "Global Records", 0, SCR_HEIGHT / 4 * 3-200, SCR_WIDTH, Align.center, true);
                for (int i = 0; i < players.length - 1; i++) {
                    fontSmall.draw(batch, i + 1 + " " + db.get(i).name, 200, 1300 - i * 100);
                    String points = amountPoints(fontSmall, i + 1 + " " + db.get(i).name, "" + db.get(i).score, SCR_WIDTH - 200 * 2);
                    fontSmall.draw(batch, points + db.get(i).score, 200, 1300 - i * 100, SCR_WIDTH - 200 * 2, Align.right, true);
                }
            } else {
                fontSmall.draw(batch, "Local Records", 0, SCR_HEIGHT / 4 * 3-200, SCR_WIDTH, Align.center, true);
                for (int i = 0; i < players.length - 1; i++) {
                    fontSmall.draw(batch, i + 1 + " " + players[i].name, 200, 1300 - i * 100);
                    String points = amountPoints(fontSmall, i + 1 + " " + players[i].name, "" + players[i].score, SCR_WIDTH - 200 * 2);
                    fontSmall.draw(batch, points + players[i].score, 200, 1300 - i * 100, SCR_WIDTH - 200 * 2, Align.right, true);
                }
            }
            btnSwitchRecords.font.draw(batch, btnSwitchRecords.text, btnSwitchRecords.x, btnSwitchRecords.y);
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

    private void killShip() {
        spawnFragments(ship);
        ship.isAlive = false;
        if(--ship.lives == 0) {
            gameOver();
        }
    }

    private void restartRound() {
        if(shots.size == 0 & enemies.size == 0) {
            fragments.clear();
            ship.reSpawn();
        }
    }

    private void gameStart() {
        ship.reSpawn();
        ship.lives = nLives;
        isGameOver = false;
        enemies.clear();
        shots.clear();
        fragments.clear();
        kills = 0;
    }

    private void gameOver() {
        isGameOver = true;
        players[players.length-1].name = spaceDS.playerName;
        players[players.length-1].score = kills;
        sortRecords(players);
        saveRecords();
        sendRecordToDataBase();
    }

    private void sortRecords(Player[] players) {
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

    private void sortRecords(List<DataFromBase> players) {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < players.size() - 1; i++) {
                if(players.get(i).score<players.get(i+1).score){
                    DataFromBase p = players.get(i);
                    players.set(i, players.get(i+1));
                    players.set(i+1, p);
                    flag = true;
                }
            }
        }
    }

    private void saveRecords() {
        Preferences preferences = Gdx.app.getPreferences("SpaceDistShooterRecords");
        for (int i = 0; i < players.length; i++) {
            preferences.putString("name"+i, players[i].name);
            preferences.putInteger("score"+i, players[i].score);
        }
        preferences.flush();
    }

    private void loadRecords() {
        Preferences prefs = Gdx.app.getPreferences("SpaceDistShooterRecords");
        for (int i = 0; i < players.length; i++) {
            if(prefs.contains("name"+i)) players[i].name = prefs.getString("name" + i);
            if(prefs.contains("score"+i)) players[i].score = prefs.getInteger("score" + i);
        }
    }

    public void clearRecords() {
        for (int i = 0; i < players.length; i++) {
            players[i].name = "Noname";
            players[i].score = 0;
        }
    }

    private String amountPoints(BitmapFont font, String textLeft, String textRight, float widthLayout) {
        GlyphLayout text1 = new GlyphLayout(font, textLeft);
        GlyphLayout text2 = new GlyphLayout(font, textRight);
        float widthOfAllPoints = widthLayout - text1.width - text2.width;
        GlyphLayout widthOnePoint = new GlyphLayout(font, ".");
        int n = (int) (widthOfAllPoints/widthOnePoint.width)/2;
        String points = "";
        for (int i = 0; i < n; i++) {
            points += ".";
        }
        return points;
    }

    private void sendRecordToDataBase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dist.sch120.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApi myApi = retrofit.create(MyApi.class);

        myApi.send(spaceDS.playerName, kills).enqueue(new Callback<List<DataFromBase>>() {
            @Override
            public void onResponse(Call<List<DataFromBase>> call, Response<List<DataFromBase>> response) {
                db = response.body();
                sortRecords(db);
            }

            @Override
            public void onFailure(Call<List<DataFromBase>> call, Throwable t) {
            }
        });
    }
}
