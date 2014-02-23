package com.games.platformerrpg;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	
	private GameView gameView;

	//creating the game
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gameview_layout);
		gameView = (GameView)findViewById(R.id.game);
		gameView.setKeepScreenOn(true);
		
	}
	
	//managing what the back button does
	@Override
	public void onBackPressed() {
		if (gameView.getScreenID() == 0) {
			if (gameView.getScreenState() == 0) {
				gameView.file.write();
				finish();
			}
			else if (gameView.getScreenState() != 0) {
				gameView.goBack();
			}
		}
		else {
			gameView.goBack();
		}		
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_MENU) {

        	if (gameView.getScreenState() != 0) {
        		gameView.goBack();
        	}
            return true;
        }
        return super.onKeyUp(keyCode, event); 
    } 

}
