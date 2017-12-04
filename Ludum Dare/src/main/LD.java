package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import enemies.Enemy;
import enums.Items;
import enums.Tiles;
import net.abysmal.engine.entities.Entity;
import net.abysmal.engine.graphics.Graphics;
import net.abysmal.engine.graphics.Window;
import net.abysmal.engine.handlers.HID.IKeyboard;
import net.abysmal.engine.handlers.misc.Settings;
import net.abysmal.engine.handlers.misc.Tick;
import net.abysmal.engine.maths.Dimension;
import net.abysmal.engine.maths.Vector;

public class LD {

	public static Window w;
	String title = "LD";
	public Dimension size = new Dimension(980, 540);
	public static Player p;
	public static Map wo;
	public static boolean init = false, levedit = false;

	public static Vector tile = Vector.ZERO();
	public static int[][] map = { new int[20 * 20], new int[20 * 20], new int[20 * 20] };
	public static int[][] outvals = { new int[20 * 20], new int[20 * 20], new int[20 * 20] };
	static BufferedImage output = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
	static boolean done = false;

	public static void main(String[] args) {
// if (args[0].equals("convert")) {
// int x = 0, y = 0;
// for (int i = 0; i < 1333; i++) {
// if (x == 43) {
// x = 0;
// y++;
// }
// try {
// BufferedImage img = ImageIO.read(ClassLoader.getSystemResource(("maps/temp/" + i + ".png")));
// File outputfile = new File("map" + x + "-" + +y + ".png");
// ImageIO.write(img, "png", outputfile);
// } catch (IOException e) {
// e.printStackTrace();
// }
// x++;
// }
// }
		new LD();
	}

	public LD() {
		Settings.setDvorak();
		w = new Window();
		w.createWindow(title, levedit ? new Dimension(960, 960):size);
		Window.frame.setUndecorated(levedit);
		w.start(new Update());

		for (int i = 0; i < 20 * 20; i++) {
			map[1][i] = 1;
		}
		w.addKeyListener(new IKeyboard() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W:
						p.setMomentum(p.getMomentum().sub(new Vector(0, -1)));
					break;
					case KeyEvent.VK_A:
						p.setMomentum(p.getMomentum().sub(new Vector(-1, 0)));
					break;
					case KeyEvent.VK_S:
						p.setMomentum(p.getMomentum().sub(new Vector(0, 1)));
					break;
					case KeyEvent.VK_D:
						p.setMomentum(p.getMomentum().sub(new Vector(1, 0)));
					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (levedit) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_AMPERSAND:
							tile = new Vector(1, 1);
						break;
						case 55:
							tile = new Vector(1, 2);
						break;
						case 53:
							tile = new Vector(1, 0);
						break;
						case 51:
							tile = Vector.ZERO();
						break;
						case KeyEvent.VK_ENTER:
							File outputfile = new File("saved.png");
							done = true;
							for (int i = 0; i < 20 * 20; i++) {
								int colour = 0;
								for (int n = 2; n >= 0; n--) {
									colour += outvals[n][i] * Math.pow(256, 2 - n);
								}
								if ((colour & 0xFFFF) == 0) colour += 0x100;
								else if ((colour & 0xFF00) > 0 && (colour & 0xFF) > 0) colour &= 0xFF00;

								java.awt.Graphics g = output.getGraphics();
								g.setColor(new Color(colour));

								g.fillRect((int) i % 20, (int) i / 20, 1, 1);
							}
							try {
								ImageIO.write(output, "png", outputfile);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						break;
					}
				}
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W:
						p.setMomentum(p.getMomentum().add(new Vector(0, -1)));
					break;
					case KeyEvent.VK_A:
						p.setMomentum(p.getMomentum().add(new Vector(-1, 0)));
					break;
					case KeyEvent.VK_S:
						p.setMomentum(p.getMomentum().add(new Vector(0, 1)));
					break;
					case KeyEvent.VK_D:
						p.setMomentum(p.getMomentum().add(new Vector(1, 0)));
					break;
					case KeyEvent.VK_SPACE:
						p.dash();
					break;
				}
			}
		});

		Window.frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if (levedit) {
					Vector p = new Vector((int) (LD.w.mouseListener.getMousePosition().x / 48), (int) (LD.w.mouseListener.getMousePosition().y / 48 + .5));
					int i = (int) (p.x + (p.y * 20));
					switch (e.getButton()) {
						case 1:
							map[(int) tile.y][i] = (int) LD.tile.x;
							outvals[(int) tile.y][i] = (int) LD.tile.x;
							if ((int) tile.y == 0) {
								outvals[1][i] = 0;
							}
						break;
						case 3:
							map[(int) tile.y][i] = 0;
							outvals[(int) tile.y][i] = 0;
						break;
					}
				} else switch (e.getButton()) {
					case 1:
						p.slash();
					break;
					case 3:
						p.shoot();
					break;

				}
			}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {}
		});

		for (Tiles t:Tiles.values()) {
			t.name();
		}
		p = new Player();
		for (Items i:Items.values()) {
			i.name();
		}

		new Enemy((short) 10, "Enemy");
		Map.init();
		Map.addMap(Vector.ZERO());
//		p.teleport(new Vector(19104, 605 * 16 * 3));
		init = true;
	}
}

class Update implements Tick {

	boolean init = false;

	@Override
	public void update() {
		if (LD.init && !LD.levedit) {
			LD.p.update();
			Map.update();
			for (Enemy e:Enemy.enemies) {
				e.update();
			}
			if (Enemy.enemies.size() < 6) {
				Enemy.enemyTypes.get(0).summon(new Vector(100, 100));
			}
			init = true;
		}
	}

	double angle = 0;

	@Override
	public void render(Graphics g) {
		if (init || LD.levedit) {
			if (!LD.levedit) {
				Map.draw(g);
				LD.p.draw(g);
				for (Entity e:Map.chunks.get(LD.p.getChunk(0)).world.populace) {
					((Item) e).draw(g);
				}
				for (Enemy e:Enemy.enemies) {
					e.draw(g);
				}

			}
		}
		if (!init && !LD.levedit) {
			try {
				g.drawImage(ImageIO.read(ClassLoader.getSystemResource("title.png")), Vector.ZERO(), Window.dimension.toVector());
				Graphics2D grot = (Graphics2D) g.create();
				grot.rotate(angle += .06, 219, 453);
				grot.drawImage(ImageIO.read(ClassLoader.getSystemResource("entities/slash1.png")), 190, 430, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (LD.levedit && LD.init) {
			if (!LD.done) {
				Vector a = new Vector((int) (LD.w.mouseListener.getMousePosition().x / 48), (int) (LD.w.mouseListener.getMousePosition().y / 48 + .5)).multiply(48);
				Map.draw(g);
				g.drawRect(a, a.add(48f));
			}
		}

	}
}