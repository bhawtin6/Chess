import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import static java.lang.Character.isLowerCase;

public class Piece {
    char colour; //'b' or 'w'
    char type; //rnbqkpx
    int turnLastMoved; //last turn that this piece has moved
    ImageView icon;

    public Piece(char ch){
        turnLastMoved = -1;
        int row;
        int col;
        if (isLowerCase(ch)){
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
                System.out.printf("ch = %c, making a rook\n", ch);
                col = 0;
                break;
            case 'n':
            case 'N':
                System.out.printf("ch = %c, making a knight\n", ch);
                col = 1;
                break;
            case 'b':
            case 'B':
                System.out.printf("ch = %c, making a bishop\n", ch);
                col = 2;
                break;
            case 'q':
            case 'Q':
                System.out.printf("ch = %c, making a queen\n", ch);
                col = 3;
                break;
            case 'k':
            case 'K':
                System.out.printf("ch = %c, making a king\n", ch);
                col = 4;
                break;
            case 'p':
            case 'P':
                System.out.printf("ch = %c, making a pawn\n", ch);
                col = 5;
                break;
            default:
                System.out.printf("ch = %c, making a blank space\n", ch);
                col = 6;//blank
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


}
