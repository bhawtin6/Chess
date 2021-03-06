import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import static java.lang.Character.isLowerCase;

public class Piece {
    char colour; //'b' or 'w' or 'x' for blank
    char type; //rnbqkpx
    int turnLastMoved; //last turn that this piece has moved
    ImageView icon;

    public Piece(char ch){
        turnLastMoved = -1;
        int row;
        int col;
        if (ch == 'X' || ch == 'x'){
            colour = 'x';
            row = 1;
        }
        else if (isLowerCase(ch)){
            colour = 'b';
            row = 1;
        }
        else {
            colour = 'w';
            row = 0;
        }
        switch (ch){
            case 'r':
            case 'R':
                col = 0;
                break;
            case 'n':
            case 'N':
                col = 1;
                break;
            case 'b':
            case 'B':
                col = 2;
                break;
            case 'q':
            case 'Q':
                col = 3;
                break;
            case 'k':
            case 'K':
                col = 4;
                break;
            case 'p':
            case 'P':
                col = 5;
                break;
            default:
                col = 6;//blank
                turnLastMoved = -2;
        }
        icon = new ImageView(new Image("chesspieces.png"));
        icon.setPreserveRatio(true);
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        if ( col == 6){
            icon.setViewport(new Rectangle2D(0,0,1,1));
            type = 'x';
            return;
        }
        type = ch;
        icon.setViewport(new Rectangle2D(col*132,row*132,132,132));
    }
    public String toString(){
        return String.format("type: %c, colour: %c, turnLastMoved: %d",this.type, this.colour, this.turnLastMoved);
    }


}
