package com.wordpress.jimmaru.android.jimvaderstutorial;

import org.andengine.entity.primitive.Rectangle;

public class Bullet {

	public Rectangle sprite;

	public Bullet() {
		sprite = new Rectangle(0, 0, 10, 10, BaseActivity.getSharedInstance()
				.getVertexBufferObjectManager());

		sprite.setColor(0.09904f, 0f, 0.1786f);
	}
}
