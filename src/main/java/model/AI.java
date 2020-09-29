package model;

import javafx.util.Pair;

import java.io.*;
import java.util.List;

public class AI {

    private Colors comp;
    private Colors user;
    private boolean userTurn = false;
    private boolean compTurn = true;

    public void setColorAi(Colors color) {
        this.comp = color;
    }
    public void setColorUser(Colors color) {
        this.user = color;
    }

    private static Board doAClone(Board board) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ous = new ObjectOutputStream(baos);
        ous.writeObject(board);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Board cloneBoard = (Board) ois.readObject();
        return cloneBoard;
    }

    private Integer minimax(Board field, boolean isMaxOrMin) {
        Integer bestScore = 0;
        if (field.getCountBlack() + field.getCountWhite() != 64) {
            if (isMaxOrMin) {
                List<Pair<Integer, Integer>> toStand = field.whereToStand(comp);
                //выбираем ход, который нам выгодней
                bestScore = -1;
                for (Pair<Integer, Integer> i: toStand) {
                    field.putChip(i.getKey(), i.getValue(),comp);
                    int score = minimax(field, userTurn);
                    field.putChip(i.getKey(), i.getValue(),Colors.Empty);
                    bestScore = Math.max(bestScore, score);
                }
                return bestScore;
            } else {
                //противник выбирает ход, который нам не выгоден
                List<Pair<Integer, Integer>> toStand = field.whereToStand(user);
                bestScore = 65;
                for (Pair<Integer, Integer> i: toStand) {
                    field.putChip(i.getKey(), i.getValue(),comp);
                    int score = minimax(field, compTurn);
                    field.putChip(i.getKey(), i.getValue(),Colors.Empty);
                    bestScore = Math.min(bestScore, score);
                }
                return bestScore;
            }
        } else {
            if (field.getCountWhite() < field.getCountBlack()) return field.getCountBlack();
            if (field.getCountWhite() > field.getCountBlack()) return field.getCountWhite();
        }
        return bestScore;
    }

    public Pair<Integer, Integer> getPosition (Board board) throws IOException, ClassNotFoundException {
        Board field = doAClone(board);
        Pair<Integer, Integer> compMove = new Pair<Integer, Integer>(0, 0);
        Integer score;
        List<Pair<Integer, Integer>> toStand = field.whereToStand(comp);
        int bestScore = -1;
        for (Pair<Integer, Integer> i: toStand) {
            compMove = i;
            field.putChip(i.getKey(), i.getValue(), comp);
            score = minimax(field, userTurn);
            field.putChip(i.getKey(), i.getValue(), Colors.Empty);
            if (score > bestScore) {
                compMove = i;
            }
        }
        return compMove;
    }
}
