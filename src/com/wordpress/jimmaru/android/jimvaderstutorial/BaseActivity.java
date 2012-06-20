package com.wordpress.jimmaru.android.jimvaderstutorial;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Typeface;
import android.util.Log;

public class BaseActivity extends SimpleBaseGameActivity {

	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 480;

	public Font mFont;
	public Camera mCamera;

	// A reference to the current scene
	public Scene mCurrentScene;
	public static BaseActivity instance;

	public static BaseActivity getSharedInstance() {
		return instance;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		instance = this;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	protected void onCreateResources() {
		mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
	}

	@Override
	protected Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		mCurrentScene = new SplashScene();
		return mCurrentScene;
	}

	// to change the current main scene
	public void setCurrentScene(Scene scene) {
		mCurrentScene = null;
		mCurrentScene = scene;
		getEngine().setScene(mCurrentScene);

	}

	@Override
	public void onBackPressed() {
		Log.v("Jimvaders",
				"BaseActivity BackPressed " + mCurrentScene.toString());
		if (mCurrentScene instanceof GameScene)
			((GameScene) mCurrentScene).detach();

		mCurrentScene = null;
		SensorListener.instance = null;
		super.onBackPressed();
	}

}