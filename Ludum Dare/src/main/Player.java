package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import enemies.Enemy;
import net.abysmal.engine.Constants;
import net.abysmal.engine.entities.Entity;
import net.abysmal.engine.graphics.Graphics;
import net.abysmal.engine.graphics.Window;
import net.abysmal.engine.handlers.misc.Sound;
import net.abysmal.engine.handlers.misc.Tile;
import net.abysmal.engine.maths.Vector;

public class Player extends Entity {

	public short kills = 0;
	short dashCooldown = 0;
	short gunCooldown = 0;
	short swordCooldown = 0;
	short reach = 300;
	short gunDamage = 8;
	public short swordDamage = 4;
	short swordReach = 50;
	short maxHealth = 50;
	short heal = 3;
	double slashWidth = 0.9;
	int movementSpeed = 6;

	Vector chunk = Vector.ZERO();
	Vector rotation;
	Vector ppos;
	Vector dash = Vector.ZERO();
	Vector antiDash = Vector.ZERO();
	private short nextLvl = 10;

	public Player() {
		super(new Vector(20.0F, 20.0F), 85.0F, new net.abysmal.engine.maths.Hitbox(new Vector(-2.0F, -2.0F), new Vector(2.0F, 2.0F)), "Player");
		Entity.entityTypes.add(this);
		HP = maxHealth;
	}

	short stepDown = 0;

	boolean dashed = true;

	public void update() {
		if (antiDash.calculateLength() < dash.calculateLength()) {
			antiDash = new Vector(dash.calculateAngle(), antiDash.calculateLength() + 8.0F);
		}
		if (antiDash.calculateLength() >= dash.calculateLength()) {
			antiDash = Vector.ZERO();
			dash = Vector.ZERO();
		}
		rotation = LD.w.mouseListener.getMousePosition().sub(Window.dimension.toVector().multiply(0.5F));
		ppos = Window.dimension.toVector().multiply(0.5F).sub(rotation.normalize().multiply(30.0F)).add(dash).sub(antiDash);
		if (momentum.calculateLength() > 0.0F) {
			moveP(pos.add(new Vector(momentum.calculateAngle(), movementSpeed)), false);
			if (stepDown <= 0) try {
				stepDown = ((short) (int) (25L + System.currentTimeMillis() % 10L - 2L));
				switch ((int) System.currentTimeMillis() % 5) {
					case 0:
						new Sound(new File("res/sounds/step.wav")).play(false);
					break;
					case 1:
						new Sound(new File("res/sounds/step2.wav")).play(false);
					break;
					case 2:
						new Sound(new File("res/sounds/step3.wav")).play(false);
					break;
					case 3:
						new Sound(new File("res/sounds/step4.wav")).play(false);
					break;
					case 4:
						new Sound(new File("res/sounds/step5.wav")).play(false);
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			else stepDown = ((short) (stepDown - 1));
		}
		gunCooldown = ((short) (gunCooldown > 0 ? gunCooldown - 1:0));
		dashCooldown = ((short) (dashCooldown > 0 ? dashCooldown - 1:0));
		swordCooldown = ((short) (swordCooldown > 0 ? swordCooldown - 1:0));
		if (kills >= nextLvl) lvlup();
// System.out.println(pos + ", " + getChunk(0));
	}

	public void lvlup() {
//		movementSpeed += 1;
//		maxHealth = ((short) (maxHealth + 5));
//		gunDamage = ((short) (gunDamage + 2));
//		swordDamage = ((short) (swordDamage + 1));
//		heal = ((short) (heal + 1));
//		nextLvl = ((short) (Math.pow(nextLvl, 1.35)));
	}

	short dashDraw = 0;
	short shotDraw = 0;
	short slashDraw = 0;

	public void draw(Graphics g) {
//		g.fillRect(new Vector(10.0F, 10.0F), new Vector(320.0F, 40.0F));
//		g.setColour(new Color(0x261111));
//		g.fillRect(new Vector(12.0F, 12.0F), new Vector((((float) HP) / maxHealth) * 318, 38));
//		g.setColour(Color.BLACK);

		double angle = -rotation.calculateAngle();
		Graphics2D grot = (Graphics2D) g.create();
		grot.rotate(angle, ppos.x, ppos.y);
		grot.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

		if (shotDraw > 0) {
			shotDraw--;
			try {
				grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/shot.png")), (int) ppos.x - 7, (int) ppos.y + 91, 14, -72, null);
				grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/reach.png")), (int) ppos.x - 6, (int) ppos.y + reach, 12, 8, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (slashDraw > 0) {
			slashDraw--;
			try {
				grot.rotate(Constants.UP * .9, ppos.x, ppos.y);
				switch (slashDraw) {
					case 10:
					case 9:
					case 8:
					case 7:
					case 6:
						grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/slash1.png")), (int) ppos.x - 16, (int) ppos.y - 40, 68, 48, null);
					break;
					case 5:
					case 4:
						grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/slash2.png")), (int) ppos.x - 47, (int) ppos.y - 63, 104, 90, null);
					break;
					case 3:
					case 2:
					case 1:
					case 0:
						grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/slash3.png")), (int) ppos.x - 47, (int) ppos.y - 63, 71, 91, null);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!dashed) {
			dashDraw = 5;
			moveP(pos.add(dash), true);
			dashed = true;
			g.clearRect();
		}

		if (dashDraw > 0) {
			dashDraw--;
			try {
				grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/dash.png")), (int) ppos.x + 7, (int) ppos.y - 7, -14, -30, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Vector a = ppos.sub(20.0F);
			grot.drawImage(javax.imageio.ImageIO.read(textureURL), (int) a.x, (int) a.y, 40, 40, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dash() {
		if (dashCooldown == 0) {
			try {
				new Sound(new File("res/sounds/dash.wav")).play(false);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			dash = new Vector(rotation.calculateAngle(), 260);
			dashed = false;
			dashCooldown = 100;
		}
	}

	public void shoot() {
		if (gunCooldown == 0) {
			Map.ScreenshakeMagnitude = 4;
			shotDraw = 4;
			try {
				new Sound(new File("res/sounds/gun3.wav")).play(false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			for (short i = 0; i < reach; i = (short) (i + 1)) {
				Vector pos = new Vector(rotation.calculateAngle(), i).add(this.pos);
				for (Enemy e:Enemy.enemies) {
					if (e.pos.sub(pos).calculateLength() <= 20.0F) {
						gunCooldown = 65;
						e.hurt(gunDamage);
						return;
					}
				}
			}
			gunCooldown = 100;
		}
	}

	public void slash() {
		if (swordCooldown == 0) {
			slashDraw = 11;
			for (Enemy e:Enemy.enemies) {
				if ((Math.abs(e.pos.sub(pos).calculateAngle() - rotation.calculateAngle()) < slashWidth) && (e.pos.sub(pos).calculateLength() < swordReach)) {
					try {
						new Sound(new File("res/sounds/swoosh.wav")).play(false);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					heal();
					e.hurt(swordDamage);
				}
			}
			swordCooldown = 20;
		}
	}

	public Vector getPos() {
		return ppos.sub(pos);
	}

	public void heal() {
		HP = (HP + heal > maxHealth ? maxHealth:HP + heal);
	}

	public void moveP(Vector dest, boolean dash) {
		Vector chunk = new Vector((float) Math.floor(dest.x / Map.mapSize.getWidth()), (float) Math.floor(dest.y / Map.mapSize.getHeight()));
		int index = (int) ((dest.sub(chunk.multiply(Map.mapSize.toVector())).x / Map.tileSize) + (int) (dest.sub(chunk.multiply(Map.mapSize.toVector())).y / Map.tileSize) * (Map.mapSize.getWidth() / 48));
		if (Map.chunks.get(chunk).world.tiles[0][index].getTextureURL() == ((Tile) Tile.tilesForeground.get(0)).getTextureURL()) {
			if (dash) this.dash = new Vector(this.dash.calculateAngle(), dest.sub(pos).calculateLength());
			teleport(dest);
		} else {
			int pindex = (int) (pos.sub(chunk.multiply(Map.mapSize.toVector())).x / Map.tileSize + (int) (pos.sub(chunk.multiply(Map.mapSize.toVector())).y / Map.tileSize) * (Map.mapSize.getWidth() / 48)) - index;
			switch (pindex) {
				case -1:
				case 1:
					teleport(new Vector(pos.x, dest.y));
				break;
				case -20:
				case 20:
					teleport(new Vector(dest.x, pos.y));
				break;
				default:
					if ((Math.abs(pindex) != 19) && (Math.abs(pindex) != 21) && ((Math.abs(pindex) != 1) || (Math.abs(pindex) != 20)) && (dest.calculateLength() > 5.0F) && dash) {
						dest = new Vector(dest.sub(pos).calculateAngle(), Math.abs(dest.sub(pos).calculateLength()) - 5.0F);
						moveP(dest.add(pos), true);
					}
				break;
			}
		}
	}

	public Vector getChunk(int i) {
		chunk = new Vector((float) Math.floor(pos.x / Map.mapSize.getWidth()), (float) Math.floor(pos.y / Map.mapSize.getHeight()));
		Vector a = null;
		Vector b = null;
		Vector c = null;
		int xp = (int) (-Map.mapSize.getHeight() * chunk.x);
		int yp = (int) (-Map.mapSize.getWidth() * chunk.y);
		int xm = (int) (-Map.mapSize.getHeight() * chunk.x) - 5;
		int ym = (int) (-Map.mapSize.getWidth() * chunk.y) - 125;

		if (getPos().x > xp) {
			a = chunk.add(new Vector(-1.0F, 0.0F));
			if (getPos().y > yp) {
				c = chunk.add(new Vector(-1.0F, -1.0F));
			} else if (getPos().y < ym) c = chunk.add(new Vector(-1.0F, 1.0F));
		} else if (getPos().x < xm) {
			a = chunk.add(new Vector(1.0F, 0.0F));
			if (getPos().y > yp) {
				c = chunk.add(new Vector(1.0F, -1.0F));
			} else if (getPos().y < ym) {
				c = chunk.add(new Vector(1.0F, 1.0F));
			}
		}
		if (getPos().y > yp) {
			b = chunk.add(new Vector(0.0F, -1.0F));
		} else if (getPos().y < ym) {
			b = chunk.add(new Vector(0.0F, 1.0F));
		}
		switch (i) {
			case 0:
				return chunk;
			case 1:
				return a;
			case 2:
				return b;
			case 3:
				return c;
		}
		return Vector.ZERO();
	}
}