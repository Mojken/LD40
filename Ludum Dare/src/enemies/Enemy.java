package enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import main.LD;
import net.abysmal.engine.Constants;
import net.abysmal.engine.entities.Entity;
import net.abysmal.engine.graphics.Graphics;
import net.abysmal.engine.maths.Hitbox;
import net.abysmal.engine.maths.Vector;

public class Enemy extends Entity {

	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static ArrayList<Enemy> enemyTypes = new ArrayList<Enemy>();

	static short baseHealth;
	static short movementSpeed = 15;

	public Enemy(short HP, String name) {
		super(0, 85, new Hitbox(new Vector(-4, -4), new Vector(4, 4)), name);
		baseHealth = HP;
		enemyTypes.add(this);
	}

	public Enemy(Entity type, Vector pos) {
		super(type, pos);
		HP = baseHealth;
	}

	public void draw(Graphics g) {
		try {
			Vector screenPos = LD.p.getPos().add(pos).sub(15f);
			double angle = LD.p.pos.sub(pos).calculateAngle();
			Graphics2D grot = (Graphics2D) g.create();
			grot.rotate(-angle + Constants.UP, screenPos.x + 15, screenPos.y + 15);
			grot.drawImage(ImageIO.read(textureURL), (int) screenPos.x, (int) screenPos.y, 31, 37, null);
			g.fillRect(screenPos.add(new Vector(-5, 23)), screenPos.add(new Vector(25, 28)));
			g.setColour(Color.gray);
			g.fillRect(screenPos.add(new Vector(-3, 24)), screenPos.add(new Vector((float) ((HP / (double) baseHealth) * 23), 27)));
			g.setColour(Color.BLACK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static int attackers = 0;
	boolean attacker = false;

	public void update() {
		if (LD.p.pos.sub(pos).calculateLength() < 1000) {
			cooldown = (short) (cooldown > 0 ? cooldown - 1:0);
			if (((double) HP) / baseHealth > .25 || HP > LD.p.swordDamage) {
				if ((!attacker && attackers < 3) || attacker) {
					attack();
				} else circle();
			} else flee();
		}
	}

	short cooldown = 0;
	short damage = 3;
	double angle = 0;
	boolean shot = false;

	public void attack() {
		if (!attacker && attackers < 3) {
			attacker = true;
			attackers++;
		}
		Vector rotation = LD.p.pos.sub(pos);
		if (rotation.calculateLength() >= 60) teleport(pos.add(new Vector(rotation.calculateAngle(), movementSpeed / 3f)));
		else {
			if (cooldown <= 25) {
				if (rotation.calculateLength() > 27) teleport(pos.add(new Vector(rotation.calculateAngle(), movementSpeed / 10f)));
				else if (cooldown <= 0) {
					LD.p.HP -= damage;
					cooldown = 200;
					angle = System.currentTimeMillis() % 2 == 0 ? Constants.LEFT:Constants.RIGHT;
				}
			} else if (rotation.calculateLength() < 40) teleport(pos.add(new Vector(rotation.calculateAngle() + Constants.UP, movementSpeed / 5f)));
			else teleport(pos.add(new Vector(rotation.calculateAngle() + angle, movementSpeed / 50f)));
		}
	}

	public void flee() {
		if (System.currentTimeMillis() % 20 == 0 || angle == 0.0d) angle = (Math.random() < 0.3) ? angle + Constants.UP:angle;
		if (attacker) {
			attacker = false;
			attackers--;
		}
		Vector rotation = pos.sub(LD.p.pos);
		if (rotation.calculateLength() < 260) teleport(pos.add(new Vector(rotation.calculateAngle(), movementSpeed / 4f)));
		else if (rotation.calculateLength() > 300) teleport(pos.add(new Vector(rotation.calculateAngle() + Constants.UP, movementSpeed / 4f)));
		else {
			if (Math.floor(System.currentTimeMillis() / 3) % 3 == 0) teleport(pos.add(new Vector(rotation.calculateAngle() + angle, movementSpeed / 10f)));
			if (cooldown <= 0) {
				LD.p.HP -= damage / 3;
				cooldown = 200;
				shot = true;
			}
		}
	}

	public void circle() {
		if (System.currentTimeMillis() % 500 == 0) angle = System.currentTimeMillis() % 2 == 0 ? Constants.LEFT:Constants.RIGHT;
		if (angle == 0.0d) angle = System.currentTimeMillis() % 2 == 0 ? Constants.LEFT:Constants.RIGHT;
		if (attacker) {
			attacker = false;
			attackers--;
		}
		Vector rotation = pos.sub(LD.p.pos);
		if (rotation.calculateLength() < 95) {
			teleport(pos.add(new Vector(rotation.calculateAngle(), (movementSpeed + 3) / 4f)));
		} else {
			if (rotation.calculateLength() >= 120) {
				teleport(pos.add(new Vector(rotation.calculateAngle() + Constants.UP, movementSpeed / 5f)));
			} else {
				teleport(pos.add(new Vector(rotation.calculateAngle() + angle, movementSpeed / 10f)));
			}
		}
	}

	public void hurt(short damage) {
		HP -= damage;
		killMobs();
	}

	public static void killMobs() {
		for (Enemy e:enemies) {
			if (e.HP <= 0) {
				if (e.attacker) attackers--;
				e.kill();
			}
		}
	}

	public void kill() {
		enemies.remove(this);
		LD.p.kills++;
		LD.p.heal();
	}

	public void summon(Vector pos) {
		enemies.add(new Enemy(this, pos));
	}
}
