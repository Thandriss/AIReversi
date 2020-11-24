package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.AI;
import model.Board;
import model.Colors;

import java.util.List;

public class MainContr {
    private static Board board1;
    private static final Text White = new Text(800, 200, "White: ");
    private static final  Text Black = new Text(800, 600, "Black: ");
    private static final Text scoreBlack = new Text(900, 600, "");
    private static final Text scoreWhite = new Text(900, 200, "");
    private static final Text turn = new Text(800, 400, "Turn: ");
    private static final Text currentPlayer = new Text(900, 400, "");
    private static final Text winner = new Text(800, 500, "");
    private static final AI ai = new AI();
    private static int i = 0;
    private static GridPane viewOfBoard = new GridPane();
    private static Canvas[][] saveCanvas = new Canvas[8][8];
    private static Colors color = Colors.Black;
    private static List<Pair<Integer, Integer>> canBePut;
    private Stage stage;


    @FXML
    private Button startGame;

    @FXML
    public RadioButton white;

    @FXML
    public RadioButton black;

    private void setColor (RadioButton white, RadioButton black) {
        ToggleGroup together = new ToggleGroup();
        white.setToggleGroup(together);
        black.setToggleGroup(together);
        RadioButton selected = (RadioButton) together.getSelectedToggle();
        String choise = selected.getText();
        if (choise.equals("Black")){
            ai.setColorAI(Colors.White);
            ai.setColorUser(Colors.Black);
            color = Colors.Black;
        } else if (choise.equals("White")) {
            ai.setColorAI(Colors.Black);
            ai.setColorUser(Colors.White);
            color = Colors.White;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void buttonToDo (ActionEvent event) {
        setColor(white, black);
        board1 = new Board();
        if (ai.getComp() == Colors.Black) {
            ai.act(board1);
        }
        White.setFont(Font.font(30.0));
        Black.setFont(Font.font(30.0));
        turn.setFont(Font.font(30.0));
        scoreWhite.setFont(Font.font(30.0));
        scoreBlack.setFont(Font.font(30.0));
        currentPlayer.setFont(Font.font(30.0));
        winner.setFont(Font.font(30.0));
        Group rootWind = new Group(White, Black, scoreBlack, scoreWhite, turn, currentPlayer, viewOfBoard, winner);
        fullCanvasForView();
        canBePut = board1.whereToStand(color);
        board1.whereToStandPut(canBePut);
        drawBoard();
        Scene sceneOfGame = new Scene(rootWind, 1100, 800);
        sceneOfGame.setOnMouseClicked(mouseHandler);
        stage.setScene(sceneOfGame);
    }

    private static void fullCanvasForView() {
        for (int i = 0; i <=7; i++){
            for (int j = 0; j <=7; j++) {
                saveCanvas[i][j] = new Canvas(100, 100);
                viewOfBoard.add(saveCanvas[i][j], i, j);
            }
        }
    }

    private static void drawBoard() {
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i<=7; i++) {
            for(int j = 0; j<=7; j++) {
                GraphicsContext first = saveCanvas[i][j].getGraphicsContext2D();
                first.setStroke(Color.BLACK);
                first.setLineWidth(3);
                first.strokeRect(x, y, 100, 100);
                first.setFill(Color.SLATEGRAY);
                first.fillRect(x, y, 99, 99);
                if (board1.valueAt(i, j) == Colors.CanPut) {
                    first.setFill(Color.POWDERBLUE);
                    first.fillOval(40, 40,  20, 20);
                }else if (board1.valueAt(i, j) == Colors.Black) {
                    first.setStroke(Color.BLACK);
                    first.setLineWidth(5);
                    first.strokeOval(10, 10, 80, 80);
                } else if (board1.valueAt(i, j) == Colors.White) {
                    first.setStroke(Color.WHITE);
                    first.setLineWidth(5);
                    first.strokeOval(10, 10, 80, 80);
                }
            }
        }
        currentPlayer.setText(color.toString());
        scoreBlack.setText(board1.getCountBlack().toString());
        scoreWhite.setText(board1.getCountWhite().toString());
    }

    private static EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            int x = (int) event.getX() / 100;
            int y = (int) event.getY() / 100;
            checking();
            if (!canBePut.isEmpty()) {
                if (board1.putTheChip(x, y, color, canBePut)) {
                    board1.changeColor(x, y, color);
                    board1.deleteCanPut(canBePut);
                    stopGame();
                    drawBoard();
                    canBePut.clear();
                    i++;
                    checking();
                    canBePut = board1.whereToStand(color);
                    board1.whereToStandPut(canBePut);
                    drawBoard();
                }
            } else {
                i++;
                checking();
                canBePut = board1.whereToStand(color);
                board1.whereToStandPut(canBePut);
                drawBoard();
            }
        }
    };

    private static void stopGame() {
        if (board1.getCountBlack() + board1.getCountWhite() == 64) {
            if (board1.getCountBlack() > board1.getCountWhite()) {
                winner.setText("Winner is Black");
            } else if (board1.getCountWhite() > board1.getCountBlack()) {
                winner.setText("Winner is White");
            } else {
                winner.setText("We are the champions");
            }
        }
    }
    private static void checking () {
        if (i % 2 == 0) {
        }else{
            do {
                ai.act(board1);
                drawBoard();
                stopGame();
            } while (board1.whereToStand(Colors.Black).size() == 0 && board1.getCountBlack() + board1.getCountWhite() < 64);
            i++;
            checking();
        }
    }
}
