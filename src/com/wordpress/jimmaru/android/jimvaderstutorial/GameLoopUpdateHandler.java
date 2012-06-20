package com.wordpress.jimmaru.android.jimvaderstutorial;

import org.andengine.engine.handler.IUpdateHandler;

public class GameLoopUpdateHandler implements IUpdateHandler{

	@Override
	public void onUpdate(float pSecondsElapsed) {
		((GameScene)BaseActivity.getSharedInstance().mCurrentScene).moveShip();
		((GameScene)BaseActivity.getSharedInstance().mCurrentScene).cleaner();
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
