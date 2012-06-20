package com.wordpress.jimmaru.android.jimvaderstutorial;

import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class GameScene extends Scene implements IOnSceneTouchListener {
	public Ship ship;
	Camera mCamera;
	public float accelerometerSpeedX;
	SensorManager sensorManager;
	public LinkedList<Bullet> bulletList;
	public int bulletCount;
	public int missCount;

	public GameScene() {
		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		// attaching an EnemyLayer entity with 12 enemies on it
		attachChild(new EnemyLayer(12));
		mCamera = BaseActivity.getSharedInstance().mCamera;
		ship = Ship.getSharedInstance();
		ship.sprite.detachSelf();

		bulletList = new LinkedList<Bullet>();
		attachChild(ship.sprite);
		ship.sprite.setVisible(true);

		BaseActivity.getSharedInstance().setCurrentScene(this);
		sensorManager = (SensorManager) BaseActivity.getSharedInstance()
				.getSystemService(BaseGameActivity.SENSOR_SERVICE);
		SensorListener.getSharedInstance();
		sensorManager.registerListener(SensorListener.getSharedInstance(),
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

		setOnSceneTouchListener(this);

		resetValues();

	}

	// method to reset values and restart the game
	public void resetValues() {
		missCount = 0;
		bulletCount = 0;
		ship.restart();
		EnemyLayer.purgeAndRestart();
		clearChildScene();
		registerUpdateHandler(new GameLoopUpdateHandler());
	}

	public void moveShip() {
		ship.moveShip(accelerometerSpeedX);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (!CoolDown.getSharedInstance().checkValidity())
			return false;

		synchronized (this) {
			ship.shoot();
		}
		return true;
	}

	public void detach() {
		Log.v("Jimvaders", "GameScene onDetached()");
		clearUpdateHandlers();
		for (Bullet b : bulletList) {
			BulletPool.sharedBulletPool().recyclePoolItem(b);
		}
		bulletList.clear();
		detachChildren();
		Ship.instance = null;
		EnemyPool.instance = null;
		BulletPool.instance = null;

	}

	public void cleaner() {
		synchronized (this) {
			// if all Enemies are killed
			if (EnemyLayer.isEmpty()) {
				Log.v("Jimvaders", "GameScene Cleaner() cleared");
				setChildScene(new ResultScene(mCamera));
				clearUpdateHandlers();
			}
			Iterator<Enemy> eIt = EnemyLayer.getIterator();
			while (eIt.hasNext()) {
				Enemy e = eIt.next();
				Iterator<Bullet> it = bulletList.iterator();
				while (it.hasNext()) {
					Bullet b = it.next();
					if (b.sprite.getY() <= -b.sprite.getHeight()) {
						BulletPool.sharedBulletPool().recyclePoolItem(b);
						it.remove();
						missCount++;
						continue;
					}

					if (b.sprite.collidesWith(e.sprite)) {

						if (!e.gotHit()) {
							createExplosion(e.sprite.getX(), e.sprite.getY(),
									e.sprite.getParent(),
									BaseActivity.getSharedInstance());
							EnemyPool.sharedEnemyPool().recyclePoolItem(e);
							eIt.remove();
						}
						BulletPool.sharedBulletPool().recyclePoolItem(b);
						it.remove();
						break;
					}
				}

			}
		}
	}

	private void createExplosion(final float posX, final float posY,
			final IEntity target, final SimpleBaseGameActivity activity) {

		int mNumPart = 15;
		int mTimePart = 2;

		PointParticleEmitter particleEmitter = new PointParticleEmitter(posX,
				posY);
		IEntityFactory<Rectangle> recFact = new IEntityFactory<Rectangle>() {

			@Override
			public Rectangle create(float pX, float pY) {
				Rectangle rect = new Rectangle(posX, posY, 10, 10,
						activity.getVertexBufferObjectManager());
				rect.setColor(Color.GREEN);
				return rect;
			}

		};
		final ParticleSystem<Rectangle> particleSystem = new ParticleSystem<Rectangle>(
				recFact, particleEmitter, 500, 500, mNumPart);

		particleSystem
				.addParticleInitializer(new VelocityParticleInitializer<Rectangle>(
						-50, 50, -50, 50));

		particleSystem
				.addParticleModifier(new AlphaParticleModifier<Rectangle>(0,
						0.6f * mTimePart, 1, 0));
		particleSystem
				.addParticleModifier(new RotationParticleModifier<Rectangle>(0,
						mTimePart, 0, 360));

		target.attachChild(particleSystem);
		target.registerUpdateHandler(new TimerHandler(mTimePart,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						particleSystem.detachSelf();
						target.sortChildren();
						target.unregisterUpdateHandler(pTimerHandler);
					}
				}));

	}
}
