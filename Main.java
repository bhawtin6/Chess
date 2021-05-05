import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application {

    TilePane piecesPane;
    Pane hoverPane, coverPane;
    Piece hoverPiece;
    double hoverX, hoverY;
    boolean highlightedSquares[][];
    Piece[][] pieces;

    public void start(Stage primaryStage){

        hoverPiece = null;
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        pieces = parseFEN(fen); //rank-file
        hoverPane = new Pane();
        piecesPane = new TilePane();
        coverPane = new Pane();
        
        //drawing the board

        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                piecesPane.getChildren().add(pieces[i][j].icon);
            }
        }

        //highlightedSquares = getHighlightedSquares(hoverPiece, pieces);
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
        root.getChildren().addAll(board, piecesPane, hoverPane);

        root.setOnMousePressed(e -> {
            int mouseX = (int) (e.getX());
            int mouseY = (int) (e.getY());
            int rank = mouseY / 100;
            int file = mouseX / 100;
            System.out.printf("clicked %c on rank %d, file %d\n", pieces[rank][file].type, rank, file);
            if (pieces[rank][file].type == 'x') return;
            hoverPiece = new Piece(pieces[rank][file].type);
            hoverX = e.getX()-50;
            hoverY = e.getY()-50;
            hoverPane = new Pane();
            hoverPane.setTranslateX(hoverX);
            hoverPane.setTranslateY(hoverY);
            hoverPane.getChildren().add(hoverPiece.icon);


            //redraw board
            pieces[rank][file] = new Piece('x');
            coverPane = new Pane();
            coverPane.setTranslateX(100*file);
            coverPane.setTranslateY(100*rank);
            if ((rank+file)%2==0) coverPane.getChildren().add(new Rectangle(100,100, Color.ALICEBLUE));
            else coverPane.getChildren().add(new Rectangle(100,100, Color.CADETBLUE));
            root.getChildren().add(coverPane);
            root.getChildren().remove(hoverPane);
            root.getChildren().add(hoverPane);
        });

        root.setOnMouseDragged(e -> {
            if (hoverPiece == null) {
                System.out.println("Null Drag");
                return;
            }
            hoverX = (int)e.getX()-50 ;
            hoverY = (int)e.getY()-50;
            hoverPane = new Pane();
            System.out.printf("dragging... X: %f, Y:%f\r", hoverX, hoverY);
            hoverPane.setTranslateX(hoverX);
            hoverPane.setTranslateY(hoverY);
            hoverPane.getChildren().add(hoverPiece.icon);
            root.getChildren().remove(hoverPane);
            root.getChildren().add(hoverPane);

        });

        root.setOnMouseReleased(e -> {
            if (hoverPiece == null) return;
            int mouseX = (int) (e.getX());
            int mouseY = (int) (e.getY());
            int rank = mouseY / 100;
            int file = mouseX / 100;
            pieces[rank][file] = hoverPiece;
            hoverPiece = null;
            root.getChildren().remove(piecesPane);
            piecesPane = new TilePane();
            for (int i = 0 ; i <8; i++){
                for (int j = 0; j <8 ; j++){
                    piecesPane.getChildren().add(pieces[i][j].icon);
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

    public static Piece[][] parseFEN(String fen){
        Piece[][] pieces = new Piece[8][8];
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
                        pieces[rank][file] = new Piece('x');
                        file++;
                    }
                }
                else {
                    pieces[rank][file] = new Piece(ch);
                    file++;
                }
                fenMarker ++;
            }
        }
        return pieces;
    }




}
