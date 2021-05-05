import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application {

    TilePane piecesPane;
    Pane hoverPane, coverPane;
    Square hoverSquare;
    double hoverX, hoverY;
    boolean highlightedSquares[][];
    Square[][] square;
    Square lastSquare;
    int numMoves; //counts all half moves

    public void start(Stage primaryStage){

        hoverSquare = null;
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        square = parseFEN(fen); //rank-file
        hoverPane = new Pane();
        piecesPane = new TilePane();
        coverPane = new Pane();

        numMoves = 0;

        //drawing the board
        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                piecesPane.getChildren().add(square[i][j].piece.icon);
            }
        }

        TilePane board = new TilePane();
        board.setPrefColumns(8);
        board.setPrefRows(8);
        board.setPrefTileHeight(100);
        board.setPrefTileWidth(100);
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                board.getChildren().add(new Rectangle(100,100,square[i][j].colour));
            }
        }
//        for (int i = 0 ; i < 64 ; i++){
//            int evenRow;
//            Color c;
//            if ((i/8)%2==0) evenRow = 1;
//            else evenRow = 0;
//            if ((i+evenRow)%2 == 0) c = Color.CADETBLUE;
//            else c = Color.ALICEBLUE;
//            board.getChildren().add(new Rectangle(100,100,c));
//        }
        StackPane root = new StackPane();
        root.getChildren().addAll(board, piecesPane, hoverPane);

        root.setOnMousePressed(e -> {
            int mouseX = (int) (e.getX());
            int mouseY = (int) (e.getY());
            int rank = mouseY / 100;
            int file = mouseX / 100;
            if (square[rank][file].piece.type == 'x') return;
            if (square[rank][file].piece.colour == 'b' && numMoves%2==0) return;
            if (square[rank][file].piece.colour == 'w' && numMoves%2==1) return;
            lastSquare = square[rank][file];
            hoverSquare = new Square(rank, file, new Piece(square[rank][file].piece.type),false);
            hoverX = e.getX()-50;
            hoverY = e.getY()-50;
            hoverPane = new Pane();
            hoverPane.setTranslateX(hoverX);
            hoverPane.setTranslateY(hoverY);
            hoverPane.getChildren().add(hoverSquare.piece.icon);

            square[rank][file] = new Square(rank, file, new Piece('x'), true);
            coverPane = new Pane();
            coverPane.setTranslateX(100*file);
            coverPane.setTranslateY(100*rank);
            coverPane.getChildren().add(new Rectangle(100,100, square[rank][file].colour));
            root.getChildren().add(coverPane);
            root.getChildren().remove(hoverPane);
            root.getChildren().add(hoverPane);
        });

        root.setOnMouseDragged(e -> {
            if (hoverSquare == null) {
                return;
            }
            hoverX = (int)e.getX()-50 ;
            hoverY = (int)e.getY()-50;
            hoverPane = new Pane();
            hoverPane.setTranslateX(hoverX);
            hoverPane.setTranslateY(hoverY);
            hoverPane.getChildren().add(hoverSquare.piece.icon);
            root.getChildren().remove(hoverPane);
            root.getChildren().add(hoverPane);

        });

        root.setOnMouseReleased(e -> {
            if (hoverSquare == null) return;
            root.getChildren().remove(coverPane);
            int mouseX = (int) (e.getX());
            int mouseY = (int) (e.getY());
            int rank = mouseY / 100;
            int file = mouseX / 100;
            square[rank][file] = hoverSquare;
            if (square[rank][file]!=lastSquare) numMoves ++;
            hoverSquare = null;
            root.getChildren().remove(piecesPane);
            piecesPane = new TilePane();
            for (int i = 0 ; i <8; i++){
                for (int j = 0; j <8 ; j++){
                    piecesPane.getChildren().add(square[i][j].piece.icon);
                }
            }
            root.getChildren().add(piecesPane);
            root.getChildren().remove(hoverPane);
            root.getChildren().add(hoverPane);
        });



        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Square[][] parseFEN(String fen){
        Square[][] square = new Square[8][8];
        int rank, file;
        String[] split1, split2;
        split1 = fen.split(" ", 2);
        split2 = split1[0].split("/");

        for (rank = 0; rank < 8; rank++){
            file = 0;
            int fenMarker = 0;
            while (file < 8 ){ //&& fenMarker < split2[rank].length()
                char ch = split2[rank].charAt(fenMarker);
                if (Character.isDigit(ch)){
                    int num = Character.getNumericValue(ch);
                    for (int i = 0;i < num; i++){
                        square[rank][file] = new Square(rank, file, new Piece('x'), false);
                        file++;
                    }
                }
                else {
                    square[rank][file] = new Square(rank, file, new Piece(ch), false);
                    file++;
                }
                fenMarker ++;
            }
        }
        return square;
    }




}
