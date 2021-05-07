import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Square {
    public int rank;
    public int file;
    public Piece piece;
    public boolean selected;
    public boolean highlighted;
    Color colour; //backing colour of the square

    public Square(int r, int f){
        rank = r;
        file = f;
        piece = new Piece('x');
        highlighted = false;
        selected = false;
        updateColour();
    }
    public Square(int r, int f, Piece p, boolean h){
        rank = r;
        file = f;
        piece = p;
        highlighted = h;
        selected = false;
        updateColour();
    }
    public Color updateColour(){
        if ((rank+file)%2 == 0 ) colour = Color.ALICEBLUE;
        else colour = Color.CADETBLUE;
        if (highlighted){
            colour = colour.darker().darker();
        }
        return this.colour;
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

    public StackPane makeStackPane(){
        StackPane p = new StackPane();
        p.setPrefSize(100,100);
        Rectangle rect = new Rectangle(100,100,colour);
        p.getChildren().add(rect);
        p.getChildren().add(new Piece(this.piece.type).icon);
        return p;
    }
}
