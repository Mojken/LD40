package enums;

import main.Item;
import net.abysmal.engine.entities.Entity;
import net.abysmal.engine.maths.Vector;

public enum Items {
	revolver(0, "revolver", Vector.ZERO())
	;
	
	
	private Items(int id, String name, Vector chunk) {
		Entity.entityTypes.add(new Item(id, name, chunk));
	}
	
}
