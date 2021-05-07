import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    TilePane piecesPane, board;
    BorderPane base;
    StackPane root;
    Pane hoverPane, colourPane; //coverPane;
    Scene scene;

    Square hoverSquare;
    double hoverX, hoverY;

    Square[][] square;
    Square lastSquare;

    int numMoves; //counts all half moves
    int bkRank, bkFile, wkRank, wkFile;//track pos of black and white kings
    int lmRank, lmFile, lmsRank, lmsFile;//tracks squares highlighted
    boolean pieceInHand;
    HBox statsPanel;
    Label numMovesPlayed, colourToPlay;
    String nmp, ctp;
    boolean checkOnBoard;
    String fen;

    public void start(Stage primaryStage) {

        base = new BorderPane();

        initialize();
        setEventHandlers();
        base.setCenter(root);
        base.setStyle("-fx-background-color: #226622");
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
    }

    private void piecePickerUpper(MouseEvent e) {
        if (pieceInHand) {
            placePiece(e);
        } else {
            pickUpPiece(e);
        }
    }

    private void placePiece(MouseEvent e) {


        int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;
        int oldRank = lastSquare.rank;
        int oldFile = lastSquare.file;

        boolean valid = false;
        if (rank < 8 && file < 8 && rank > -1 && file > -1) {
            if (square[rank][file].highlighted) valid = true;
        }
        //toggles highlights
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                square[i][j].setHighlight(false);
            }
        }

        if (valid) {
            square[lastSquare.rank][lastSquare.file] = new Square(rank, file, new Piece('x'), false);
            square[rank][file] = lastSquare;
        }

        if ((rank != lastSquare.rank || file != lastSquare.file) && valid) { //if the piece was moved, then
            square[rank][file].piece.turnLastMoved = numMoves;
            square[rank][file].rank = rank;
            square[rank][file].file = file;
            square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece('x'), false);
            if (square[rank][file].piece.type == 'k') {
                bkFile = file;
                bkRank = rank;
                if (file - oldFile == 2) {
                    square[rank][5] = square[rank][7];
                    square[rank][5].file = 5;
                    square[rank][7] = new Square(rank, 7, new Piece('x'), false);
                }
                else if (file - oldFile == -2){
                    square[rank][3] = square[rank][0];
                    square[rank][3].file = 3;
                    square[rank][3].updateColour();
                    square[rank][0] = new Square(rank, 0, new Piece('x'), false);
                }
            } else if (square[rank][file].piece.type == 'K') {
                wkFile = file;
                wkRank = rank;
                if (file - oldFile == 2) {
                    square[rank][5] = square[rank][7];
                    square[rank][5].file = 5;
                    square[rank][7] = new Square(rank, 7, new Piece('x'), false);
                }
                else if (file - oldFile == -2){
                    square[rank][3] = square[rank][0];
                    square[rank][3].file = 3;
                    square[rank][3].updateColour();
                    square[rank][0] = new Square(rank, 0, new Piece('x'), false);

                }
            } else if (square[rank][file].piece.type == 'p' && rank == 7){
                square[rank][file] = new Square(rank, file, new Piece('q'), false);
            } else if (square[rank][file].piece.type == 'P' && rank == 0){
                square[rank][file] = new Square(rank, file, new Piece('Q'), false);
            }
            updateLastMove(rank, file, oldRank, oldFile);
            if (square[rank][file].piece.type == 'p'){
                square[rank-1][file] = new Square(rank -1, file, new Piece('x'), false);
            }else if (square[rank][file].piece.type == 'P'){
                square[rank+1][file] = new Square(rank +1, file, new Piece('x'), false);
            }
            numMoves++;
        }

        hoverSquare = null;
        root.getChildren().remove(piecesPane);
        root.getChildren().remove(hoverPane);
        piecesPane = new TilePane();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                piecesPane.getChildren().add(square[i][j].piece.icon);
                square[i][j].updateColour();
            }
        }

        redraw();
        updateStats();
        setEventHandlers();
        pieceInHand = !pieceInHand;
        if ((oppInCheck(numMoves % 2 == 0 ? 'b' : 'w'))) {
            checkOnBoard = true;
            base.setStyle("-fx-background-color: #FF0000");
        } else {
            checkOnBoard = false;
            base.setStyle("-fx-background-color: #226622");
        }
    }

    private void pickUpPiece(MouseEvent e) {
                int mouseX = (int) (e.getX());
        int mouseY = (int) (e.getY());
        int rank = mouseY / 100;
        int file = mouseX / 100;
        if (square[rank][file].piece.type == 'x') return;
        if (square[rank][file].piece.colour == 'b' && numMoves % 2 == 0) return;
        if (square[rank][file].piece.colour == 'w' && numMoves % 2 == 1) return;

        lastSquare = square[rank][file];
        getMoves(lastSquare);

        redraw();
        setEventHandlers();
        pieceInHand = !pieceInHand;

    }

    private void initialize() {
        //fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        //fen = "8/8/8/3bB3/8/8/8/8 u ";
        fen = "n2r3k/4p3/8/8/3P4/8/8/3K3N w - - 0 1";
        square = parseFEN(fen); //rank-file
        numMoves = 0;
        pieceInHand = false;
        checkOnBoard = false;
        lmRank=-1;
        lmFile=-1;
        lmsRank=-1;
        lmsFile=-1;
        redraw();


    }

    public Square[][] parseFEN(String fen) {
        Square[][] square = new Square[8][8];
        int rank, file;
        String[] split1, split2;
        split1 = fen.split(" ", 2);
        split2 = split1[0].split("/");

        for (rank = 0; rank < 8; rank++) {
            file = 0;
            int fenMarker = 0;
            while (file < 8) { //&& fenMarker < split2[rank].length()
                char ch = split2[rank].charAt(fenMarker);
                if (Character.isDigit(ch)) {
                    int num = Character.getNumericValue(ch);
                    for (int i = 0; i < num; i++) {
                        square[rank][file] = new Square(rank, file, new Piece('x'), false);
                        file++;
                    }
                } else {
                    square[rank][file] = new Square(rank, file, new Piece(ch), false);
                    if (ch == 'K') {
                        wkRank = rank;
                        wkFile = file;
                    } else if (ch == 'k') {
                        bkRank = rank;
                        bkFile = file;
                    }
                    file++;
                }
                fenMarker++;
            }
        }
        return square;
    }

    public void getMoves(Square s) {

        Piece piece = s.piece;
        int rank = s.rank;
        int file = s.file;

        if (piece.type == 'p' || piece.type == 'P') {
            int dir = 1;
            if (piece.colour == 'w') dir = -1;
            checkAndHighlight(rank + dir, file, s);
            if (piece.turnLastMoved == -1 && square[rank+dir][file].isEmpty()) {
                checkAndHighlight(rank + 2 * dir, file, s);
            }
            checkAndHighlight(rank + dir, file + 1, s);
            checkAndHighlight(rank + dir, file - 1, s);



            //}
        }
        if (piece.type == 'b' || piece.type == 'B' || piece.type == 'q' || piece.type == 'Q') {

            int iter;
            int rnk;
            int fle;

            iter = 1;
            rnk = rank - iter;
            fle = file - iter;
            while (rnk > -1 && fle > -1) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                iter++;
                rnk = rank - iter;
                fle = file - iter;
            }
            iter = 1;
            rnk = rank - iter;
            fle = file + iter;
            while (rnk > -1 && fle < 8) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                iter++;
                rnk = rank - iter;
                fle = file + iter;
            }

            iter = 1;
            rnk = rank + iter;
            fle = file + iter;
            while (rnk < 8 && fle < 8) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                iter++;
                rnk = rank + iter;
                fle = file + iter;
            }
            iter = 1;
            rnk = rank + iter;
            fle = file - iter;
            while (rnk < 8 && fle > -1) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                iter++;
                rnk = rank + iter;
                fle = file - iter;
            }
        }
        if (piece.type == 'r' || piece.type == 'R' || piece.type == 'q' || piece.type == 'Q') {
            int rnk = rank + 1;
            int fle = file;
            while (rnk<8) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                rnk++;
            }
            rnk = rank - 1;
            while (rnk>-1) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                rnk--;
            }
            rnk = rank;
            fle = file - 1;
            while (fle>-1) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                fle--;
            }
            fle = file + 1;
            while (fle<8) {
                checkAndHighlight(rnk, fle, s);
                if (!square[rnk][fle].isEmpty()) break;
                fle++;
            }
        }
        if (piece.type == 'k' || piece.type == 'K') {
            checkAndHighlight(rank - 1, file - 1, s);
            checkAndHighlight(rank - 1, file, s);
            checkAndHighlight(rank - 1, file + 1, s);
            if (checkAndHighlight(rank, file - 1, s)){
               if (piece.turnLastMoved==-1 && square[rank][0].piece.turnLastMoved == -1 && !checkOnBoard){ //allows caslting
                   if(square[rank][1].isEmpty() && square[rank][2].isEmpty()){
                        checkAndHighlight(rank,file-2,s);
                   }
               }
            }
            if (checkAndHighlight(rank, file + 1, s)){
                if (piece.turnLastMoved==-1 && square[rank][7].piece.turnLastMoved == -1 && !checkOnBoard){
                    if(square[rank][6].isEmpty()){
                        checkAndHighlight(rank,file+2,s);
                    }
                }
            }
            checkAndHighlight(rank + 1, file - 1, s);
            checkAndHighlight(rank + 1, file, s);
            checkAndHighlight(rank + 1, file + 1, s);
        }
        if (piece.type == 'n' || piece.type == 'N') {
            checkAndHighlight(rank - 2, file - 1, s);
            checkAndHighlight(rank - 2, file + 1, s);
            checkAndHighlight(rank - 1, file - 2, s);
            checkAndHighlight(rank - 1, file + 2, s);
            checkAndHighlight(rank + 1, file + 2, s);
            checkAndHighlight(rank + 1, file - 2, s);
            checkAndHighlight(rank + 2, file - 1, s);
            checkAndHighlight(rank + 2, file + 1, s);
        }
        square[rank][file].toggleHighlight();

    }

    public boolean checkAndHighlight(int r, int f, Square s) { //r and f describe the target square, s is the square of the piece trying to be moved
        boolean tryingEnPassent = false;
        boolean good = false;
        char myCol = s.piece.colour;
        char oppCol = myCol == 'w' ? 'b' : 'w';
        if (r < 0 || r > 7 || f < 0 || f > 7) return false; //oob checks
        if (square[r][f].piece.colour == myCol) return false; //cant take own colour
        if (s.piece.type == 'p' || s.piece.type == 'P') { //pawns
            if (!square[r][f].isEmpty() && s.file == f) return false; //pawns cant take in the same file

            if (s.file != f && square[r][f].isEmpty()) {
                if (myCol == 'w'){
                    if (r == 2 && square[1][f].lastMovedStart && square[r+1][f].piece.type == 'p' && square[r+1][f].lastMoved){
                        tryingEnPassent = true;
                        System.out.println("en passent found");
                    }
                    else return false;
                }
                else {
                    if (r == 5 && square[6][f].lastMovedStart && square[r-1][f].piece.type == 'P' && square[r-1][f].lastMoved){
                        tryingEnPassent = true;
                        System.out.println("en passent found");
                    }
                    else return false;
                }

            }
        }

        //this chunk determines if the move puts themselves in check
        if ((numMoves % 2 == 0 && myCol == 'w') || (numMoves % 2 == 1 && myCol == 'b')) {
            Square sTaken = square[r][f]; //sTaken is the square that is temporarily being replaced
            int oldRank = s.rank; //so we know where to put the square back to later
            int oldFile = s.file;

            //note that we dont need to do any special check for castling, since there is no situation
            //where the castling rook is protecting from check before moving.
            if (s.piece.type == 'k' || s.piece.type == 'K'){
                if (s.piece.type == 'K') {
                    wkRank = r;
                    wkFile = f;
                } else {
                    bkRank = r;
                    bkFile = f;
                }

                Square temp = square[r][f];
                square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece('x'), false);
                square[r][f] = new Square(r, f, new Piece(s.piece.type), false);

                if (!oppInCheck(oppCol)) good = true;

                if (s.piece.type == 'K') {
                    wkRank = oldRank;
                    wkFile = oldFile;
                } else if (s.piece.type == 'k') {
                    bkRank = oldRank;
                    bkFile = oldFile;
                }
                square[r][f] = temp;
                square[oldRank][oldFile] = s;

                if (good) {
                    square[r][f].toggleHighlight();
                    return true;
                } else return false;

            }

            Square epPawnHolder=null;
            int epphRank=0;
            int epphFile=0;
            if (tryingEnPassent){
                epPawnHolder = square[s.rank][f];
                epphRank = epPawnHolder.rank;
                epphFile = epPawnHolder.file;
                square[s.rank][f] = new Square(r, f, new Piece('x'),false);
            }

            square[r][f] = s; //place the square at the prospective location
            square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece('x'), false); //replace old with temp blank

            if (!oppInCheck(oppCol)) good = true;

            square[oldRank][oldFile] = square[r][f];
            square[r][f] = sTaken;
            if (tryingEnPassent){
                square[epphRank][epphFile] = epPawnHolder;
            }



            if (good) {
                square[r][f].toggleHighlight();

                return true;
            } else return false;
        }

        square[r][f].toggleHighlight();

        return true;

    }

    public void updateStats() {
        statsPanel = new HBox();
        nmp = String.format("Moves Played: %d", numMoves);
        ctp = numMoves % 2 == 0 ? "White To Play" : "Black To Play";
        numMovesPlayed = new Label(nmp);
        colourToPlay = new Label(ctp);
        numMovesPlayed.setStyle("-fx-text-fill: #dddddd");
        colourToPlay.setStyle("-fx-text-fill: #dddddd");
        numMovesPlayed.setFont(new Font(30));
        numMovesPlayed.setPadding(new Insets(15, 30, 0, 30));
        colourToPlay.setFont(new Font(30));
        colourToPlay.setPadding(new Insets(15, 30, 0, 30));
        statsPanel.getChildren().removeAll();
        statsPanel.getChildren().addAll(numMovesPlayed, colourToPlay);
        statsPanel.setPrefHeight(30);
        statsPanel.setAlignment(Pos.CENTER);

        colourPane = new Pane();
        colourPane.setPrefSize(800,30);
        colourPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        if (numMoves % 2 == 0) {
            colourPane.setStyle("-fx-background-color: #FFFFFF");
        } else {
            colourPane.setStyle("-fx-background-color: #000000");
        }

        base.getChildren().removeAll();
        base.setBottom(statsPanel);
        base.setTop(colourPane);
    }

    public boolean oppInCheck(char c) { //char represents colour that just moved
        boolean result;
        if (c == 'b') {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (square[i][j].piece.colour == 'b') {
                        getMoves(square[i][j]);
                        result = square[wkRank][wkFile].highlighted;
                        getMoves(square[i][j]);
                        if (result) return true;
                    }
                }
            }
        } else if (c == 'w') {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (square[i][j].piece.colour == 'w') {
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

    public void redraw() {
        makePieces();
        makeBoard();
        root = new StackPane();
        root.setMaxSize(802, 802);
        root.getChildren().add(board);
        root.getChildren().add(piecesPane);
        base.setCenter(root);
        root.requestFocus();
    }

    public void makeBoard() {
        board = new TilePane();
        board.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        board.setPrefColumns(8);
        board.setPrefRows(8);
        board.setPrefTileHeight(100);
        board.setPrefTileWidth(100);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getChildren().add(new Rectangle(100, 100, square[i][j].colour));
            }
        }
    }

    public void makePieces() {
        piecesPane = new TilePane();
        piecesPane.setPrefColumns(8);
        piecesPane.setPrefRows(8);
        piecesPane.setPrefTileHeight(100);
        piecesPane.setPrefTileWidth(100);

        System.out.println();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                piecesPane.getChildren().add(square[i][j].piece.icon);
                System.out.print(square[i][j].piece.type + " ");
            }
            System.out.println();
        }
    }

    public void updateLastMove(int newR, int newF, int startR, int startF){
        if (lmsRank > -1 && lmsFile > -1)
            square[lmsRank][lmsFile].setLastMovedStartTrue(false);
        if (lmRank > -1 && lmFile > -1)
            square[lmRank][lmFile].setLastMovedTrue(false);
        lmsRank = startR;
        lmsFile = startF;
        lmRank = newR;
        lmFile = newF;
        square[lmsRank][lmsFile].setLastMovedStartTrue(true);
        square[lmRank][lmFile].setLastMovedTrue(true);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
