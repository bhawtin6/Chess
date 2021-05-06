import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    TilePane piecesPane, board;
    BorderPane base;
    StackPane root;
    Pane hoverPane, movePane, coverPane;
    Scene scene;

    Square hoverSquare;
    double hoverX, hoverY;

    Square[][] square;
    Square lastSquare;

    int numMoves; //counts all half moves
    HBox statsPanel;
    Label numMovesPlayed, colourToPlay;
    String nmp, ctp;

    String fen;

    public void start(Stage primaryStage){
        initialize();
        setEventHandlers();
        scene = new Scene(base);
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        root.requestFocus();
    }

    private void setEventHandlers() {
        root.setOnMousePressed(this::handleMouseDown);
        root.setOnMouseDragged(this::handleMouseDrag);
        root.setOnMouseReleased(this::handleMouseUp);
        root.setOnKeyPressed(e-> reset());
    }

    private void reset() {

    }

    private void handleMouseUp(MouseEvent e) {

        if (hoverSquare == null) return;

        int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;

        boolean valid = false;
        if (square[rank][file].highlighted) valid = true;
        getMoves(lastSquare); //toggles highlights

        if (valid){
            square[lastSquare.rank][lastSquare.file] = new Square(rank, file, new Piece('x'), false);
            square[rank][file] = hoverSquare;
        }

        if ((rank != lastSquare.rank || file != lastSquare.file) && valid) { //if the piece was moved, then
            square[rank][file].piece.turnLastMoved = numMoves;
            square[rank][file].rank = rank;
            square[rank][file].file = file;
            numMoves ++;
        }

        hoverSquare = null;
        root.getChildren().remove(piecesPane);
        root.getChildren().remove(hoverPane);
        piecesPane = new TilePane();
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                piecesPane.getChildren().add(square[i][j].piece.icon);
                square[i][j].updateColour();
            }
        }

        root.getChildren().add(movePane);
        root.getChildren().add(piecesPane);
        updateStats();

    }

    private void handleMouseDrag(MouseEvent e) {
        if (hoverSquare == null) {
            return;
        }
        hoverX = (int)e.getX()-50 ;
        hoverY = (int)e.getY()-50;
        hoverPane.setTranslateX(hoverX);
        hoverPane.setTranslateY(hoverY);
    }

    private void handleMouseDown(MouseEvent e) {
        int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;
        if (square[rank][file].piece.type == 'x') return;
        if (square[rank][file].piece.colour == 'b' && numMoves%2==0) return;
        if (square[rank][file].piece.colour == 'w' && numMoves%2==1) return;

        lastSquare = square[rank][file];
        getMoves(lastSquare);

        hoverSquare = new Square(rank, file, new Piece(square[rank][file].piece.type),false);
        hoverX = e.getX()-50;
        hoverY = e.getY()-50;
        hoverPane = new Pane();
        hoverPane.setTranslateX(hoverX);
        hoverPane.setTranslateY(hoverY);
        hoverPane.getChildren().add(hoverSquare.piece.icon);
        root.getChildren().remove(movePane);
        root.getChildren().add(movePane);
        root.getChildren().remove(hoverPane);
        root.getChildren().add(hoverPane);

    }

    private void initialize() {
        //fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        fen = "8/8/8/3bB3/8/8/8/8 u ";
        square = parseFEN(fen); //rank-file
        hoverSquare = null;
        hoverPane = new Pane();
        piecesPane = new TilePane();
        coverPane = new Pane();
        numMoves = 0;

        redraw();

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

    public void getMoves(Square s){

        movePane = new Pane();
        movePane.setPrefSize(800,800);
        movePane.setMinSize(800,800);

        Piece piece = s.piece;
        int rank = s.rank;
        int file = s.file;

        square[rank][file].toggleHighlight();
        Rectangle tempx = new Rectangle(100,100,square[rank][file].colour);
        tempx.setTranslateX(100*file);
        tempx.setTranslateY(100*(rank));
        movePane.getChildren().add(tempx);

        if (piece.type == 'p' || piece.type == 'P'){
            int dir = 1;
            if (piece.colour == 'w') dir = -1;
            if ((rank>0 && piece.colour == 'w') || (rank < 7 && piece.colour == 'b')){
                if (square[rank+dir][file].isEmpty()){
                    square[rank+dir][file].toggleHighlight();
                    StackPane temp = square[rank+dir][file].makeStackPane();
                    temp.setTranslateX(100*file);
                    temp.setTranslateY(100*(rank+dir));
                    movePane.getChildren().add(temp);
                }
                if (file < 7){
                    if (!square[rank+dir][file+1].isEmpty() && square[rank+dir][file+1].piece.colour!=square[rank][file].piece.colour){
                        square[rank+dir][file+1].toggleHighlight();
                        StackPane temp = square[rank+dir][file+1].makeStackPane();
                        temp.setTranslateX(100*(file+1));
                        temp.setTranslateY(100*(rank+dir));
                        movePane.getChildren().add(temp);
                    }
                }
                if (file > 0){
                    if (!square[rank+dir][file-1].isEmpty() && square[rank+dir][file-1].piece.colour!=square[rank][file].piece.colour){
                        square[rank+dir][file-1].toggleHighlight();
                        StackPane temp = square[rank+dir][file-1].makeStackPane();
                        temp.setTranslateX(100*(file-1));
                        temp.setTranslateY(100*(rank+dir));
                        movePane.getChildren().add(temp);
                    }
                }

                if (((rank>1 && piece.colour == 'w') || (rank < 6 && piece.colour == 'b')) && (piece.turnLastMoved == -1 || piece.turnLastMoved == numMoves)){
                    if (square[rank+2*dir][file].isEmpty()){
                        square[rank+2*dir][file].toggleHighlight();
                        StackPane temp = square[rank+2*dir][file].makeStackPane();
                        temp.setTranslateX(100*file);
                        temp.setTranslateY(100*(rank+2*dir));
                        movePane.getChildren().add(temp);
                    }
                }
            }

        }

        else if (piece.type == 'b' || piece.type == 'B'){

            int iter;
            int rnk;
            int fle;

            iter = 1;
            rnk = rank - iter;
            fle = file - iter;
            System.out.println("\n");
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){
                System.out.printf("%d %d -> ", rnk, fle);
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();
                StackPane temp = square[rnk][fle].makeStackPane();
                temp.setTranslateX(100*fle);
                temp.setTranslateY(100*rnk);
                movePane.getChildren().add(temp);

                if (!square[rnk][fle].isEmpty()) break;

                iter ++;
                rnk = rank - iter;
                fle = file - iter;
                System.out.printf("%d %d\n", rnk, fle);
            }
            iter = 1;
            rnk = rank - iter;
            fle = file + iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){
                System.out.printf("%d %d -> ", rnk, fle);
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();
                StackPane temp = square[rnk][fle].makeStackPane();
                temp.setTranslateX(100*fle);
                temp.setTranslateY(100*rnk);
                movePane.getChildren().add(temp);

                if (!square[rnk][fle].isEmpty()) break;
                iter ++;
                rnk = rank - iter;
                fle = file + iter;
                System.out.printf("%d %d\n", rnk, fle);
            }

            iter = 1;
            rnk = rank + iter;
            fle = file + iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){

                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();
                StackPane temp = square[rnk][fle].makeStackPane();
                temp.setTranslateX(100*fle);
                temp.setTranslateY(100*rnk);
                movePane.getChildren().add(temp);

                if (!square[rnk][fle].isEmpty()) break;
                iter ++;
                rnk = rank + iter;
                fle = file + iter;
            }
            iter = 1;
            rnk = rank + iter;
            fle = file - iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){

                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();
                StackPane temp = square[rnk][fle].makeStackPane();
                temp.setTranslateX(100*fle);
                temp.setTranslateY(100*rnk);
                movePane.getChildren().add(temp);

                if (!square[rnk][fle].isEmpty()) break;
                iter ++;
                rnk = rank + iter;
                fle = file - iter;
            }

        }

    }
    public void updateStats(){
        statsPanel = new HBox();
        nmp = String.format("Moves Played: %d", numMoves);
        ctp = numMoves%2==0? "White To Play" : "Black To Play";
        numMovesPlayed = new Label(nmp);
        colourToPlay = new Label (ctp);
        numMovesPlayed.setFont(new Font(30));
        numMovesPlayed.setPadding(new Insets(15,30,0,30));
        colourToPlay.setFont(new Font(30));
        colourToPlay.setPadding(new Insets(15,30,0,30));
        statsPanel.getChildren().removeAll();
        statsPanel.getChildren().addAll(numMovesPlayed, colourToPlay);
        statsPanel.setPrefHeight(30);
        statsPanel.setAlignment(Pos.CENTER);
        base.getChildren().removeAll();
        base.setBottom(statsPanel);
    }

    public void redraw(){
        makePieces();
        makeBoard();
        root = new StackPane();
        root.setMaxSize(800,800);
        root.getChildren().addAll(board, piecesPane, hoverPane);

        base = new BorderPane();
        base.setStyle("-fx-background-color: #AAAAAA");
        base.setCenter(root);
        base.setPadding(new Insets(15));

        updateStats();
    }

    public void makeBoard(){
        board = new TilePane();
        board.setPrefColumns(8);
        board.setPrefRows(8);
        board.setPrefTileHeight(100);
        board.setPrefTileWidth(100);
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                board.getChildren().add(new Rectangle(100,100,square[i][j].colour));
            }
        }
    }

    public void makePieces(){
        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){
                piecesPane.getChildren().add(square[i][j].piece.icon);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
