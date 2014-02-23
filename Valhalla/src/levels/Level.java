package levels;

import java.util.ArrayList;

import game_objects.Platform;

public class Level {
	
	//screen attributes
	public int screenWidth;
	public int screenHeight;
	public float xScale;
	public float yScale;
	
	//level attributes
	public int levelID;
	public int mapWidth;
	public int mapHeight;
	public int spawnX;
	public int spawnY;
	
	//level objects
	public ArrayList<Platform> platforms;

	public Level(int ID, int sWidth, int sHeight, float xS, float yS) {
		
		levelID = ID;
		screenWidth = sWidth;
		screenHeight = sHeight;
		xScale = xS;
		yScale = yS;
		
		//read level from file based on ID
		
		//default/test level
		mapWidth = screenWidth * 2;
		mapHeight = screenHeight * 2;
		
		//assigning platforms
		platforms = new ArrayList<Platform>();
		
		//floor
		for (int i = 0; i < mapWidth; i += (int)(150 * xScale)) {
			platforms.add(new Platform(i, (int)(520 * yScale), 2)); //245 off the bottom - doesn't currently map to the level map
		}
		
		//platforms
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				platforms.add(new Platform(i * 300, (j * 110), 1));
			}
		}
		

		
		
		
	}
}
