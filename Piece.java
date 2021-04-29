import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;

public class Piece {
    char colour; //'b' or 'w'
    int turnLastMoved; //last turn that this piece has moved

    public Piece(){
        ImageView icon = new ImageView(new Image("chesspieces.png"));
        icon.setPreserveRatio(true);
        icon.setFitHeight(100);
        icon.setFitWidth(100);
        icon.setViewport(new Rectangle2D(0,0,132,132));
    }


}
