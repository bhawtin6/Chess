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
    Pane hoverPane; //coverPane;
    Scene scene;

    Square hoverSquare;
    double hoverX, hoverY;

    Square[][] square;
    Square lastSquare;

    int numMoves, bkRank, bkFile, wkRank, wkFile; //counts all half moves
    boolean pieceInHand;
    HBox statsPanel;
    Label numMovesPlayed, colourToPlay;
    String nmp, ctp;

    String fen;

    public void start(Stage primaryStage){

        base = new BorderPane();

        initialize();
        setEventHandlers();
        base.setCenter(root);
        base.setStyle("-fx-background-color: #AAAAAA");
        base.setPadding(new Insets(15));
        updateStats();
        scene = new Scene(base);
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        root.requestFocus();
    }

    private void setEventHandlers() {
        root.setOnMouseClicked(this::piecePickerUpper);
//        root.setOnMousePressed(this::handleMouseDown);
//        root.setOnMouseDragged(this::handleMouseDrag);
//        root.setOnMouseReleased(this::handleMouseUp);
//        root.setOnKeyPressed(e-> reset());

    }

    private void piecePickerUpper(MouseEvent e) {
        if (pieceInHand){
            placePiece(e);
        }
        else {
            pickUpPiece(e);
        }

    }

    private void reset() {

    }

    private void placePiece(MouseEvent e) {


        int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;
        int oldRank = lastSquare.rank;
        int oldFile = lastSquare.file;


        boolean valid = false;
        if (rank < 8 && file < 8 && rank > -1 && file > -1){
            if (square[rank][file].highlighted) valid = true;
        }
        getMoves(lastSquare); //toggles highlights

        if (valid){
            square[lastSquare.rank][lastSquare.file] = new Square(rank, file, new Piece('x'), false);

            square[rank][file] = lastSquare;
        }

        if ((rank != lastSquare.rank || file != lastSquare.file) && valid) { //if the piece was moved, then
            square[rank][file].piece.turnLastMoved = numMoves;
            square[rank][file].rank = rank;
            square[rank][file].file = file;
            square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece('x'), false);
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

        redraw();
        updateStats();
        setEventHandlers();
        pieceInHand = !pieceInHand;
        if ((oppInCheck(numMoves % 2 == 0 ? 'b' : 'w'))) {
            base.setStyle("-fx-background-color: #FF0000");
        } else {
            base.setStyle("-fx-background-color: #AAAAAA");
        }
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
    private void pickUpPiece(MouseEvent e) {
        int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;
        if (square[rank][file].piece.type == 'x') return;
        if (square[rank][file].piece.colour == 'b' && numMoves%2==0) return;
        if (square[rank][file].piece.colour == 'w' && numMoves%2==1) return;

        lastSquare = square[rank][file];
        getMoves(lastSquare);

        redraw();
        setEventHandlers();
        pieceInHand = !pieceInHand;
    }
    private void initialize() {
        fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        //fen = "8/8/8/3bB3/8/8/8/8 u ";
        square = parseFEN(fen); //rank-file
        numMoves = 0;
        pieceInHand = false;
        redraw();


    }
    public Square[][] parseFEN(String fen){
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
                    if (ch == 'K'){
                        wkRank = rank;
                        wkFile = file;
                    }
                    else if (ch == 'k'){
                        bkRank = rank;
                        bkFile = file;
                    }
                    file++;
                }
                fenMarker ++;
            }
        }
        return square;
    }
    public void getMoves(Square s){

        Piece piece = s.piece;
        int rank = s.rank;
        int file = s.file;

        square[rank][file].toggleHighlight();

        if (piece.type == 'p' || piece.type == 'P'){
            int dir = 1;
            if (piece.colour == 'w') dir = -1;
            if ((rank>0 && piece.colour == 'w') || (rank < 7 && piece.colour == 'b')){
                if (square[rank+dir][file].isEmpty()){
                    square[rank+dir][file].toggleHighlight();
                    if (((rank>1 && piece.colour == 'w') || (rank < 6 && piece.colour == 'b')) && (piece.turnLastMoved == -1 || piece.turnLastMoved == numMoves)){
                        if (square[rank+2*dir][file].isEmpty()){
                            square[rank+2*dir][file].toggleHighlight();
                        }
                    }
                }
                if (file < 7){
                    if (!square[rank+dir][file+1].isEmpty() && square[rank+dir][file+1].piece.colour!=square[rank][file].piece.colour){
                        square[rank+dir][file+1].toggleHighlight();
                    }
                }
                if (file > 0){
                    if (!square[rank+dir][file-1].isEmpty() && square[rank+dir][file-1].piece.colour!=square[rank][file].piece.colour){
                        square[rank+dir][file-1].toggleHighlight();
                    }
                }


            }
        }

        if (piece.type == 'b' || piece.type == 'B' || piece.type == 'q' || piece.type == 'Q'){

            int iter;
            int rnk;
            int fle;

            iter = 1;
            rnk = rank - iter;
            fle = file - iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;
                square[rnk][fle].toggleHighlight();
                if (!square[rnk][fle].isEmpty()) break;

                iter ++;
                rnk = rank - iter;
                fle = file - iter;
            }
            iter = 1;
            rnk = rank - iter;
            fle = file + iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();

                if (!square[rnk][fle].isEmpty()) break;
                iter ++;
                rnk = rank - iter;
                fle = file + iter;
            }

            iter = 1;
            rnk = rank + iter;
            fle = file + iter;
            while (rnk <8 && rnk > -1 && fle <8 && fle > -1){

                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;

                square[rnk][fle].toggleHighlight();

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

                if (!square[rnk][fle].isEmpty()) break;
                iter ++;
                rnk = rank + iter;
                fle = file - iter;
            }
        }
        if (piece.type == 'r' || piece.type == 'R' || piece.type == 'q' || piece.type == 'Q'){

            int rnk = rank+1;
            int fle = file;
            while (rnk <8){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;
                square[rnk][fle].toggleHighlight();
                if (!square[rnk][fle].isEmpty()) break;
                rnk++;
            }

            rnk = rank-1;
            while (rnk > -1){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;
                square[rnk][fle].toggleHighlight();
                if (!square[rnk][fle].isEmpty()) break;
                rnk--;
            }

            rnk = rank;
            fle = file-1;
            while (fle > -1){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;
                square[rnk][fle].toggleHighlight();
                if (!square[rnk][fle].isEmpty()) break;
                fle--;
            }

            fle = file+1;
            while (fle <8){
                if (!square[rnk][fle].isEmpty() && square[rnk][fle].piece.colour == piece.colour) break;
                square[rnk][fle].toggleHighlight();
                if (!square[rnk][fle].isEmpty()) break;
                fle++;
            }
        }
        if (piece.type == 'k' || piece.type == 'K'){
            checkAndHighlight(rank-1, file-1, piece.colour);
            checkAndHighlight(rank-1, file, piece.colour);
            checkAndHighlight(rank-1, file+1, piece.colour);
            checkAndHighlight(rank, file-1, piece.colour);
            checkAndHighlight(rank, file+1, piece.colour);
            checkAndHighlight(rank+1, file-1, piece.colour);
            checkAndHighlight(rank+1, file, piece.colour);
            checkAndHighlight(rank+1, file+1, piece.colour);
        }
        if (piece.type == 'n' || piece.type == 'N'){
            checkAndHighlight(rank-2, file-1, piece.colour);
            checkAndHighlight(rank-2, file+1, piece.colour);
            checkAndHighlight(rank-1, file-2, piece.colour);
            checkAndHighlight(rank-1, file+2, piece.colour);
            checkAndHighlight(rank+1, file+2, piece.colour);
            checkAndHighlight(rank+1, file-2, piece.colour);
            checkAndHighlight(rank+2, file-1, piece.colour);
            checkAndHighlight(rank+2, file+1, piece.colour);
        }
    }
    public boolean checkAndHighlight(int r, int f, char c){
        if (r < 0 || r > 7 || f < 0 || f > 7) return false;
        if (square[r][f].piece.colour == c) return false;

        square[r][f].toggleHighlight();
        return true;
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

    public boolean oppInCheck(char c){ //char represents colour that just moved
        boolean result;
        if (c == 'b'){
            for (int i = 0 ; i <8; i++){
                for (int j = 0; j <8 ; j++){
                    if (square[i][j].piece.colour == 'b'){
                        getMoves(square[i][j]);
                        result = square[wkRank][wkFile].highlighted == true;
                        getMoves(square[i][j]);
                        if (result) return true;
                    }
                }
            }
        }
        else if (c == 'w'){
            System.out.println("looking foor checks against the black king");
            System.out.printf("black king located at %d, %d.\n", bkRank, bkFile);
            for (int i = 0 ; i <8; i++){
                for (int j = 0; j <8 ; j++){
                    if (square[i][j].piece.colour == 'w'){
                        getMoves(square[i][j]);
                        result = square[bkRank][bkFile].highlighted;
                        getMoves(square[i][j]);
                        if (result) return true;
                    }
                }
            }
        }
        return false;
    }

    public void redraw(){
        makePieces();
        makeBoard();
        root = new StackPane();
        root.setMaxSize(800,800);
        root.getChildren().add(board);
        root.getChildren().add(piecesPane);
        base.setCenter(root);
        root.requestFocus();
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
        piecesPane = new TilePane();
        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);

        System.out.println();
        for (int i = 0 ; i <8; i++){
            for (int j = 0; j <8 ; j++){

                piecesPane.getChildren().add(square[i][j].piece.icon);
                System.out.print(square[i][j].piece.type + " ");

            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
