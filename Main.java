import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception{

        Image piecesImage = new Image("chesspieces.png");

        //rooks
        ImageView wRook = new ImageView(piecesImage);
        wRook.setPreserveRatio(true);
        wRook.setFitHeight(100);
        wRook.setFitWidth(100);
        wRook.setViewport(new Rectangle2D(0,0,132,132));
        ImageView bRook = new ImageView(piecesImage);
        bRook.setPreserveRatio(true);
        bRook.setFitHeight(100);
        bRook.setFitWidth(100);
        bRook.setViewport(new Rectangle2D(0,132,132,132));
        //knights
        ImageView wKnight = new ImageView(piecesImage);
        wKnight.setPreserveRatio(true);
        wKnight.setFitHeight(100);
        wKnight.setFitWidth(100);
        wKnight.setViewport(new Rectangle2D(132,0,132,132));
        ImageView bKnight = new ImageView(piecesImage);
        bKnight.setPreserveRatio(true);
        bKnight.setFitHeight(100);
        bKnight.setFitWidth(100);
        bKnight.setViewport(new Rectangle2D(132,132,132,132));
        //bishops
        ImageView wBishop = new ImageView(piecesImage);
        wBishop.setPreserveRatio(true);
        wBishop.setFitHeight(100);
        wBishop.setFitWidth(100);
        wBishop.setViewport(new Rectangle2D(264,0,132,132));
        ImageView bBishop = new ImageView(piecesImage);
        bBishop.setPreserveRatio(true);
        bBishop.setFitHeight(100);
        bBishop.setFitWidth(100);
        bBishop.setViewport(new Rectangle2D(264,132,132,132));
        //queens
        ImageView wQueen = new ImageView(piecesImage);
        wQueen.setPreserveRatio(true);
        wQueen.setFitHeight(100);
        wQueen.setFitWidth(100);
        wQueen.setViewport(new Rectangle2D(396,0,132,132));
        ImageView bQueen = new ImageView(piecesImage);
        bQueen.setPreserveRatio(true);
        bQueen.setFitHeight(100);
        bQueen.setFitWidth(100);
        bQueen.setViewport(new Rectangle2D(396,132,132,132));
        //kings
        ImageView wKing = new ImageView(piecesImage);
        wKing.setPreserveRatio(true);
        wKing.setFitHeight(100);
        wKing.setFitWidth(100);
        wKing.setViewport(new Rectangle2D(528,0,132,132));
        ImageView bKing = new ImageView(piecesImage);
        bKing.setPreserveRatio(true);
        bKing.setFitHeight(100);
        bKing.setFitWidth(100);
        bKing.setViewport(new Rectangle2D(528,132,132,132));
        //pawns
        ImageView wPawn = new ImageView(piecesImage);
        wPawn.setPreserveRatio(true);
        wPawn.setFitHeight(100);
        wPawn.setFitWidth(100);
        wPawn.setViewport(new Rectangle2D(660,0,132,132));
        ImageView bPawn = new ImageView(piecesImage);
        bPawn.setPreserveRatio(true);
        bPawn.setFitHeight(100);
        bPawn.setFitWidth(100);
        bPawn.setViewport(new Rectangle2D(660,132,132,132));
        //blank
        ImageView blankSpace = new ImageView(piecesImage);
        blankSpace.setPreserveRatio(true);
        blankSpace.setFitHeight(100);
        blankSpace.setFitWidth(100);
        blankSpace.setViewport(new Rectangle2D(0,0,1,1));




        TilePane piecesPane = new TilePane();
        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);

        piecesPane.getChildren().addAll(wRook, blankSpace, bRook, wKnight, bKnight, wBishop, bBishop, wQueen, bQueen, wKing, bKing, wPawn, bPawn );

        TilePane board = new TilePane();
        board.setPrefColumns(8);
        board.setPrefRows(8);
        board.setPrefTileHeight(100);
        board.setPrefTileWidth(100);
        for (int i = 0 ; i < 64 ; i++){
            int evenRow;
            Color c;
            if ((i/8)%2==0) evenRow = 1;
            else evenRow = 0;
            if ((i+evenRow)%2 == 0) c = Color.CADETBLUE;
            else c = Color.ALICEBLUE;
            board.getChildren().add(new Rectangle(100,100,c));
        }
        StackPane root = new StackPane();
        root.getChildren().addAll(board, piecesPane);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
