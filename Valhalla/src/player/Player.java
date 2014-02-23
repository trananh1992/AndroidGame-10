package player;

import com.games.platformerrpg.R;
import com.games.platformerrpg.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Player {
	
	//tracking frame for animation
	public int frame = 0;
	
	//tracking direction
	public int direction = 0; //0 - left, 1 - right
	public int ladderDir = 0; //0 - down, 1 - up
	public boolean falling = false;
	
	//amount to move for each frame - varies with elapsed time
	int moveDist = 0;
	
	//tracking states of motion
	public boolean walking = false;
	public boolean isJumping = false;
	public boolean jumpingSideways = false;
	public float defaultDY;
	public float dY;
	
	public float topDY;
	public float maxDY;
	
	//tracking frames
	private int frameID = 0;
	private int walkingFrame = 0;
	private int frameElapsed = 0;
	
	//player variables
	public int health = 84;
	public int totalHealth = 100;
	public int mana = 65;
	public int totalMana = 100;
	public int xp = 15;
	public int totalXP = 100;
	public int level = 14;
	public int hPotions = 7;
	public int mPotions = 112;
	public int defense = 18;
	public int dmg1 = 27;
	public int dmg2 = 46;
	
	//position - temporary
	public int xPos;
	public int yPos;
	public float xOffset;
	public float yOffset;
	public int floor; //base floor - must make map coords not screen coords
	
	//the frames
	//keeping frames in arrays
	private Bitmap[] left = new Bitmap[6]; //will have to grow as sprites do
	private Bitmap[] right = new Bitmap[6];
	// 0 - stand, 1 - jump, 2 - walk0, 3 - walk1, 4 - walk2, 5 - walk3
	//left
/*	public Bitmap standleft;
	public Bitmap jumpleft;
	public Bitmap walk0left;
	public Bitmap walk1left;
	public Bitmap walk2left;
	public Bitmap walk3left;
	public Bitmap attack01left;
	public Bitmap attack02left;
	//right
	public Bitmap standright;
	public Bitmap jumpright;
	public Bitmap walk0right;
	public Bitmap walk1right;
	public Bitmap walk2right;
	public Bitmap walk3right;
	public Bitmap attack01right;
	public Bitmap attack02right;*/
	//both
	public Bitmap climb01;
	public Bitmap climb02;
	
	//scales and dimensions
	float xScale;
	float yScale;
	int screenWidth;
	int screenHeight;
	
	public Player(Context context, float x_scale, float y_scale, int screen_width, int screen_height, int spawnX, int spawnY) {
		//setting the scales and dimensions
		xScale = x_scale;
		yScale = y_scale;
		screenWidth = screen_width;
		screenHeight = screen_height;
		
		//setting jumping values
		defaultDY = 8 * yScale;
		dY = defaultDY;
		
		//temporary
		xPos = spawnX;
		yPos = spawnY - 30;
		floor = spawnY + (int)(140 * yScale); //should be level dependent
		
		//load all the bitmaps
		left[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.standleft), (int)(150 * xScale), (int)(150 * yScale), true);
		right[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.standright), (int)(150 * xScale), (int)(150 * yScale), true);
		left[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jumpleft), (int)(150 * xScale), (int)(150 * yScale), true);
		right[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.jumpright), (int)(150 * xScale), (int)(150 * yScale), true);
		left[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk0left), (int)(150 * xScale), (int)(150 * yScale), true);
		left[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk1left), (int)(150 * xScale), (int)(150 * yScale), true);
		left[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk2left), (int)(150 * xScale), (int)(150 * yScale), true);
		left[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk3left), (int)(150 * xScale), (int)(150 * yScale), true);
		right[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk0right), (int)(150 * xScale), (int)(150 * yScale), true);
		right[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk1right), (int)(150 * xScale), (int)(150 * yScale), true);
		right[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk2right), (int)(150 * xScale), (int)(150 * yScale), true);
		right[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.walk3right), (int)(150 * xScale), (int)(150 * yScale), true);
		//load all others when they exist
	}
	
	public void draw(Canvas canvas) {
		if (isJumping || falling) {
			if (direction == 0)
				canvas.drawBitmap(left[1], xPos, yPos, null);
			else
				canvas.drawBitmap(right[1], xPos, yPos, null);
		}
		else if (walking) {
			if (direction == 0)
				canvas.drawBitmap(left[2 + walkingFrame], xPos, yPos, null);
			else
				canvas.drawBitmap(right[2 + walkingFrame], xPos, yPos, null);
		}
		else {
			if (direction == 0)
				canvas.drawBitmap(left[0], xPos, yPos, null);
			else
				canvas.drawBitmap(right[0], xPos, yPos, null);
		}
	}
	
	private int prevY = 0;
	
	public void update(Long elapsedTime) {
		int elapsed = (int)(elapsedTime / 1000000); //converting to milliseconds
		moveDist = elapsed / 8;
		//apply gravity and time delays and such
		
		//applying gravity to jumps and falls - should add terminal velocity
		if (isJumping || falling) {
			
			//checking if top of jump has been reached
			if (dY < 0) {
				falling = true;
			}
			
			//setting start acceleration for falling
			if (falling && dY > 0) 
				dY = 0;
			
			//changing position during jump/fall
			yPos -= dY;
			dY -= (2.0 * yScale) / (7.0 * yScale); //hard-coded so that distance moved is the same for low frame rates
			if (dY < (-8 * yScale)) {
				dY = (float) (-8 * yScale);
				isJumping = false;
			}
			if (jumpingSideways) {
				if (direction == 0)
					xPos -= 2.0; //hard-coded so that distance moved is the same for low frame rates
				else
					xPos += 2.0; //hard-coded so that distance moved is the same for low frame rates
			}
		}
		
		//updating walking frame
		frameElapsed += elapsed;
		if (frameElapsed >= 200) {
			frameElapsed -= 200;
			walkingFrame += 1;
			if (walkingFrame == 4)
				walkingFrame = 0;
		}
	}
	
	public void walk() {
		walking = true;
		if (direction == 0) {
			xPos -= 2.0;//moveDist;
		}
		else {
			xPos += 2.0;//moveDist;
		}		
	}
	
	public void jump() {
		walking = false;
		isJumping = true;
		falling = false;
		dY = defaultDY;
	}
	
	public void jumpSide() {
		walking = false;
		isJumping = true;
		jumpingSideways = true;
		falling = false;
		dY = defaultDY;
	}
	
	public void stop() {
		walking = false;
	}
	
	public void driftLeft() {
		walking = false;
		if (jumpingSideways && !isJumping){
			if (direction == 1)
				jumpingSideways = false;
		}
		else {
			xPos -= 2.0;//moveDist;
			direction = 0;
		}
	}
	
	public void driftRight() {
		walking = false;
		if (jumpingSideways && !isJumping){
			if (direction == 0)
				jumpingSideways = false;
		}
		else {
			xPos += 2.0;//moveDist;
			direction = 1;
		}
	}
	
	public void climb() {
		
	}
	
	public void attack1() {
		
	}
	
	public void attack2() {
		
	}
}
