import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class Game {

	JFrame jf;
	DrawPanel dp;
	
	public int w, h;
	
	Tetris game;
	
	boolean pause = false;
	
	static boolean[] keys = new boolean[6]; //up, down, right, left, space, c
	
	public static void main(String[] args) {
		int size = 4;
		if (args.length > 0) {
			try {
				size = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.print("INVALID ARGUMENTS\nUSAGE: java -jar <jar_file>.jar (<tetrimino_size>)");
			}
		} else {
			System.out.println("Try adding a custom <tetrimino_size> to the run arguments!");
		}

		if (size > 6)
			System.out.println("WARNING: Tetriminos too big, may break game. Ideally try 1 <= size <= 6");

		System.out.println("Tetrimino Size: " + size);
		
		new Game().run(size);
	}
	
	private void run(int size) {
		jf = new JFrame("Tetris");
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		dp = new DrawPanel();
		jf.getContentPane().add(BorderLayout.CENTER, dp);
		
		jf.setSize(400, 400);
		jf.setLocation(375, 50);
		jf.setVisible(true);
		jf.setResizable(false);
		jf.addKeyListener(new AL());
		
		w = jf.getWidth();
		h = jf.getHeight() - 20;
		
		game = new Tetris(10, 20, size);
		
		while (true) {
			if (!pause)
				game.update(keys);
			
			jf.repaint();
			try {
				Thread.sleep(50);
			} catch (Exception exc) {}
		}
		
		
	}
	
	public class DrawPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
			if (game != null)
				game.draw(g, w, h);
		}
	}
	

	
	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP)
				keys[0] = true;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				keys[1] = true;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				keys[2] = true;
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				keys[3] = true;
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				keys[4] = true;
			if (e.getKeyCode() == KeyEvent.VK_C)
				keys[5] = true;
			
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				pause = !pause;
		}
		
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP)
				keys[0] = false;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				keys[1] = false;
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				keys[2] = false;
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				keys[3] = false;
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				keys[4] = false;
			if (e.getKeyCode() == KeyEvent.VK_C)
				keys[5] = false;
		}
	}
}