import javafx.scene.paint.Color;

public class Square {
    public int rank;
    public int file;
    public Piece piece;
    public boolean selected;
    public boolean highlighted;
    public boolean lastMoved;
    public boolean lastMovedStart;
    Color colour; //backing colour of the square

    public Square(int r, int f){
        rank = r;
        file = f;
        piece = new Piece('x');
        highlighted = false;
        selected = false;
        lastMoved = false;
        lastMovedStart = false;
        updateColour();
    }
    public Square(int r, int f, Piece p, boolean h){
        rank = r;
        file = f;
        piece = p;
        highlighted = h;
        selected = false;
        lastMoved = false;
        lastMovedStart = false;
        updateColour();
    }
    public Color updateColour(){
        if (lastMoved || lastMovedStart){
            colour = Color.DARKSEAGREEN;
            if (lastMovedStart) colour = colour.desaturate().desaturate();
        }
        else {
            if ((rank + file) % 2 == 0) colour = Color.ALICEBLUE;
            else colour = Color.CADETBLUE;
        }
        if (highlighted){
            colour = colour.darker().darker();
        }
        return this.colour;
    }
    public void setLastMovedTrue(boolean b){
        lastMoved = b;
        updateColour();
    }
    public void setLastMovedStartTrue(boolean b){
        lastMovedStart = b;
        updateColour();
    }
    public void toggleHighlight(){
        highlighted = !highlighted;
        updateColour();
    }
    public void setHighlight(boolean b){

        highlighted = b;
        updateColour();
    }



    public boolean isEmpty(){
        return (this.piece.type == 'x');
    }


}
