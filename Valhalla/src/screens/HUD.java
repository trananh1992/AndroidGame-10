package screens;

import java.util.ArrayList;

import com.games.platformerrpg.R;
import com.games.platformerrpg.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

//load and scale all hud elements once and updates them as necessary - extends screen just for slight convenience with variables
public class HUD{
	//screen variables
	public boolean sound;
	public Bitmap backgroundImg;
	public ArrayList<ScreenElement> elements;
	protected Context context;
	protected float xScale;
	protected float yScale;
	protected int screenWidth = 1280;
	protected int screenHeight = 720;
	
	//keeping track of state
	public boolean paused = false;
	
	//allowing game to draw stats
	public boolean drawStats = false;
	
	//boolean indicating hud animation status
	public boolean inMotion = false;
	
	//overlays for frequently pressed buttons
	private ScreenElement movementOverlay;
	private ScreenElement actionOverlay;
	private ScreenElement optionOverlay;
	
	//off-screen elements
	private ScreenElement resume;
	private ScreenElement pause;
	private ScreenElement power;
	private ScreenElement soundOn;
	private ScreenElement soundOff;
	private ScreenElement stats;
	private ScreenElement healthPotion;
	private ScreenElement manaPotion;
	private ScreenElement directpad;
	private ScreenElement action1;
	private ScreenElement action2;
	
	//shields
	private ScreenElement shield1;
	private ScreenElement shield2;
	private ScreenElement shield3;
	private Bitmap shieldback;
	private Bitmap shield1front;
	private Bitmap shield2front;
	private Bitmap shield3front;
	
	//dimensions of movement keys
	private int moveWidth;
	private int moveHeight;
	
	//dimensions of action keys
	private int actionWidth;
	private int actionHeight;
	
	//pressed status of action keys
	private boolean action1Pressed = false;
	private boolean action2Pressed = false;

	public HUD(Context _context, float x_scale, float y_scale, int width, int height) {
		context = _context;
		xScale = x_scale;
		yScale = y_scale;
		screenWidth = width;
		screenHeight = height;
		elements = new ArrayList<ScreenElement>();
		
		//a temporary storage for all future bitmaps before being placed in elements array
		ScreenElement se;
		
		//have to manually scale to original dimensions because bitmapfactory seems to upscale them by an unknown amount
		//loading all permanent HUD elements
		
		//the corner where the player's level is displayed
		se = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.levelcorner), 
				(int)(244 * xScale), (int)(76 * yScale), true), (int)(Math.ceil(1036 * xScale)), 0); //plus for the screens in which in rounds down and there is a border
		elements.add(se);
		
		//corner bar for player health and mana
		se = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.barcover), 
				(int)(640 * xScale), (int)(76 * yScale), true), 0, 0);
		elements.add(se);
		
		//the three shields
		shieldback = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.emptyshield), (int)(68 * xScale), (int)(80 * yScale), true);
		shield1front = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield1), (int)(68 * xScale), (int)(80 * yScale), true);
		shield2front = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield2), (int)(68 * xScale), (int)(80 * yScale), true);
		shield3front = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield3), (int)(68 * xScale), (int)(80 * yScale), true);
		
		shield1 = new ScreenElement(shieldback, (int)(688 * xScale) , 0);
		elements.add(shield1);
		shield2 = new ScreenElement(shieldback, (int)(804 * xScale) , 0);
		elements.add(shield2);
		shield3 = new ScreenElement(shieldback, (int)(920 * xScale) , 0);
		elements.add(shield3);
		
		//movement keys
		if (yScale <= xScale) {
			directpad = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.joystickring), 
					(int)(368 * yScale), (int)(246 * yScale), true), 0, 0);
			directpad.yPos = screenHeight - directpad.image.getHeight();
			moveWidth = (int)(368 * yScale);
			moveHeight = (int)(246 * yScale);
			elements.add(directpad);
		}
		else {
			directpad = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.joystickring), 
					(int)(368 * xScale), (int)(246 * xScale), true), 0, 0);
			directpad.yPos = screenHeight - directpad.image.getHeight();
			moveWidth = (int)(368 * xScale);
			moveHeight = (int)(246 * xScale);
			elements.add(directpad);
		}
		
		//action buttons
		if (yScale <= xScale) {
			action1 = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.redbutton), 
					(int)(136 * yScale), (int)(136 * yScale), true), (int)(1110 * xScale), screenHeight - (int)(136 * yScale));
			elements.add(action1);
			action2 = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bluebutton), 
					(int)(136 * yScale), (int)(136 * yScale), true), (int)(900 * xScale), screenHeight - (int)(136 * yScale));
			actionWidth = action2.image.getWidth();
			actionHeight = action2.image.getHeight();
			elements.add(action2);
		}
		else {
			action1 = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.redbutton), 
					(int)(136 * xScale), (int)(136 * xScale), true), (int)(1110 * xScale), screenHeight - (int)(136 * xScale));
			elements.add(action1);
			action2 = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bluebutton), 
					(int)(136 * xScale), (int)(136 * xScale), true), (int)(900 * xScale), screenHeight - (int)(136 * xScale));
			actionWidth = action2.image.getWidth();
			actionHeight = action2.image.getHeight();
			elements.add(action2);
		}
		
		//elements that move around
		//movement key overlay
		if (yScale <= xScale) {
			movementOverlay = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.directionpressed),
				(int)(122 * yScale), (int)(122 * yScale), true), (int)(-122 * xScale), (int)(-122 * yScale));
		}
		else {
			movementOverlay = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.directionpressed),
					(int)(122 * xScale), (int)(122 * xScale), true), (int)(-122 * xScale), (int)(-122 * yScale));
		}
		elements.add(movementOverlay);
		
		//action key overlay
		if (yScale <= xScale) {
			actionOverlay = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.actionpressed),
				(int)(136 * yScale), (int)(136 * yScale), true), (int)(-136 * xScale), (int)(Math.ceil(-136 * yScale)));
		}
		else {
			actionOverlay = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.actionpressed),
					(int)(136 * xScale), (int)(136 * xScale), true), (int)(-136 * xScale), (int)(Math.ceil(-136 * yScale)));
		}
		elements.add(actionOverlay);
		
		//the pause key
		pause = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pausebutton), 
				(int)(76 * xScale), (int)(76 * yScale), true), (int)(602 * xScale), screenHeight - (int)(76 * yScale));
		elements.add(pause);
		//the resume key
		resume = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.resumebutton), 
				(int)(76 * xScale), (int)(76 * yScale), true), (int)(602 * xScale), screenHeight);
		elements.add(resume);
		
		//stats block
		stats = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.statspanel), 
				(int)(400 * xScale), (int)(400 * yScale), true), (int)(-400 * xScale), (int)(198 * yScale));
		elements.add(stats);
		
		//the power key
		power = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.power),
				(int)(136 * xScale), (int)(136 * yScale), true), screenWidth, (int)(114 * yScale));
		elements.add(power);
		
		//the sound keys
		soundOn = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.soundon),
				(int)(136 * xScale), (int)(136 * yScale), true), screenWidth, (int)(250 * yScale));
		elements.add(soundOn);
		soundOff = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.soundoff),
				(int)(136 * xScale), (int)(136 * yScale), true), screenWidth, (int)(250 * yScale));
		elements.add(soundOff);
		
		//the potion keys
		healthPotion = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.healthpotion),
				(int)(136 * yScale), (int)(136 * yScale), true), screenWidth, (int)(410 * yScale));
		elements.add(healthPotion);
		manaPotion = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.manapotion),
				(int)(136 * yScale), (int)(136 * yScale), true), screenWidth, (int)(546 * yScale));
		elements.add(manaPotion);
		
		//option key overlay
		optionOverlay = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.optionpressed),
				(int)(136 * xScale), (int)(136 * yScale), true), (int)(-136 * xScale), (int)(Math.ceil(-136 * yScale)));
		elements.add(optionOverlay);
	}
	
	public ArrayList<Integer> updateHUD(PointF[] points, int actionType, long elapsedTime){
		
		//a list of outcomes to be interpretted by main class
		ArrayList<Integer> outputs = new ArrayList<Integer>();
		
		//checks for each of the buttons
		boolean dpad = false;
		boolean action = false;
		boolean options = false;
		
		//if the action type is down
		if (actionType == 0) {
			//looping through the downs
			for (int i = 0; i < 4; i++) {
				PointF point = points[i];
				//not paused
				if (!paused) {
					//movement keys
					if (point.x <= (int)(122 * xScale)) {
						//upper-left
						if ((point.y >= screenHeight - moveHeight) && (point.y < screenHeight - (moveHeight/2))) {
							movementOverlay.xPos = 1;
							movementOverlay.yPos = screenHeight - moveHeight + 1;
							dpad = true;
							outputs.add(4);
						}
						//left
						else if ((point.y >= screenHeight - (moveHeight/2)) && (point.y < screenHeight)) {
							movementOverlay.xPos = 1;
							movementOverlay.yPos = screenHeight - moveHeight + 1 + (moveHeight / 2);
							dpad = true;
							outputs.add(5);
						}
					}
					else if ((point.x >= (int)(123 * xScale)) && (point.x < (int)(244 * xScale))) {
						//up
						if ((point.y >= screenHeight - moveHeight) && (point.y < screenHeight - (moveHeight/2))) {
							movementOverlay.xPos = (int)(123 * xScale);
							movementOverlay.yPos = screenHeight - moveHeight + 1;
							dpad = true;
							outputs.add(6);
						}
						//down
						else if ((point.y >= screenHeight - (moveHeight/2)) && (point.y < screenHeight)) {
							movementOverlay.xPos = (int)(123 * xScale);
							movementOverlay.yPos = screenHeight - moveHeight + 1 + (moveHeight / 2);
							dpad = true;
							outputs.add(7);
						}
					}
					else if ((point.x >= (int)(245 * xScale)) && (point.x < (int)(368 * xScale))) {
						//upper-right
						if ((point.y >= screenHeight - moveHeight) && (point.y < screenHeight - (moveHeight/2))) {
							movementOverlay.xPos = (int)(245 * xScale);
							movementOverlay.yPos = screenHeight - moveHeight + 1;
							dpad = true;
							outputs.add(8);
						}
						//right
						else if ((point.y >= screenHeight - (moveHeight/2)) && (point.y < screenHeight)) {
							movementOverlay.xPos = (int)(245 * xScale);
							movementOverlay.yPos = screenHeight - moveHeight + 1 + (moveHeight / 2);
							dpad = true;
							outputs.add(9);
						}
					}
					
					//action keys
					//blue key
					if ((point.x >= (int)(900 * xScale)) && (point.x < (int)(900 * xScale) + actionWidth)) {
						if (point.y >= screenHeight - actionHeight) {
							actionOverlay.xPos = (int)(900 * xScale);
							actionOverlay.yPos = screenHeight - actionHeight;
							action = true;
							if (action2Pressed == false) {
								action2Pressed = true;
								outputs.add(12);
							}
						}
					}
					//red key
					else if ((point.x >= (int)(1110 * xScale)) && (point.x < (int)(1110 * xScale) + actionWidth)) {
						if (point.y >= screenHeight - actionHeight) {
							actionOverlay.xPos = (int)(1110 * xScale);
							actionOverlay.yPos = screenHeight - actionHeight;
							action = true;
							if (action1Pressed == false) {
								action1Pressed = true;
								outputs.add(11);
							}
						}
					}
					else {
						action1Pressed = false;
						action2Pressed = false;
					}
					
					//pause key
					/*if ((point.x >= (int)(445 * xScale)) && (point.x < (int)(835 * xScale))) {
						if (point.y >= screenHeight - (int)(76 * yScale)) {
							pause = true;
						}
					}*/
				}
				
				//paused
				else {
					//resuming
					/*if ((point.x >= (int)(445 * xScale)) && (point.x < (int)(835 * xScale))) {
						if (point.y >= screenHeight - (int)(76 * yScale)) {
							resume = true;
						}
					}*/
					
					//keys on the right of pause menu
					if ((point.x >= screenWidth - (int)(136 * xScale))) {
						//handling power key
						if ((point.y >= (int)(114 * yScale)) && point.y < (int)(250 * yScale)) {
							//powerPressed.xPos = screenWidth - (int)(136 * xScale);
							optionOverlay.xPos = screenWidth - (int)(136 * xScale);
							optionOverlay.yPos = (int)(114 * yScale);
							options = true;
						}
						
						//handling sound key
						else if ((point.y >= (int)(250 * yScale)) && point.y < (int)(386 * yScale)) {
							optionOverlay.xPos = screenWidth - (int)(136 * xScale);
							optionOverlay.yPos = (int)(250 * yScale);
							options = true;
						}
						
						//handling health potion
						else if ((point.y >= (int)(410 * yScale)) && (point.y < (int)(546 * yScale))) {
							//healthPotionPressed.xPos = screenWidth - (int)(136 * yScale);
							optionOverlay.xPos = screenWidth - (int)(136 * xScale);
							optionOverlay.yPos = (int)(410 * yScale);
							options = true;
						}
						//handling mana potion
						else if ((point.y >= (int)(546 * yScale)) && (point.y < (int)(682 * yScale))) {
							//manaPotionPressed.xPos = screenWidth - (int)(136 * yScale);
							optionOverlay.xPos = screenWidth - (int)(136 * xScale);
							optionOverlay.yPos = (int)(546 * yScale);
							options = true;
						}
					}
				}
			}
			//removing unselected items
			if (dpad == false) {
				movementOverlay.xPos = (int)(-122 * xScale);
				movementOverlay.yPos = (int)(-122 * yScale);
				outputs.add(10);
			}
			if (action == false) {
				actionOverlay.xPos = (int)(-136 *xScale);
				actionOverlay.yPos = (int)(-136 * yScale);
			}
			if (options == false) {
				optionOverlay.xPos = screenWidth;
			}
		}
		
		//if the action type is up
		else if (actionType == 1) {
			//looping through the ups
			for (int i = 0; i < 4; i++) {
				PointF point = points[i];
				//not paused
				if (!paused) {
					//if pause was released
					if ((point.x >= (int)(600 * xScale)) && (point.x < (int)(680 * xScale))) {
						if (point.y >= screenHeight - (int)(76 * yScale)) {
							pause();
						}
					}
					//if attack 1 was released - should set outcome on press and reset boolean on release
					else if ((point.x >= (int)(1110 * xScale)) && (point.x < (int)(1110 * xScale) + actionWidth)) {
						if (point.y >= screenHeight - actionHeight) {
							action1Pressed = false;
						}
					}
					//if attack 2 was released
					if ((point.x >= (int)(900 * xScale)) && (point.x < (int)(900 * xScale) + actionWidth)) {
						if (point.y >= screenHeight - actionHeight) {
							action2Pressed = false;
						}
					}
				}
				//paused
				else {				
					//resume pressed
					if ((point.x >= (int)(600 * xScale)) && (point.x < (int)(680 * xScale))) {
						if (point.y >= screenHeight - (int)(76 * yScale)) {
							resume();
						}
					}
					//use health potion
					if (point.x >= screenWidth - (int)(136 * xScale)) {
						if ((point.y >= (int)(410 * yScale)) && (point.y < (int)(546 * yScale))) {
							outputs.add(2);
						}
					}
					
					//use mana potion
					if (point.x >= screenWidth - (int)(136 * xScale)) {
						if ((point.y >= (int)(546 * yScale)) && (point.y < (int)(682 * yScale))) {
							outputs.add(3);
						}
					}
					
					//toggle sound - sound released
					if (point.x >= screenWidth - (int)(136 * xScale)) {
						if ((point.y >= (int)(250 * yScale)) && point.y < (int)(386 * yScale)) {
							if (sound) {
								//soundOnPressed.xPos = screenWidth;
								soundOn.xPos = screenWidth;
								soundOff.xPos = screenWidth - (int)(136 * xScale);
								sound = false;
							}
							else {
								//soundOffPressed.xPos = screenWidth;
								soundOff.xPos = screenWidth;
								soundOn.xPos = screenWidth - (int)(136 * xScale);
								sound = true;
							}
							outputs.add(1);
						}
					}
					//go back to title screen - power released
					if ((point.x >= screenWidth - (int)(136 * xScale))) {
						if ((point.y >= (int)(114 * yScale)) && point.y < (int)(250 * yScale)) {
							paused = false;
							resetPause();
							outputs.add(0);
						}
					}
				}
			}
		}
		
		//updating the placement of moving HUD pieces
		int elapsed = (int)(elapsedTime / 1000000);
		if (inMotion) {
			if (paused) {
				//moving pause menu items in
				stats.xPos += elapsed * 3;
				power.xPos -= elapsed;
				if (sound)
					soundOn.xPos -= elapsed;
				else
					soundOff.xPos -= elapsed;
				healthPotion.xPos -= elapsed;
				manaPotion.xPos -= elapsed;
				
				//correcting overcompensation
				if (stats.xPos > 0)
					stats.xPos = 0;
				if (power.xPos < screenWidth - (int)(136 * xScale))
					power.xPos = screenWidth - (int)(136 * xScale);
				if (sound) {
					if (soundOn.xPos < screenWidth - (int)(136 * xScale))
						soundOn.xPos = screenWidth - (int)(136 * xScale);
				}
				else {
					if (soundOff.xPos < screenWidth - (int)(136 * xScale))
						soundOff.xPos = screenWidth - (int)(136 * xScale);
				}
				if (healthPotion.xPos < screenWidth - (int)(136 * xScale))
					healthPotion.xPos = screenWidth - (int)(136 * xScale);
				if (manaPotion.xPos < screenWidth - (int)(136 * xScale)) {
					manaPotion.xPos = screenWidth - (int)(136 * xScale);
				}
				
				//moving the game buttons out
				action1.yPos += elapsed;
				action2.yPos += elapsed;
				directpad.yPos += elapsed;
				
				//correcting over compensation
				if (action1.yPos > screenHeight)
					action1.yPos = screenHeight;
				if (action2.yPos > screenHeight)
					action2.yPos = screenHeight;
				if (directpad.yPos > screenHeight) {
					directpad.yPos = screenHeight;
					drawStats = true;
					inMotion = false;
				}
			}
			else {
				//move the pause menu stuff out
				stats.xPos -= elapsed * 3;
				power.xPos += elapsed;
				if (sound)
					soundOn.xPos += elapsed;
				else
					soundOff.xPos += elapsed;
				healthPotion.xPos += elapsed;
				manaPotion.xPos += elapsed;
				
				//correcting over compensation
				if (stats.xPos < (int)(-400 * xScale))
					stats.xPos = (int)(-400 * xScale);
				if (power.xPos > screenWidth)
					power.xPos = screenWidth;
				if (sound) {
					if (soundOn.xPos > screenWidth)
						soundOn.xPos = screenWidth;
				}
				else {
					if (soundOff.xPos > screenWidth)
						soundOff.xPos = screenWidth;
				}
				if (healthPotion.xPos > screenWidth)
					healthPotion.xPos = screenWidth;
				if (manaPotion.xPos > screenWidth) {
					manaPotion.xPos = screenWidth;
				}
				
				//moving the game buttons in
				action1.yPos -= elapsed;
				action2.yPos -= elapsed;
				directpad.yPos -= elapsed;
				
				//correcting overcompensation
				if (action1.yPos < screenHeight - actionHeight)
					action1.yPos = screenHeight - actionHeight;
				if (action2.yPos < screenHeight - actionHeight)
					action2.yPos = screenHeight - actionHeight;
				if (directpad.yPos < screenHeight - moveHeight) {
					directpad.yPos = screenHeight - moveHeight;
					inMotion = false;
				}
			}
		}
		return outputs;
	}
	
	//drawing the hud
	public void draw(Canvas canvas) {
		for (ScreenElement se : elements)
			canvas.drawBitmap(se.image, se.xPos, se.yPos, null);
	}
	
	//pause as a method such that keys outside of this class can make use of it
	public void pause() {
		//move the resume key into place
		pause.yPos = screenHeight;
		resume.yPos = screenHeight - (int)(Math.floor(76 * yScale));
		//setting the state to paused
		paused = true;
		inMotion = true;
	}
	
	//resume as a method such that keys outside of this class can make use of it
	public void resume() {
		//remove the resume button
		resume.yPos = screenHeight;
		pause.yPos = screenHeight - (int)(Math.floor(76 * yScale));
		//moving the pressed state of pause out - had a bug
		//setting the state to un-paused
		paused = false;
		drawStats = false;
		inMotion = true;
	}
	
	//reseting all pause menu bitmaps off-screen when the power key is pressed - else they appear upon resume
	private void resetPause() {
		//remove all the paused state presses
		optionOverlay.xPos = screenWidth;
		//remove the resume button
		resume.yPos = screenHeight;
		//remove the stats panel
		stats.xPos = (int)(-400 * xScale);
		//move the power key out
		power.xPos = screenWidth;
		//move the sound key out
		soundOn.xPos = screenWidth;
		soundOff.xPos = screenWidth;
		//move the health potion out
		healthPotion.xPos = screenWidth;
		//move the mana potion out
		manaPotion.xPos = screenWidth;
		//setting the state to un-paused
		paused = false;
	}
}
