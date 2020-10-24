package model;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AI {

    private Colors comp;
    private Colors user;
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
        return newBoard;
    }

    private Integer minimax(Board field, boolean isMaxOrMin, int depth) {
        Integer bestScore = 0;
        toSave.add(doAClone(field));
        if (depth != 6) {
            if (isMaxOrMin) {
                List<Pair<Integer, Integer>> toStand = field.whereToStand(comp);
                //выбираем ход, который нам выгодней
                bestScore = -1;
                for (Pair<Integer, Integer> i: toStand) {
                    field.putChip(i.getKey(), i.getValue(),comp);
                    field.changeColor(i.getKey(), i.getValue(),comp);
                    int score = minimax(field, userTurn, depth + 1);
                    //field.putChip(i.getKey(), i.getValue(),Colors.Empty);
                    bestScore = Math.max(bestScore, score);
                    field = toSave.get(toSave.size() - 1);
                }
                toSave.remove(toSave.size() - 1);
                return bestScore;
            } else {
                //противник выбирает ход, который нам не выгоден
                List<Pair<Integer, Integer>> toStand = field.whereToStand(user);
                bestScore = 65;
                for (Pair<Integer, Integer> i: toStand) {
                    field.putChip(i.getKey(), i.getValue(),user);
                    field.changeColor(i.getKey(), i.getValue(),user);
                    int score = minimax(field, compTurn, depth + 1);
                    //field.putChip(i.getKey(), i.getValue(),Colors.Empty);
                    bestScore = Math.min(bestScore, score);
                    field = toSave.get(toSave.size() - 1);
                }
                toSave.remove(toSave.size() - 1);
                return bestScore;
            }
        } else {
            if (comp == Colors.Black) {
                toSave.remove(toSave.size() - 1);
                return field.getCountBlack();
            }
            if (comp == Colors.White) {
                toSave.remove(toSave.size() - 1);
                return field.getCountWhite();
            }
        }
        return bestScore;
    }

    public Pair<Integer, Integer> getPosition (Board board){
        Board field = doAClone(board);
        toSave.add(doAClone(field));
        Pair<Integer, Integer> compMove = new Pair<Integer, Integer>(0, 0);
        Integer score;
        List<Pair<Integer, Integer>> toStand = field.whereToStand(comp);
        int bestScore = -1;
        for (Pair<Integer, Integer> i: toStand) {
            //compMove = i;
            field.putChip(i.getKey(), i.getValue(), comp);
            field.changeColor(i.getKey(), i.getValue(), comp);
            score = minimax(field, userTurn, 1);
            //field.putChip(i.getKey(), i.getValue(), Colors.Empty);
            if (score > bestScore) {
                bestScore = score;
                compMove = i;
            }
            field = toSave.get(toSave.size() - 1);
        }
        toSave.clear();
        return compMove;
    }
    public void act (Board board) {
        Pair<Integer, Integer> whereToPut = getPosition(board);
        board.putChip(whereToPut.getKey(), whereToPut.getValue(), comp);
        board.changeColor(whereToPut.getKey(), whereToPut.getValue(), comp);
    }
}
