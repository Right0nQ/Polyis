import java.awt.Color;
import java.util.ArrayList;

public class Tetrimino {

	static ArrayList<byte[][][]> pieces;

	int type;
	int rotation;
	
	static int startX;
	static int startY;
	
	int x;
	int y;

	public Tetrimino(int type) {
		this.type = type;
		rotation = 0;
		x = startX;
		y = startY;
	}
	
	public Tetrimino() {
		this.type = (int) (pieces.size() * Math.random());
		rotation = 0;
		x = startX;
		y = startY;
	}
	
	public void reset() {
		x = startX;
		y = startY;
	}
	
	public static void setStart(int x, int y) {
		startX = x;
		startY = y;
	}
	
	public static void makePieces(int size) {
		ArrayList<byte[][]> basic = generate(size);
		
		pieces = new ArrayList<byte[][][]>();
		
		byte[][] curr;
		byte[][][] rots;
		int w, h, sq;
		int xx = 0;
		int yy = 0;
		for (int i = 0; i < basic.size(); i++) {
			curr = basic.get(i);
			
			w = curr[0].length;
			h = curr.length;
			
			sq = w > h? w : h;
			
			rots = new byte[4][sq][sq];
			
			for (int r = 0; r < rots.length; r++) {
				xx = (sq - w) / 2;
				yy = (sq - h) / 2;
				
				copyInto(curr, rots[r], yy, xx);
				
				curr = rotatePiece(curr);
				w = curr[0].length;
				h = curr.length;
			}
			
			pieces.add(rots);
		}
	}
	
	public static void colorPieces(ArrayList<Color> colors) {
		int total = pieces.size();
		
		float offset = 1f / total;
		float currCol = 0f;
		
		colors.clear();
		
		colors.add(Color.BLACK);
		
		byte[][] curr;
		for (int i = 0; i < pieces.size(); i++) {
			colors.add(Color.getHSBColor(currCol, 1f, 1f));
			
			for (int r = 0; r < pieces.get(i).length; r++) {
				curr = pieces.get(i)[r];
				
				for (int j = 0; j < curr.length; j++) {
					for (int k = 0; k < curr[j].length; k++) {
						if (curr[j][k] != 0)
							curr[j][k] = (byte) (i + 1);
					}
				}
			}
			
			currCol += offset;
		}
	}
	
	public static ArrayList<byte[][]> generate(int size) {
		if (size == 1) {
			ArrayList<byte[][]> a = new ArrayList<byte[][]>();
			a.add(new byte[][] {{1}});
			return a;
		}
		
		ArrayList<byte[][]> oldP = generate(size - 1);
		
		ArrayList<byte[][]> newP = new ArrayList<byte[][]>();
		
		byte[][] curr;
		byte[][] next;
		for (int i = 0; i < oldP.size(); i++) {
			curr = pad(oldP.get(i));
			
			for (int j = 0; j < curr.length; j++) {
				f: for (int l = 0; l < curr[j].length; l++) {
					if (curr[j][l] == 0) {
						if ((j != 0 && curr[j-1][l] > 0) || (j != curr.length - 1 && curr[j+1][l] > 0) ||
								(l != 0 && curr[j][l-1] > 0) || (l != curr[0].length - 1 && curr[j][l+1] > 0)) {
							next = pieceClone(curr);
							next[j][l] = 1;
							
							for (int k = 0; k < newP.size(); k++) {
								if (same(newP.get(k), next))
									continue f;
							}
							newP.add(bound(next));
						}
								
					}
				}
			}
		}
		
		
		return newP;
		
	}
	
	public static byte[][] pieceClone(byte[][] piece) {
		byte[][] ret = new byte[piece.length][piece[0].length];
		
		for (int i = 0; i < piece.length; i++) {
			for (int j = 0; j < piece[i].length; j++) {
				ret[i][j] = piece[i][j];
			}
		}
		
		return ret;
	}
	
	public static void copyInto(byte[][] src, byte[][] to, int row, int col) {
		f: for (int i = 0; i < src.length; i++) {
			if (i + row > to.length)
				return;
			
			for (int j = 0; j < src[i].length; j++) {
				if (j + col > to[0].length)
					continue f;
				
				to[i + row][j + col] = src[i][j];
			}
		}
	}
	
	public static byte[][] bound(byte[][] piece) {
		int minI = piece.length;
		int minJ = piece[0].length;
		int maxI = -1;
		int maxJ = -1;
		
		for (int i = 0; i < piece.length; i++) {
			for (int j = 0; j < piece[i].length; j++) {
				if (piece[i][j] > 0) {
					if (i < minI)
						minI = i;
					else if (i > maxI)
						maxI = i;
					
					if (j < minJ)
						minJ = j;
					else if (j > maxJ)
						maxJ = j;
				}
			}
		}
		
		byte[][] ret = new byte[maxI - minI + 1][maxJ - minJ + 1];
		
		for (int i = minI; i <= maxI; i++) {
			for (int j = minJ; j <= maxJ; j++) {
				if (piece[i][j] > 0) {
					ret[i - minI][j - minJ] = piece[i][j];
				}
			}
		}
		
		return ret;
	}
	
	public static byte[][] pad(byte[][] piece) {
		byte[][] ret = new byte[piece.length + 2][piece[0].length + 2];
		
		for (int i = 0; i < piece.length; i++) {
			for (int j = 0; j < piece[i].length; j++) {
				if (piece[i][j] > 0) {
					ret[i + 1][j + 1] = piece[i][j];
				}
			}
		}
		
		return ret;
	}
	
	public static byte[][] rotatePiece(byte[][] piece) {
		byte[][] ret = new byte[piece[0].length][piece.length];
		
		for (int i = 0; i < piece.length; i++) {
			for (int j = 0; j < piece[i].length; j++) {
				if (piece[i][j] > 0) {
					ret[j][piece.length - 1 - i] = piece[i][j];
				}
			}
		}
		
		return ret;
	}
	
	public static boolean same(byte[][] p1, byte[][] p2) {
		p1 = bound(p1);
		p2 = bound(p2);
		
		f: for (int i = 0; i < 4; i++) {
			if (p1.length != p2.length || p1[0].length != p2[0].length) {
				p1 = rotatePiece(p1);
				continue;
			}
			
			for (int j = 0; j < p1.length; j++) {
				for (int l = 0; l < p1[0].length; l++) {
					if (p1[j][l] != p2[j][l]) {
						p1 = rotatePiece(p1);
						continue f;
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean drop(byte[][] board) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (x + xx < 0 || x + xx >= board[0].length || y + yy + 1 < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0) {
					if (y + yy + 1 >= board.length || board[y + yy + 1][x + xx] > 0)
						return false;
				}
			}
		}
		
		y++;
		
		return true;
	}
	
	public boolean hardDrop(byte[][] board) {
		while (true) {
			if (!drop(board))
				return false;
		}
	}
	
	
	public boolean moveRight(byte[][] board) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (x + xx + 1 < 0 || y + yy >= board.length || y + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0) {
					if (x + xx + 1 >= board[0].length || board[y + yy][x + xx + 1] > 0)
						return false;
				}
			}
		}
		
		x++;
		
		return true;
	}
	
	public boolean moveLeft(byte[][] board) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (x + xx - 1 >= board[0].length || y + yy >= board.length || y + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0) {
					if (x + xx - 1 < 0 || board[y + yy][x + xx - 1] > 0)
						return false;
				}
			}
		}
		
		x--;
		
		return true;
	}
	
	public boolean rotate(byte[][] board) {
		int newRot = (rotation + 1) % 4;
		
		int w = pieces.get(type)[newRot][0].length;
		int h = pieces.get(type)[newRot].length;
		
		int[] xshift = {0, 1, -1};
		int[] yshift = {0, -1, -2, 1};
		
		for (int xs = 0; xs < xshift.length; xs++) {
			fy: for (int ys = 0; ys < yshift.length; ys++) {
		
				for (int xx = 0; xx < w; xx++) {
					for (int yy = 0; yy < h; yy++) {
						
						if (pieces.get(type)[newRot][yy][xx] > 0) {
							if (x + xx + xshift[xs] < 0 || x + xx + xshift[xs] >= board[0].length || y + yy + yshift[ys] >= board.length || y + yy + yshift[ys] < 0)
								continue fy;
							
							if (board[y + yy + yshift[ys]][x + xx + xshift[xs]] > 0)
								continue fy;
						}
					}
				}
				
				rotation = newRot;
				x += xshift[xs];
				y += yshift[ys];
				
				return true;
			
			}
		}
		
		return false;
	}
	
	public void boardAdd(byte[][] board) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (x + xx < 0 || x + xx >= board[0].length || y + yy >= board.length || y + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0)
					board[y + yy][x + xx] = pieces.get(type)[rotation][yy][xx];
			}
		}
	}
	
	public void boardSub(byte[][] board) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (x + xx < 0 || x + xx >= board[0].length || y + yy >= board.length || y + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0)
					board[y + yy][x + xx] = 0;
			}
		}
	}
	
	public void boardAdd(byte[][] board, int newX, int newY) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (newX + xx < 0 || newX + xx >= board[0].length || newY + yy >= board.length || newY + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0)
					board[newY + yy][newX + xx] = pieces.get(type)[rotation][yy][xx];
			}
		}
	}
	
	public void boardSub(byte[][] board, int newX, int newY) {
		int w = pieces.get(type)[rotation][0].length;
		int h = pieces.get(type)[rotation].length;
		
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				if (newX + xx < 0 || newX + xx >= board[0].length || newY + yy >= board.length || newY + yy < 0)
					continue;
				
				if (pieces.get(type)[rotation][yy][xx] > 0)
					board[newY + yy][newX + xx] = 0;
			}
		}
	}
	
	
}
