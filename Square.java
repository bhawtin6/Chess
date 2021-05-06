import javafx.scene.paint.Color;

public class Square {
    public int rank;
    public int file;
    public Piece piece;
    public boolean highlighted;
    Color colour; //backing colour of the square

    public Square(int r, int f){
        rank = r;
        file = f;
        piece = new Piece('x');
        highlighted = false;
        updateColour();
    }
    public Square(int r, int f, Piece p, boolean h){
        rank = r;
        file = f;
        piece = p;
        highlighted = h;
        updateColour();
    }
    public Color updateColour(){
        if ((rank+file)%2 == 0 ) colour = Color.ALICEBLUE;
        else colour = Color.CADETBLUE;
        if (highlighted){
            colour = colour.darker();
        }
        return this.colour;
    }
    public Color toggleHighlight(){
        highlighted = !highlighted;
        updateColour();
        return this.colour;
    }
    public boolean isEmpty(){
        return (this.piece.type == 'x');
    }
}
