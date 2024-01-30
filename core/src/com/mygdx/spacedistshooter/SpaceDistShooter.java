package com.mygdx.spacedistshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class SpaceDistShooter extends Game {
	public static final float SCR_WIDTH = 1080, SCR_HEIGHT = 2200;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;

	ScreenMenu screenMenu;
	ScreenGame screenGame;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();

		screenMenu = new ScreenMenu(this);
		screenGame = new ScreenGame(this);

		setScreen(screenGame);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
