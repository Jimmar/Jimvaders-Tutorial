package com.wordpress.jimmaru.android.jimvaderstutorial;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;

public class Enemy {
	public Rectangle sprite;
	public int hp;
	// the max health for each enemy
	protected final int MAX_HEALTH = 2;

	public Enemy() {
		sprite = new Rectangle(0, 0, 30, 30, BaseActivity.getSharedInstance()
				.getVertexBufferObjectManager());
		sprite.setColor(0.09904f, 0.8574f, 0.1786f);
		init();
	}

	// method for initializing the Enemy object , used by the constructor and
	// the EnemyPool class
	public void init() {
		hp = MAX_HEALTH;
		sprite.registerEntityModifier(new LoopEntityModifier(
				new RotationModifier(5, 0, 360)));

	}

	public void clean() {
		sprite.clearEntityModifiers();
		sprite.clearUpdateHandlers();
	}

	// method for applying hit and checking if enemy died or not
	// returns false if enemy died
	public boolean gotHit() {
		synchronized (this) {
			hp--;
			if (hp <= 0)
				return false;
			else
				return true;
		}
	}
}
