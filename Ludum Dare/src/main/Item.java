package main;

import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import net.abysmal.engine.entities.Entity;
import net.abysmal.engine.graphics.Graphics;
import net.abysmal.engine.maths.Vector;

public class Item extends Entity {

	static boolean[] itemsBool = new boolean[100];
	static ArrayList<Item> items = new ArrayList<Item>();
	int id;
	Vector chunk;

	public Item(int id, String name, Vector chunk) {
		this.id = id;
		textureStr = name;
		textureURL = ClassLoader.getSystemResource("items/" + name + ".png");
		items.add(this);
		this.chunk = chunk;
	}

	public static void init(Vector chunk) {
		for (Item i:items) {
			if (chunk == i.chunk) Map.chunks.get(chunk).items.add(i);
		}
	}

	public void draw(Graphics g) {
		System.out.println(pos);
		try {
			g.drawImage(ImageIO.read(textureURL), LD.p.getPos());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}