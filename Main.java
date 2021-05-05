import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application {

    Piece curPiece; //holds currently selected piece, so that we know to deselect it when a new one is clicked

    public void start(Stage primaryStage){

        curPiece = null;
        Piece hoverPiece = new Piece('x');
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Piece[][] pieces = parseFEN(fen); //rank-file
        boolean highlightedSquares[][];
        //Piece curPiece; //holds currently selected piece, so that we know to deselect it when a new one is clicked

        EventHandler<MouseEvent> clickHandler = e -> {
            int mouseX = (int)Math.floor(e.getX());
            int mouseY = (int)Math.floor(e.getY());
            int rank = mouseY/100;
            int file = mouseX/100;
            System.out.printf("clicked %c on rank %d, file %d\n", pieces[rank][file].type, rank, file);
            if (curPiece != null) curPiece.highlighted = false;
            if (curPiece != pieces[rank][file]){
                curPiece = pieces[rank][file];
                if (curPiece.type != 'x') curPiece.highlighted = true;
            }

        };

        
        //drawing the board
        TilePane piecesPane = new TilePane();
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
        root.getChildren().addAll(board, piecesPane);
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
//        root.setOnMousePressed(e -> {
//
//        });


        Scene scene = new Scene(root);
        primaryStage.setTitle("Chess");
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
