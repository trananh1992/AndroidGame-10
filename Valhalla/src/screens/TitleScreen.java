package screens;

import java.util.ArrayList;
import java.util.Random;

import com.games.platformerrpg.R;
import com.games.platformerrpg.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

//holds all the details for the title screen
public class TitleScreen{
	
	public boolean sound;
	public ArrayList<ScreenElement> elements;
	protected Context context;
	protected float xScale;
	protected float yScale;
	protected int screenWidth = 1280;
	protected int screenHeight = 720;

	public int selectedItem = 0;
	private ScreenElement selection;
	private ScreenElement menu;
	private ScreenElement soundOn;
	private ScreenElement soundOff;
	private ScreenElement credits;
	private ScreenElement title;
	private ScreenElement accept;
	public int state = 0; //0 = title, 1 = new game affirmation, 2 = instructions
	
	private Bitmap glow1;
	private Bitmap glow2;
	private Bitmap glow3;
	private Dot[] size1;
	private Dot[] size2;
	private Dot[] size3;
	
	public float elapsedTurn;
	public float elapsedMove;
	
	Random rand;

	//constructor
	public TitleScreen (Context _context, float x_scale, float y_scale, int width, int height, boolean soundstate) {
		context = _context;
		xScale = x_scale;
		yScale = y_scale;
		screenWidth = width;
		screenHeight = height;
		elements = new ArrayList<ScreenElement>();

		sound = soundstate;
		
		//loading the "item selected" image
		selection = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.titleselected),
				(int)(420 * xScale), (int)(100 * yScale), true), (int)(31 * xScale), (int)(148 * yScale));
		elements.add(selection);

		//loading and storing menu options
		menu = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.titleoptions), 
				(int)(346 * xScale), (int)(452 * yScale), true), (int)(78 * xScale), (int)(148 * yScale));
		elements.add(menu);
		
		//loading the sound icon
		soundOn = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.soundontitle),
				(int)(74 * xScale), (int)(69 * yScale), true), (int)(3 * xScale), (int)(0 * yScale));
		//loading the sound icons
		soundOff = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.soundofftitle),
				(int)(74 * xScale), (int)(69 * yScale), true), (int)(3 * xScale), (int)(0 * yScale));
		if (sound)
			elements.add(soundOn);
		else
			elements.add(soundOff);
		
		//loading the credits icon
		credits = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.credits),
				(int)(74 * xScale), (int)(69 * yScale), true), (int)(3 * xScale), screenHeight - (int)(76 * yScale));
		elements.add(credits);
		
		//loading the title
		title = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.title),
				(int)(511 * xScale), (int)(225 * yScale), true), (int)(595 * xScale), (int)(80 * yScale));
		elements.add(title);
		
		//loading the accept button
		accept = new ScreenElement(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.accept),
				(int)(350 * xScale), (int)(101 * yScale), true), (int)(901 * xScale), (int)(587 * yScale));
		elements.add(accept);
		
		rand = new Random(System.currentTimeMillis());
		
		//dot bitmaps
		glow1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(150 * xScale), (int)(150 * yScale), true);
		glow2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(80 * xScale), (int)(80 * yScale), true);
		glow3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(40 * xScale), (int)(40 * yScale), true);
		
		//initializing dot arrays
		size1 = new Dot[5];
		size2 = new Dot[5];
		size3 = new Dot[5];
		
		//initializing dots
		for (int i = 0; i < 5; i++) {
			size1[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() + 0.2), screenWidth, screenHeight, xScale, yScale);
			size2[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() + 0.2), screenWidth, screenHeight, xScale, yScale);
			size3[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() + 0.2), screenWidth, screenHeight, xScale, yScale);
		}
	}
	
	public int update(PointF point, int actionType, long elapsedTime){		
		//main state takes ups and downs, options and credits states take only ups
		//the outcome value
		int outcome = -1;
		
		if (point.x != 0 && point.y != 0) {
		
			//looping through the down list and selecting the last selected menu option
			if (actionType == 0) {
				if (state == 0) {
					if (point.x >= 0 && point.x <= 540 * xScale) {
						if (point.y > 76) {
							if (point.y > 76 && point.y <= 256 * yScale) {
								selectedItem = 0;
								selection.yPos = (int)(148 * yScale);
							}
							else if (point.y <= 364 * yScale) {
								selectedItem = 1;
								selection.yPos = (int)(267 * yScale);
							}
							else if (point.y <= 482 * yScale) {
								selectedItem = 2;
								selection.yPos = (int)(380 * yScale);
							}
							else if (point.y <= screenHeight - (int)(76 * yScale)){
								selectedItem = 3;
								selection.yPos = (int)(497 * yScale);
							}
						}
					}
					else if (point.x >= 990 * xScale) {
						if (point.y >= 590 * yScale) {
							outcome = selectedItem;
						}
					}
				}
			}
			
			//checking the up position
			else if (actionType == 1) {
				if (point != null) {
				//main state
					if (state == 0) {
						if (point.x <= 100 * xScale) {
							//sound
							if (point.y <= 75 * yScale) {
								if (sound) {
									sound = false;
									elements.set(2, soundOff);
								}
								else {
									sound = true;
									elements.set(2, soundOn);
								}
							}
							//credits
							else if (point.y >= screenHeight - (int)(76 * yScale)) {
								
							}
						}
						if (point.x >= 990 * xScale) {
							if (point.y >= 590 * yScale) {
								//Instructions - if selected then start
							}
						}
					}
					//instructions state
					/*else if (state == 2) {
						//move on to the next state when something is pressed
					}*/
				}
			}
		}
		
		//updating elapsed time
		elapsedTurn += elapsedTime / 1000000;
		elapsedMove += elapsedTime / 1000000;
				
		//turning dots every half second
		if (elapsedTurn >= 500) {
			elapsedTurn -= 500;
			for (int i = 0; i < 5; i++) {
				size1[i].turn(rand.nextInt(12));
				size2[i].turn(rand.nextInt(12));
				size3[i].turn(rand.nextInt(12));
			}
		}
				
		//moving dots
		if (elapsedMove >= 100) {
			elapsedMove -= 100;
			for (int i = 0; i < 5; i++) {
				size1[i].move();
				size2[i].move();
				size3[i].move();
			}
		}
		
		return outcome;
	}
	
	//drawing the screen
	public void draw(Canvas canvas) {
				
		//drawing the game elements
		for (ScreenElement se : elements)
			canvas.drawBitmap(se.image, se.xPos, se.yPos, null);
		
		//drawing the dots
		for (int i = 0; i < 5; i++) {
			canvas.drawBitmap(glow1, size1[i].xPos, size1[i].yPos, null);
			canvas.drawBitmap(glow2, size2[i].xPos, size1[i].yPos, null);
			canvas.drawBitmap(glow3, size3[i].xPos, size1[i].yPos, null);
		}
	}
	
	//returning to base state
	public void setBase() {
		if (state != 0)
			elements.remove(elements.size()-1);
		state = 0;
	}
}
