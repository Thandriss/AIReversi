package model;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AI {

    private Colors comp;
    private Colors user;
    boolean flagNoWhereToStand = false;
    private final boolean userTurn = false;
    private final boolean compTurn = true;
    private List<Board> toSave = new ArrayList<Board>();

    public void setColorAI(Colors color) {
        this.comp = color;
    }
    public void setColorUser(Colors color) {
        this.user = color;
    }

    private static Board doAClone(Board board)  {
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newBoard.putChip(i , j, board.valueAt(i, j));
            }
        }
        newBoard.setCountBlack(board.getCountBlack());
        newBoard.setCountWhite(board.getCountWhite());
        return newBoard;
    }

    private Integer valueOfComp(Board field) {
        if (comp == Colors.Black) {
            toSave.remove(toSave.size() - 1);
            return field.getCountBlack();
        }
        else {
            toSave.remove(toSave.size() - 1);
            return field.getCountWhite();
        }
    }
    private Integer minimax(Board field, boolean isMaxOrMin, int depth) {
        Integer bestScore;
        toSave.add(doAClone(field));
        Board board = doAClone(field);
        if (depth != 3 || board.getCountWhite() + board.getCountBlack() >= 64) {
            if (isMaxOrMin) {
                List<Pair<Integer, Integer>> toStand = board.whereToStand(comp);
                //выбираем ход, который нам выгодней
                bestScore = 0;
                for (Pair<Integer, Integer> i: toStand) {
                    board.putChip(i.getKey(), i.getValue(),comp);
                    board.changeColor(i.getKey(), i.getValue(),comp);
                    int score = minimax(board, userTurn, depth + 1);
                    bestScore = Math.max(bestScore, score);
                    if (bestScore == 65) bestScore = valueOfComp(board);
                    board = doAClone(toSave.get(toSave.size() - 1));
                }
                if (toSave.size() > 1)toSave.remove(toSave.size() - 1);
            } else {
                //противник выбирает ход, который нам не выгоден
                List<Pair<Integer, Integer>> toStand = board.whereToStand(user);
                bestScore = 65;
                for (Pair<Integer, Integer> i: toStand) {
                    board.putChip(i.getKey(), i.getValue(),user);
                    board.changeColor(i.getKey(), i.getValue(),user);
                    int score = minimax(board, compTurn, depth + 1);
                    bestScore = Math.min(bestScore, score);
                    if (bestScore == 0) bestScore = valueOfComp(board);
                    board = doAClone(toSave.get(toSave.size() - 1));
                }
                if (toSave.size() > 1)toSave.remove(toSave.size() - 1);
            }
        } else {
            bestScore = valueOfComp(board);
        }
        return bestScore;
    }

    public Pair<Integer, Integer> getPosition (Board board){
        Board field = doAClone(board);
        toSave.add(doAClone(field));
        Pair<Integer, Integer> compMove = new Pair<Integer, Integer>(0, 0);
        Integer score;
        if (board.getCountWhite() + board.getCountBlack() != 64) {
            List<Pair<Integer, Integer>> toStand = field.whereToStand(comp);
            if (toStand.isEmpty()) flagNoWhereToStand = true;
            int bestScore = -1;
            for (Pair<Integer, Integer> i : toStand) {
                field.putChip(i.getKey(), i.getValue(), comp);
                field.changeColor(i.getKey(), i.getValue(), comp);
                score = minimax(field, userTurn, 1);
                if (score >= bestScore) {
                    bestScore = score;
                    compMove = i;
                }
                field = doAClone(toSave.get(toSave.size() - 1));
            }
            toSave.clear();
        }
        System.out.println(compMove);
        return compMove;
    }
    public void act (Board board) {
        Pair<Integer, Integer> whereToPut = getPosition(board);
        if (!flagNoWhereToStand && board.getCountWhite() + board.getCountBlack() != 64 && board.valueAt(whereToPut.getKey(), whereToPut.getValue()) != user) {
            board.putChip(whereToPut.getKey(), whereToPut.getValue(), comp);
            board.changeColor(whereToPut.getKey(), whereToPut.getValue(), comp);
        }
        flagNoWhereToStand = false;
    }
}
