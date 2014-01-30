package screens;

import android.graphics.Bitmap;

//stores the image and location of a screen-based element
public class ScreenElement {
	
	public Bitmap image;
	public int xPos;
	public int yPos;
	
	public ScreenElement(Bitmap Image, int x, int y) {
		image = Image;
		xPos = x;
		yPos = y;
	}
}
