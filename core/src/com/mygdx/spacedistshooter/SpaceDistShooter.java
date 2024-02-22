package com.mygdx.spacedistshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class SpaceDistShooter extends Game {
	public static final float SCR_WIDTH = 1000, SCR_HEIGHT = 2200;
	public static final int TYPE_SHIP = 0, TYPE_ENEMY = 1;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;
	BitmapFont fontSmall, fontLarge;

	ScreenMenu screenMenu;
	ScreenGame screenGame;
	ScreenSettings screenSettings;
	ScreenAbout screenAbout;

	boolean isSoundOn = true;
	String playerName = "Player";
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		fontSmall = new BitmapFont(Gdx.files.internal("ds_crystal.fnt"));
		fontLarge = new BitmapFont(Gdx.files.internal("ds_crystal2.fnt"));

		screenMenu = new ScreenMenu(this);
		screenGame = new ScreenGame(this);
		screenSettings = new ScreenSettings(this);
		screenAbout = new ScreenAbout(this);

		setScreen(screenMenu);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		fontSmall.dispose();
		fontLarge.dispose();
	}
}
