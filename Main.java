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

    public void start(Stage primaryStage) {

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
    }

    private void piecePickerUpper(MouseEvent e) {
        if (pieceInHand) {
            placePiece(e);
        } else {
            pickUpPiece(e);
        }

    }

    private void reset() {

    }

    private void placePiece(MouseEvent e) {

        System.out.printf("start2 white king: %d %d\n", wkRank, wkFile);
        System.out.printf("start2 black king: %d %d\n", bkRank, bkFile);

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
        //getMoves(lastSquare); //toggles highlights
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
            } else if (square[rank][file].piece.type == 'K') {
                wkFile = file;
                wkRank = rank;
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
            base.setStyle("-fx-background-color: #FF0000");
        } else {
            base.setStyle("-fx-background-color: #AAAAAA");
        }
        System.out.printf("end2 white king: %d %d\n", wkRank, wkFile);
        System.out.printf("end2 black king: %d %d\n", bkRank, bkFile);
    }

    private void handleMouseDrag(MouseEvent e) {
        if (hoverSquare == null) {
            return;
        }
        hoverX = (int) e.getX() - 50;
        hoverY = (int) e.getY() - 50;
        hoverPane.setTranslateX(hoverX);
        hoverPane.setTranslateY(hoverY);
    }

    private void pickUpPiece(MouseEvent e) {
        System.out.printf("start1 white king: %d %d\n", wkRank, wkFile);
        System.out.printf("start1 black king: %d %d\n", bkRank, bkFile);
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

        System.out.printf("end1 white king: %d %d\n", wkRank, wkFile);
        System.out.printf("end1 black king: %d %d\n", bkRank, bkFile);

    }

    private void initialize() {
        fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        //fen = "8/8/8/3bB3/8/8/8/8 u ";
        square = parseFEN(fen); //rank-file
        numMoves = 0;
        pieceInHand = false;
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
            //if ((rank>0 && piece.colour == 'w') || (rank < 7 && piece.colour == 'b')){
            if (checkAndHighlight(rank + dir, file, s)) {
                if (piece.turnLastMoved == -1) {
                    checkAndHighlight(rank + 2 * dir, file, s);
                }
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
            checkAndHighlight(rank, file - 1, s);
            checkAndHighlight(rank, file + 1, s);
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
    }

    public boolean checkAndHighlight(int r, int f, Square s) {
        boolean good = false;
        char myCol = s.piece.colour;
        char oppCol = myCol == 'w' ? 'b' : 'w';
        if (r < 0 || r > 7 || f < 0 || f > 7) return false; //oob checks
        if (square[r][f].piece.colour == myCol) return false; //cant take own colour
        if (s.piece.type == 'p' || s.piece.type == 'P') { //pawns
            if (!square[r][f].isEmpty() && s.file == f) return false; //pawns cant take in the same file
            if (s.file != f && square[r][f].isEmpty()) return false; //pawns cant move diag if empty
        }

        //this chunk determines if the move puts themselves in check
        //does not currently work with en passent
        if ((numMoves % 2 == 0 && myCol == 'w') || (numMoves % 2 == 1 && myCol == 'b')) {
            Square sTaken = square[r][f]; //sTaken is the square that is temporarily being replaced
            int oldRank = s.rank; //so we know where to put the square back to later
            int oldFile = s.file;


            if (s.piece.type == 'k' || s.piece.type == 'K'){
                if (s.piece.type == 'K') {
                    wkRank = r;
                    wkFile = f;
                } else {
                    bkRank = r;
                    bkFile = f;
                }

                Square temp = square[r][f];
                square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece(s.piece.type), false);
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

            square[r][f] = s; //place the square at the prospective location
            square[oldRank][oldFile] = new Square(oldRank, oldFile, new Piece('x'), false); //replace old with temp blank

            if (!oppInCheck(oppCol)) good = true;

            square[oldRank][oldFile] = square[r][f];
            square[r][f] = sTaken;



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
        numMovesPlayed.setFont(new Font(30));
        numMovesPlayed.setPadding(new Insets(15, 30, 0, 30));
        colourToPlay.setFont(new Font(30));
        colourToPlay.setPadding(new Insets(15, 30, 0, 30));
        statsPanel.getChildren().removeAll();
        statsPanel.getChildren().addAll(numMovesPlayed, colourToPlay);
        statsPanel.setPrefHeight(30);
        statsPanel.setAlignment(Pos.CENTER);
        base.getChildren().removeAll();
        base.setBottom(statsPanel);
    }

    public boolean oppInCheck(char c) { //char represents colour that just moved
        boolean result;
        if (c == 'b') {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (square[i][j].piece.colour == 'b') {
                        getMoves(square[i][j]);
                        result = square[wkRank][wkFile].highlighted == true;
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
        root.setMaxSize(800, 800);
        root.getChildren().add(board);
        root.getChildren().add(piecesPane);
        base.setCenter(root);
        root.requestFocus();
    }

    public void makeBoard() {
        board = new TilePane();
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

    public static void main(String[] args) {
        launch(args);
    }
}
