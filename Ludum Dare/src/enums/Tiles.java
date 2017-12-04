package enums;

import net.abysmal.engine.handlers.misc.Tile;

public enum Tiles {
	
	emptyF(0, "void", 0),
	emptyR(0, "void", 1),
	emptyB(0, "voidB", 2),
	pine(1, "pine", new String[] {}, 0),
	snow(1, "snow", new String[] {}, 1),
	ice(2, "ice", new String[] {}, 1)
	;
	
	final int id, layer;
	final String path;
	final String[] traits;
	
	private Tiles(int id, String path, int layer) {
		this.id = id;
		this.layer = layer;
		this.path = path;
		this.traits = new String[]{""};
		new Tile(id, path, null);
		Tile.getArrayList(layer).add(new Tile(id, path, traits));
	}
	private Tiles(int id, String path, String[] traits, int layer) {
		this.id = id;
		this.layer = layer;
		this.path = path;
		this.traits = traits;
		new Tile(id, path, traits);
		Tile.getArrayList(layer).add(new Tile(id, path, traits));
	}
}
