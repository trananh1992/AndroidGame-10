package com.games.platformerrpg;

import java.util.ArrayList;

import player.Player;

import saves.SaveHandler;
import screens.GameScreen;
import screens.HUD;
import screens.ScreenElement;
import screens.TitleScreen;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.app.Activity;


//list of things to do next
// - possibly add character to title screen or make glowy things initially take the shape of a sword or something and then move when touched - do after gameplay
// - fix jump - have gravity and apply it at time intervals; have a while loop within the update method and change dY and yPos every increment
// - start adding instructions
// - platform objects 
// - make background scroll
// - make player object climb
// - give player a weapon 
// - animate weapon to match player movement 
// - add first enemy 
// - animate enemy 
// - add NPCs 
// - add NPC speech bubbles 
// - make bubble scale and point to correct place 
// - make buildings and other stationary objects 
// - make other enemies 
// - make level editor if it doesn't already exist 
// - create other weapons and player abilities 
// - plan and develop a complete level 
// - plan and develop maps/levels and story 
// - create bosses on an as-needed basis, as well as any other objects 

// - for live loading screen: move loading into separate thread (most of the screens and such are global anyway), and set a loading state which is then updated (spin or dots, etc)

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	
	//variables used in the game
	private Context myContext;
	private SurfaceHolder mySurfaceHolder;
	
	//dimenions
	private int screenWidth = 1280;
	private int screenHeight = 720;
	
	//loop terminator
	private boolean running = false;

	//game thread
	private GameThread thread; 
	
	//player object
	private Player player;
	
	//scale of screen to image size (1280 x 720)
	private float xScale = 1;
	private float yScale = 1;
	
	//various screens - separate objects for easier control over their memory
	private int screenID = 0;
	private TitleScreen title;
	private HUD hud;
	private GameScreen game; // - game screen should contain map
	
	//for drawing text
	private Paint whitePaint;
	private Paint rightPaint;
	private Paint centerPaint;
	private Paint healthPaint;
	private Paint manaPaint;
	private Paint levelPaint;
	private Paint grayPaint;
	
	//sound
	boolean soundOn;
	
	//keeping track of time
	private long lastFrameTime;
	private long currentFrameTime;
	private long elapsedTime;
	
	//for testing
	String hudoutput = "";
	int direction;
	int pointer1x = 0;
	int pointer1y = 0;
	int pointer2x = 0;
	int pointer2y = 0;
	int pointer3x = 0;
	int pointer3y = 0;
	int pointer4x = 0;
	int pointer4y = 0;
	
	long secondCount = 0;
	int frames = 0;
	int fps = 0;
	
	//filehandler for saves
	public SaveHandler file;
	
	//keeping track of the multiple points touched
	private PointF[] downList = new PointF[4];
	private PointF[] upList = new PointF[4];
	private int pointers;
	
	//for loading
	private boolean loading = false;
	private int loadingCode = 0; //0 - resume game selected, 1 - new game, 2 - back to title, 3 - load next level
	private int levelID = 0;
	
	//game view constructor
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		thread = new GameThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {}
		});
		setFocusable(true);

		setFocusable(true);
	}
	
	//the game thread - logic, drawing, animation, etc
	class GameThread extends Thread {
		
		//constructor of game thread
		public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mySurfaceHolder = surfaceHolder;
			myContext = context;
			
			//getting the display dimensions
			WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
			xScale = screenWidth/1280.0f;
			yScale = screenHeight/720.0f;
			
			//file handling
			file = new SaveHandler(myContext, "gamesave");
			file.read();
			
			//remembering sound setting
			soundOn = file.sound;
			
			//setting up the first screen
			screenID = 0;
			title = new TitleScreen(myContext, xScale, yScale, screenWidth, screenHeight, soundOn);
			hud = null;
			player = null;
			game = null;

			//setting up the paints
			whitePaint = new Paint();
			whitePaint.setAntiAlias(true);
			whitePaint.setColor(Color.WHITE);
			whitePaint.setTextAlign(Paint.Align.LEFT);
			whitePaint.setTextSize((int)(50 * xScale));
			
			rightPaint = new Paint();
			rightPaint.setAntiAlias(true);
			rightPaint.setColor(Color.WHITE);
			rightPaint.setTextAlign(Paint.Align.RIGHT);
			rightPaint.setTextSize((int)(50 * xScale));
			
			centerPaint = new Paint();
			centerPaint.setAntiAlias(true);
			centerPaint.setColor(Color.WHITE);
			centerPaint.setTextAlign(Paint.Align.CENTER);
			centerPaint.setTextSize((int)(25 * xScale));
			
			healthPaint = new Paint();
			healthPaint.setAntiAlias(true);
			healthPaint.setColor(Color.RED);
			healthPaint.setStyle(Paint.Style.FILL);
			
			manaPaint = new Paint();
			manaPaint.setAntiAlias(true);
			manaPaint.setColor(Color.BLUE);
			manaPaint.setStyle(Paint.Style.FILL);
			
			levelPaint = new Paint();
			levelPaint.setAntiAlias(true);
			levelPaint.setColor(Color.GREEN);
			levelPaint.setStyle(Paint.Style.FILL);
			
			grayPaint = new Paint();
			grayPaint.setAntiAlias(true);
			grayPaint.setColor(Color.DKGRAY);
			grayPaint.setStyle(Paint.Style.FILL);
			
			//setting up the pointer lists
			for (int i = 0; i < 4; i++) {
				downList[i] = new PointF(0, 0);
				upList[i] = new PointF(0, 0);
			}
			
			//initiating time
			lastFrameTime = System.nanoTime();
			currentFrameTime = lastFrameTime;
		}
		
		@Override
		public void run() {
			while(running) {
				Canvas c = null;
				try {
					c = mySurfaceHolder.lockCanvas(null);
					synchronized(mySurfaceHolder) {
						update();
						draw(c);
					}
				} finally {
					if (c != null) {
						mySurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		//drawing the game
		private void draw(Canvas canvas) {
			try {
				//"clearing the screen"
				canvas.drawColor(Color.BLACK);
				
				if (!loading) {
					//drawing the title screen
					if (screenID == 0)
						title.draw(canvas);
					
					//drawing the game screen
					else if (screenID == 1){
							
						//drawing the screen
						game.draw(canvas);
							
						//drawing the player
						player.draw(canvas);
								
						//drawing the player's health, mana and XP bars before the hud elements
						canvas.drawRect((int)(9 * xScale), (int)(5 * yScale), (int)(606 * xScale), (int)(22 * yScale), grayPaint);
						canvas.drawRect((int)(9 * xScale), (int)(25 * yScale), (int)(586 * xScale), (int)(42 * yScale), grayPaint);
						canvas.drawRect((int)(9 * xScale), (int)(45 * yScale), (int)(566 * xScale), (int)(62 * yScale), grayPaint);
						canvas.drawRect((int)(11 * xScale), (int)(5 * yScale), (int)(605 * xScale * player.health / (float)(player.totalHealth)), (int)(22 * yScale), healthPaint);
						canvas.drawRect((int)(11 * xScale), (int)(25 * yScale), (int)(686 * xScale * player.mana / (float)(player.totalMana)), (int)(42 * yScale), manaPaint);
						canvas.drawRect((int)(11 * xScale), (int)(45 * yScale), (int)(566 * xScale * player.xp / (float)(player.totalXP)), (int)(62 * yScale), levelPaint);
								
						//drawing the HUD
						hud.draw(canvas);
								
						//drawing HUD text - should fined a way to integrate with screen.draw
						canvas.drawText("lvl ", (int)(1120 * xScale), (int)((5 + whitePaint.getTextSize()) * yScale), whitePaint);
						canvas.drawText(Integer.toString(player.level), (int)(1270 * xScale), (int)((5 + whitePaint.getTextSize()) * yScale), rightPaint);
						if (((HUD)(hud)).paused && ((HUD)(hud)).drawStats) {
							drawText(canvas, 0, "HP", player.health, "/", player.totalHealth);
							drawText(canvas, 1, "MP", player.mana, "/", player.totalMana);
							drawText(canvas, 2, "XP", player.xp, "/", player.totalXP);
							drawText(canvas, 3, "DEF", -1, "", player.defense);
							drawText(canvas, 4, "ATK 1", -1, "", player.dmg1);
							drawText(canvas, 5, "ATK 2", -1, "", player.dmg2);
							canvas.drawText("" + player.hPotions, (int)(1180 * xScale), (int)(460 * yScale), centerPaint);
							canvas.drawText("" + player.mPotions, (int)(1180 * xScale), (int)(596 * yScale), centerPaint);
						}
					}
				}
				else
					canvas.drawText("LOADING...", (int)(100 * xScale), screenHeight - (int)(100 * yScale), whitePaint);
				//canvas.drawText("" + fps, 10, 50, whitePaint);
			} catch (Exception e) {}
		}
		
		//method to draw stats
		void drawText(Canvas canvas, int number, String title, int amount, String seperator, int total) {
			canvas.drawText(title, (int)(25 * xScale), (int)((220 + (60 * number) + whitePaint.getTextSize()) * yScale), whitePaint);
			if (amount != -1)
				canvas.drawText(Integer.toString(amount), (int)(210 * xScale), (int)((220 + (60 * number) + whitePaint.getTextSize()) * yScale), rightPaint);
			if (!seperator.equals(""))
				canvas.drawText(seperator, (int)(220 * xScale), (int)((220 + (60 * number) + whitePaint.getTextSize()) * yScale), whitePaint);
			canvas.drawText(Integer.toString(total), (int)(330 * xScale), (int)((220 + (60 * number) + whitePaint.getTextSize()) * yScale), rightPaint);
		}
		
		//updating everything!!
		void update() {
			
			//updating time elapsed and counting frames
			lastFrameTime = currentFrameTime;
			currentFrameTime = System.nanoTime();
			elapsedTime = currentFrameTime - lastFrameTime;
			
			//fps counter
			secondCount += elapsedTime;
			frames++;
			if (secondCount/1000000000 >= 1) {
				secondCount = 0;
				fps = frames;
				frames = 0;
			}
			
			//if loading is required
			if (loading) {
				if (loadingCode == 0) {
					//resuming a game so load saves
					screenID = 1;
					title = null;
					hud = new HUD(myContext, xScale, yScale, screenWidth, screenHeight);
					hud.sound = file.sound;
					player = new Player(myContext, xScale, yScale, screenWidth, screenHeight);
					game = new GameScreen(myContext, xScale, yScale, screenWidth, screenHeight);
					//while (!game.loaded) {}
					loading = false;
				}
				else if (loadingCode == 1) {
					//new game so wipe saves and load default start
					screenID = 1;
					title = null;
					hud = new HUD(myContext, xScale, yScale, screenWidth, screenHeight);
					hud.sound = file.sound;
					player = new Player(myContext, xScale, yScale, screenWidth, screenHeight);
					game = new GameScreen(myContext, xScale, yScale, screenWidth, screenHeight);
					//while (!game.loaded) {}
					loading = false;
				}
				else if (loadingCode == 2) {
					//should do the saving in here
					screenID = 0;
					game = null;
					hud = null;
					player = null;
					title = new TitleScreen(myContext, xScale, yScale, screenWidth, screenHeight, soundOn);
					loading = false;
				}
			}
			else {
			//gathering and acting upon input
			if (screenID == 0) {
				for (int i = 0; i < 4; i++) {
					if (screenID == 1) //screen was changed by a pointer that wasn't in last position
						break;
					handleScreenOutput(title.update(downList[i], 0, elapsedTime));
				}
				for (int i = 0; i < 4; i++) {
					if (screenID == 1) //screen was changed by a pointer that wasn't in last position
						break;
					handleScreenOutput(title.update(upList[i], 1, elapsedTime));
				}
			}
			else if (screenID == 1){
				handleHudOutput(hud.updateHUD(downList, 0, elapsedTime));
				handleHudOutput(hud.updateHUD(upList, 1, elapsedTime));
			}
			
			//in game
			if (screenID == 1) {
				if (hud.paused == false) {
					//updating the player
					if (player != null) {
						player.update(elapsedTime);
						if (!player.isJumping){ 
							if (direction == 4) { //up-left
								player.direction = 0;
								player.jumpSide();
							}
							else if (direction == 5) { //left
								player.direction = 0;
								player.walk();
							}
							else if (direction == 6) { //up
								player.ladderDir = 1;
								player.jump();
							}
							else if (direction == 7) { //down
								player.ladderDir = 0;
								player.climb();
							}
							else if (direction == 8) { //up-right
								player.direction = 1;
								player.jumpSide();
							}
							else if (direction == 9) { //right
								player.direction = 1;
								player.walk();
							}
							else if (direction == 10) {
								player.stop(); //stand
							}
						}
					}
					
					//updating the screen
					game.update(elapsedTime);
				}
			}
			}
			
			//clearing the upList as the actions have now been done
			for (int i = 0; i < 4; i++)
				upList[i] = new PointF(0, 0);
					
		}
		
		//handling input
		boolean doTouchEvent(MotionEvent event) {
			synchronized (mySurfaceHolder) {
				//counting the number of pointers - for testing
				pointers = event.getPointerCount();
				
				//variables to be used with the up/cancel action
				int pointerIndex = event.getActionIndex();
				int pointerId = event.getPointerId(pointerIndex);
				int maskedAction = event.getActionMasked();
				
				//switch on masked action				
				switch(maskedAction) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_MOVE:
					for (int i = 0; i < pointers; i++) {//for some stupid reason android doesn't recognize the movement of any pointer but the first, so you have to iterate through all of them anyway
						int id = event.getPointerId(i);
						downList[id] = new PointF((int)(event.getX(i)), (int)(event.getY(i)));
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					upList[pointerId] = new PointF((int)event.getX(pointerIndex), (int)event.getY(pointerIndex));
					downList[pointerId] = new PointF(0, 0);
					break;
				}
			}
			return true;
		}
		
		//transitioning back to the title screen from the game screen
		public void goBack() {
			if (screenID == 0) {
				title.setBase();
			}
			else if (screenID == 1) {
				if (hud.paused)
					hud.resume();
				else if (!hud.paused)
					hud.pause();
			}
		}
		
		//going back to title screen from game screen
		public void returnToTitle() {
			loading = true;
			loadingCode = 2;
		}
		
		//setting the size of the screen and background image (called first time and shouldn't be called again after)
		public void setSurfaceSize(int width, int height) {
			synchronized(mySurfaceHolder) {
				screenWidth = width;
				screenHeight = height;
				xScale = (float)width/1280; //width of HUD
				yScale = (float)height/720; //height of HUD
			}
		}
		
		//setting the status of the running boolean
		public void setRunning(boolean b) {
			running = b;
		}
	}
	
	//handling the non-screen specific outcomes of the screen input
	void handleScreenOutput(int output) {
				
		//title screen output
		if (screenID == 0) {
			soundOn = title.sound;
			file.sound = title.sound;
			switch(output) {
			case 0:
				//resume a current game - load saves
				loading = true;
				loadingCode = 0;
				break;
			case 1:
				//start a new game - flush saves
				loading = true;
				loadingCode = 1;
				break;
			case 3:
				//save and close - save should actually be on game exit not app exit
				file.write();
				((Activity)(myContext)).finish();
				break;
			}
		}
	}
			
	//handling the HUD output
	void handleHudOutput(ArrayList<Integer> outputs) {
		//0 - power, 1 - sound, 2 - health, 3 - mana, 4 - top left, 5 - left, 6 - up, 7 - down, 8 - top right, 9 - right, 10 - no movement, 11 - attack 1, 12 - attack 2
		hudoutput = "";
		for (int output : outputs) {
			switch (output) {
			case 0:
				thread.returnToTitle();
				hudoutput += "0, ";
				break;
			case 1:
				if (soundOn) {
					soundOn = false;
					file.sound = false;
				}
				else {
					soundOn = true;
					file.sound = true;
				}
				hudoutput += "1, ";
				break;
			case 2:
				hudoutput += "2, ";
				break;
			case 3:
				hudoutput += "3, ";
				break;
			case 4:
				hudoutput += "4, ";
				direction = 4;
				break;
			case 5:
				hudoutput += "5, ";
				direction = 5;
				break;
			case 6:
				hudoutput += "6, ";
				direction = 6;
				break;
			case 7:
				hudoutput += "7, ";
				direction = 7;
				break;
			case 8:
				hudoutput += "8, ";
				direction = 8;
				break;
			case 9:
				hudoutput += "9, ";
				direction = 9;
				break;
			case 10:
				hudoutput += "10, ";
				direction = 10;
				break;
			case 11:
				hudoutput += "11, ";
				break;
			case 12:
				hudoutput += "12, ";
				break;
			}
		}
	}
	
	//returning the current screen ID to the activity class
	public int getScreenID() {
		return screenID;
	}
	
	//returning the state of the current screen
	public int getScreenState() {
		if (screenID == 0)
			return title.state;
		else
			return -1;
	}
	
	//telling the thread to go back a screen
	public void goBack() {
		thread.goBack();
	}
	
	//passing the input to the thread
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.doTouchEvent(event);
	}
	
	//changing the screen size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
		
	}

	//setting up the screen
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		if(thread.getState() == Thread.State.NEW) {
			thread.start();
			//loading.start();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
		
	}
}
