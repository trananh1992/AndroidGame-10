package screens;

import game_objects.Platform;

import java.util.ArrayList;
import java.util.Random;

import levels.Level;

import com.games.platformerrpg.R;
import com.games.platformerrpg.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//the main game screen - pause will have to be in here as well, or new screens must not overwrite - (but pause is small)
public class GameScreen{
	
	//sound boolean
	public boolean sound;
	
	//screen stuff
	public ArrayList<ScreenElement> elements;
	protected Context context;
	protected float xScale;
	protected float yScale;
	protected int screenWidth = 1280;
	protected int screenHeight = 720;
	
	//the offset - due to player movement
	
	
	//dots
	private Bitmap glow1;
	private Bitmap glow2;
	private Bitmap glow3;
	private Dot[] size1;
	private Dot[] size2;
	private Dot[] size3;
	
	//paints
	private Paint blackPaint;
	private Paint whitePaint;
	
	//floors
	private Bitmap platform;
	private Bitmap midplatform;
	
	//level
	public Level level;
	
	//random variable
	Random rand;
	
	public boolean loaded = false;
	
	private float elapsedTurn = 0;
	public float elapsedMove = 0;
	
	//constructor
	public GameScreen (Context _context, float x_scale, float y_scale, int width, int height) {
		context = _context;
		xScale = x_scale;
		yScale = y_scale;
		screenWidth = width;
		screenHeight = height;
		elements = new ArrayList<ScreenElement>();
		
		rand = new Random(System.currentTimeMillis());
		
		//dot bitmaps
		glow1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(150 * xScale), (int)(150 * yScale), true);
		glow2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(80 * xScale), (int)(80 * yScale), true);
		glow3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.glowy1), (int)(40 * xScale), (int)(40 * yScale), true);
		
		//floor bitmap
		midplatform = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.basefloor), (int)(160 * xScale), (int)(30 * yScale), true);
		platform = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform), (int)(160 * xScale), (int)(30 * yScale), true);
		
		//initialising the level
		level = new Level(0, screenWidth, screenHeight, xScale, yScale);
		
		//initializing dot arrays
		size1 = new Dot[8];
		size2 = new Dot[8];
		size3 = new Dot[8];
		
		//initializing dots
		for (int i = 0; i < 8; i++) {
			size1[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() * 2 + 0.2), screenWidth, screenHeight, xScale, yScale);
			size2[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() * 2 + 0.2), screenWidth, screenHeight, xScale, yScale);
			size3[i] = new Dot((int)(rand.nextFloat() * screenWidth), (int)(rand.nextFloat() * screenHeight), (double)(rand.nextFloat() * (Math.PI * 2)), (float)(rand.nextFloat() * 2 + 0.2), screenWidth, screenHeight, xScale, yScale);
		}
		
		//black paint
		blackPaint = new Paint();
		blackPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		blackPaint.setStyle(Paint.Style.FILL);
		
		//white paint
		whitePaint = new Paint();
		whitePaint.setAntiAlias(true);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStyle(Paint.Style.FILL);
		
		loaded = true;
	}
	
	public void update(long elapsedTime) {
		//updating elapsed time
		elapsedTurn += elapsedTime / 1000000;
		elapsedMove += elapsedTime / 1000000;
		
		//turning dots every half second
		if (elapsedTurn >= 500) {
			elapsedTurn -= 500;
			for (int i = 0; i < 8; i++) {
				size1[i].turn(rand.nextInt(12));
				size2[i].turn(rand.nextInt(12));
				size3[i].turn(rand.nextInt(12));
			}
		}
		
		//moving dots
		if (elapsedMove >= 25) {
			elapsedMove -= 25;
			for (int i = 0; i < 8; i++) {
				size1[i].move();
				size2[i].move();
				size3[i].move();
			}
		}
	}
	
	//drawing the game screen
	public void draw(Canvas canvas) {
		//drawing the dots
		for (int i = 0; i < 8; i++) {
			canvas.drawBitmap(glow1, size1[i].xPos, size1[i].yPos, null);
			canvas.drawBitmap(glow2, size2[i].xPos, size1[i].yPos, null);
			canvas.drawBitmap(glow3, size3[i].xPos, size1[i].yPos, null);
		}
		
		//drawing the level
		for (Platform p : level.platforms) {
			if (p.xPos > (0 - (int)(150 * xScale)) && p.xPos < screenWidth) {
				if (p.yPos > (0 - (int)(30 * yScale)) && p.yPos < screenHeight) {
					if ( p.size == 1) 
						canvas.drawBitmap(platform, p.xPos, p.yPos, null);
					else if (p.size == 2)
						canvas.drawBitmap(midplatform, p.xPos, p.yPos, null);
				}
			}
		}
		
		//drawing the game elements
		for (ScreenElement se : elements)
			canvas.drawBitmap(se.image, se.xPos, se.yPos, null);
		
		//drawing the floor
		canvas.drawRect((int)(0 * xScale), (int)(540 * yScale), screenWidth, (int)(740 * yScale), blackPaint);

	}
}
