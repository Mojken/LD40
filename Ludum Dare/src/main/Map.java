package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.abysmal.engine.graphics.Graphics;
import net.abysmal.engine.handlers.misc.Tile;
import net.abysmal.engine.handlers.misc.World;
import net.abysmal.engine.maths.Dimension;
import net.abysmal.engine.maths.Vector;

public class Map {

	static HashMap<Vector, Map> chunks = new HashMap<Vector, Map>();
	static Map def;
	static Dimension mapSize;
	static int tileSize;
	World world;
	public static int ScreenshakeMagnitude = 0;
	ArrayList<Item> items = new ArrayList<Item>();

	public static void init() {
		def = new Map("maps/voidmap.png");
	}

	public static void addMap(Vector pos) {
		chunks.put(pos, new Map("maps/map" + (int) pos.y + "-" + (int) pos.x + ".png"));
		mapSize = new Dimension(chunks.get(pos).world.worldSize.toVector().multiply(3f));
		tileSize = chunks.get(pos).world.tileSize * 3;
		Item.init(pos);
	}

	public Map(String filename) {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					world = new World(ClassLoader.getSystemResource(filename), 16, false);
				} catch (IllegalArgumentException e) {
					world = def.world;
				}
			}
		}.run();
	}

	public static void update() {
		Vector p = LD.p.getChunk(0);
		for (int i = 0; i < 25; i++) {
			Vector pp = new Vector(p.x + i % 5, p.y + i / 5);
			if (null == chunks.get(pp)) {
				Map.addMap(pp);
			}
		}
	}

	static BufferedImage img = new BufferedImage(20 * 16, 20 * 16, BufferedImage.TYPE_INT_RGB);
	public static void draw(Graphics g) {
		if (LD.levedit) {
			for (int y = 0; y < 20; y++) {
				for (int x = 0; x < 20; x++) {
					for (int i = 2; i >= 0; i--) {
						int val = LD.map[i][x + y * 20];

						try {
							if (val != 0) {
								img.createGraphics().drawImage(ImageIO.read(Tile.getTile(val, i).getTextureURL()), x * 16, y * 16, null);
								LD.map[i][x + y * 20] = 0;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			g.drawImage(img, Vector.ZERO(), new Vector(960, 960));
		} else {

			for (int i = 0; i < 4; i++) {
				if (null != LD.p.getChunk(i)) {
					Vector p = LD.p.getChunk(i);
					Map chunk = chunks.get(p);
					if (null == chunk) {
						Map.addMap(p);
						chunk = chunks.get(p);
					}
					if (null == chunk) chunk = def;
					World world = chunk.world;
					Vector pos = LD.p.getPos().add(screenshake()).add(world.worldSize.toVector().multiply(p.multiply(3f)));
					g.drawImage(world.world, pos, world.worldSize.toVector().multiply(3f));
				}
			}
		}
	}
	
	

	static short index = 0;
	static short count = 0;

	public static Vector screenshake() {
		if (ScreenshakeMagnitude == 0) { return Vector.ZERO(); }
		if (count >= 2) {
			index = 0;
			count = 0;
			ScreenshakeMagnitude = 0;
			return Vector.ZERO();

		}
		switch (index) {
			case 0:
				index++;
				return new Vector(ScreenshakeMagnitude, 0);
			case 1:
				index++;
				return new Vector(-ScreenshakeMagnitude, 0);
			case 2:
				index++;
				return new Vector(0, ScreenshakeMagnitude);
			case 3:
				index++;
				return new Vector(0, -ScreenshakeMagnitude);
			case 4:
				index = 0;
				count++;
				return Vector.ZERO();
		}
		return null;
	}
}
