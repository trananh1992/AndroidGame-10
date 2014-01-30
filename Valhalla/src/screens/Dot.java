package screens;

public class Dot {
	
	public float xPos;
	public float yPos;
	public float speed;
	public double angle;
	public float dY = 0;
	public float dX = 0;
	public int screenWidth;
	public int screenHeight;
	public float xScale;
	public float yScale;
	
	public Dot(int x, int y, double ang, float sp, int width, int height, float xS, float yS) {
		xPos = x;
		yPos = y;
		angle = ang;
		speed = sp;
		screenWidth = width;
		screenHeight = height;
		xScale = xS;
		yScale = yS;
	}
	
	public void turn(int dir) {
		switch (dir) {
		case 0:
		case 1: //little left
			angle += 0.05;
			break;
		case 2:
		case 3: //little right
			angle -= 0.05;
			break;
		case 4: 
		case 5: //big left
			angle -= 0.1;
			break;
		case 6:
		case 7: //big right
			angle += 0.1;
			break;
		case 8: //huge right
			angle += 0.3;
			break;
		case 9: //huge left
			angle -= 0.3;
			break;
		case 10: //nothing
			break;
		case 11: //nothing
			break;
		}
		
		if (angle > Math.PI * 2)
			angle -= Math.PI * 2;
		else if (angle < 0)
			angle += Math.PI * 2;
	}
	
	public void move() {
		dX = (float)(Math.cos(angle) * speed);
		xPos += (float)(Math.cos(angle) * speed);
		dY = (float)(Math.sin(angle) * speed);
		yPos += (float)(Math.sin(angle) * speed);
		
		//bounding
		if (xPos > screenWidth)
			xPos = 0 - (150 * xScale);
		else if (xPos < -(150 * xScale))
			xPos = screenWidth;
		if (yPos > screenHeight)
			yPos = 0 - (150 * yScale);
		else if (yPos < -(150 * yScale))
			yPos = screenHeight;
	}

}
