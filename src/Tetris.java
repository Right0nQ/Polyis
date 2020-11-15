import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Tetris {
	static ArrayList<Color> colors = new ArrayList<Color>();
	
	byte[][] board;
	
	int[] pieceCounts;
	
	int pieceSize;
	
	int boardW;
	int boardH;
	
	int frameDelay = 2;
	int frameCount = 0;
	
	int downAlive = 10;
	int downCount = 0;
	boolean down = false;
	
	int keyDelay = 3;
	int[] keyDelays = new int[6];
	
	Tetrimino current;
	Tetrimino hold;
	
	ArrayList<Tetrimino> nextT = new ArrayList<Tetrimino>();
	final static int nextLength = 3;
	
	
	public Tetris(int width, int height, int pieceSize) {
		boardW = width;
		boardH = height;
		
		this.pieceSize = pieceSize;
		
		board = new byte[height][width];
		
		Tetrimino.setStart((width - pieceSize) / 2 + 1, -pieceSize + 2);

		Tetrimino.makePieces(pieceSize);
		Tetrimino.colorPieces(colors);
		
		pieceCounts = new int[Tetrimino.pieces.size()];
		
		current = new Tetrimino();
		pieceCounts[current.type]++;
		
		fillNext();
	}
	
	public void fillNext() {
		while (nextT.size() < nextLength) {
			nextT.add(new Tetrimino(equalRand()));
			pieceCounts[nextT.get(nextT.size() - 1).type]++;
		}
	}
	
	public void draw(Graphics g, int w, int h) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
		int blockSize = Math.min((w - 100) / boardW, (h - 100) / boardH);
		
		if (current != null)
			current.boardAdd(board);
		
		drawBoard(g, board, w / 2 - blockSize * boardW / 2, h / 2 - blockSize * boardH / 2, blockSize);
		
		if (current != null)
			current.boardSub(board);
		
		
		
		byte[][] nextArea = new byte[nextLength * (pieceSize + 2)][(pieceSize + 2)];
		int y = 1;
		for (Tetrimino t: nextT) {
			t.boardAdd(nextArea, 1, y);
			y += pieceSize + 2;
		}
		
		drawBoard(g, nextArea, w / 2 + blockSize * boardW / 2 + blockSize / 2, h / 2 - blockSize * boardH / 2, blockSize / 2);
		
		nextArea = new byte[pieceSize + 2][(pieceSize + 2)];
		if (hold != null)
			hold.boardAdd(nextArea, 1, 1);
		drawBoard(g, nextArea, w / 2 - blockSize * boardW / 2 - (pieceSize + 3) * blockSize / 2, h / 2 - blockSize * boardH / 2, blockSize / 2);
		
		
	}
	
	public static void drawBoard(Graphics g, byte[][] board, int startX, int startY, int blockSize) {
		int trueY = startY;
		int trueX;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(startX - 2, startY - 2, blockSize * board[0].length + 4, blockSize * board.length + 4);
		
		for (int y = 0; y < board.length; y++) {
			trueX = startX;
			
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] < colors.size())
					g.setColor(colors.get(board[y][x]));
				else
					g.setColor(Color.BLACK);
				
				g.fillRect(trueX + 1, trueY + 1, blockSize - 2, blockSize - 2);
				
				trueX += blockSize;
			}
			
			trueY += blockSize;
		}
		
	}
	
	public void update(boolean[] keys) {
		frameCount++;
		
		if (keys[1]) {
			frameCount = frameCount % (frameDelay / 2);
		} else {
			frameCount = frameCount % frameDelay;
		}
		
		
		if (current == null) {
			current = nextT.remove(0);
		}
		
		if (frameCount == 0)
			down = !current.drop(board);
		
		if (down)
			downCount++;
		
		if (keys[0] && keyDelays[0] == 0 && current.rotate(board)) {
			downCount = 0;
		}
		
		if (keys[2] && keyDelays[2] == 0  && current.moveRight(board)) {
			downCount = 0;
		}
		
		if (keys[3] && keyDelays[3] == 0  && current.moveLeft(board)) {
			downCount = 0;
		}
		
		if (keys[4] && keyDelays[4] == 0) {
			current.hardDrop(board);
			downCount = downAlive;
		}
		
		if (keys[5] && keyDelays[5] == 0) {
			if (hold == null) {
				hold = current;
			} else {
				nextT.add(0, hold);
				hold = current;
			}
			
			hold.reset();
			
			current = nextT.remove(0);
		}
		
		
		if (downCount >= downAlive) {
			current.boardAdd(board);
			checkLines();
			frameCount = 1;
			downCount = 0;
			current = nextT.remove(0);
		}
		
		fillNext();
		
		for (int k = 0; k < keys.length; k++) {
			if (keys[k]) {
				keyDelays[k] = (keyDelays[k] + 1) % keyDelay;
			} else {
				keyDelays[k] = 0;
			}
		}
	}
	
	public int checkLines() {
		int cleared = 0;
		boolean filled = true;
		
		for (int y = 0; y < board.length; y++) {
			filled = true;
			
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == 0)
					filled = false;
			}
			
			if (filled) {
				shiftLines(0, y - 1, 1);
			}
		}
		
		return cleared;
	}
	
	public void shiftLines(int y1, int y2, int amt) {
		if (amt > 0) {
			for (int y = y2; y >= y1; y--) {
				if (y + amt > board.length || y + amt < 0)
					continue;
				
				for (int x = 0; x < board[y].length; x++) {
					board[y + amt][x] = board[y][x];
					
				}
			}
			
			clearRect(0, y1, board[0].length, y1 + amt - 1);
		} else if (amt < 0) {
			for (int y = y1; y <= y2; y++) {
				if (y + amt > board.length || y + amt < 0)
					continue;
				
				for (int x = 0; x < board[y].length; x++) {
					board[y + amt][x] = board[y][x];
					
				}
			}
			
			clearRect(0, y2 + amt + 1, board[0].length, y2);
		}
	}
	
	public void clearRect(int x1, int y1, int x2, int y2) {
		for (int y = y1; y < y2; y++) {
			if (y > board.length || y < 0)
				continue;
			
			for (int x = x1; x < x2; x++) {
				if (x > board[y].length || x < 0)
					continue;
				
				board[y][x] = 0;
			}
		}
		
	}
	
	public int equalRand() {
		int max = 0;
		
		for (int i = 0; i < pieceCounts.length; i++) {
			if (pieceCounts[i]> max)
				max = pieceCounts[i];
		}
		
		int[] weights = new int[pieceCounts.length];
		int total = 0;
		
		for (int i = 0; i < pieceCounts.length; i++) {
			weights[i] = max - pieceCounts[i];
			total += weights[i];
		}
		
		int rand = (int) (Math.random() * total);
		
		int i = 0;
		while(rand > weights[i]) {
			rand -= weights[i];
			i++;
		}
		
		return i;
	}
	
}
