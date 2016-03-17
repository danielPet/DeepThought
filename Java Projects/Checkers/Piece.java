public class Piece {

	private boolean isFire;
	private Board b;
	private int x;
	private int y;
	private String type;

	private boolean isKing = false;
	private boolean isBomb;
	private boolean isShield;
	private boolean hasCaptured;

	public Piece(boolean isFire, Board b, int x, int y, String type) {
		this.isFire = isFire;
		this.b = b;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public boolean isFire() {
		return isFire;
	}

	public int side() {
		if (isFire == true) {
			return 0;
		}
		else {
			return 1;
	    }
	}

	public boolean isKing() {
		if (((isFire == true) && (this.y == 7)) || ((isFire == false) && (this.y == 0))){
			isKing = true;
		}
		return isKing;
	}

	public boolean isBomb() {
		if (this.type == "bomb") {
			isBomb = true;
		}
		else {
			isBomb = false;
		}
		return isBomb;
	}

	private void explosion(int x, int y) {
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				if (b.pieceAt(x+i, y+j) != null) {
					if (b.pieceAt(x+i, y+j).isShield() == false) {
						b.remove(x+i,y+j);
					}
				}
			}
		}
	}

	public boolean isShield() {
		if (this.type == "shield") {
			isShield = true;
		}
		else {
			isShield = false;
		}
		return isShield;
	}

	public void move(int x, int y) {
		int oldX = this.x;
		int oldY = this.y;
		this.x = x;
		this.y = y;
		b.remove(oldX,oldY);
		b.place(this,x,y);
		isKing();
		if ((Math.abs(oldX - x) == 2) && (Math.abs(oldY - y) == 2)) {
			hasCaptured = true;
		}
		if (hasCaptured == true) {
			if (oldX < x) {
				if (oldY < y) {
					b.remove(oldX+1, oldY+1);
				}
				else {
					b.remove(oldX+1, oldY-1);
				}
				
			}
			else if (oldX > x) {
				if (oldY < y) {
					b.remove(oldX-1, oldY+1);
				}
				else {
					b.remove(oldX-1, oldY-1);
				}
			}
			
			if (isBomb() == true) {
				System.out.println("BOOM!");
				explosion(x, y);
			}
		}

		
	}

	public boolean hasCaptured() {
		return hasCaptured;
	}

	public void doneCapturing() {
		hasCaptured = false;
	}
}