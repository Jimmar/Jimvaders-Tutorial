//source: https://c0deattack.wordpress.com/2011/01/06/andengine-using-the-object-pool/
package com.wordpress.jimmaru.android.jimvaderstutorial;

import org.andengine.util.adt.pool.GenericPool;

import android.util.Log;

public class EnemyPool extends GenericPool<Enemy> {

	public static EnemyPool instance;

	public static EnemyPool sharedEnemyPool() {

		if (instance == null)
			instance = new EnemyPool();
		return instance;

	}

	private EnemyPool() {
		super();
	}

	@Override
	protected Enemy onAllocatePoolItem() {
		return new Enemy();
	}

	@Override
	protected void onHandleObtainItem(Enemy pItem) {
		pItem.init();
	}

	protected void onHandleRecycleItem(final Enemy e) {
		e.sprite.setVisible(false);
		e.sprite.detachSelf();
		e.clean();
		Log.v("Jimvaders", "EnemyPool onHandleRecycleItem()");
	}
}