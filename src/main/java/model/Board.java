package model;

import javafx.util.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
    private Integer countWhite = 0;
    private Integer countBlack = 0;
    private Colors [][] grid = new Colors[8][8];

    private List<Pair<Integer, Integer>> sidesOfLook = new ArrayList<Pair<Integer, Integer>>() {
        {
            add(new Pair<Integer, Integer>(0, 1));
            add(new Pair<Integer, Integer>(1, 0));
            add(new Pair<Integer, Integer>(0, -1));
            add(new Pair<Integer, Integer>(-1, 0));
            add(new Pair<Integer, Integer>(1, 1));
            add(new Pair<Integer, Integer>(1, -1));
            add(new Pair<Integer, Integer>(-1, -1));
            add(new Pair<Integer, Integer>(-1, 1));
        }
    };

    public void setCountBlack(Integer countBlack) {
        this.countBlack = countBlack;
    }

    public void setCountWhite(Integer countWhite) {
        this.countWhite = countWhite;
    }

    public Integer getCountBlack() {
        return countBlack;
    }

    public Integer getCountWhite() {
        return countWhite;
    }

    public Board() {
        for (int i = 0; i <= 7; ++i) {
            for (int j = 0; j <= 7;++j) {
                Colors a = Colors.Empty;
                grid[i][j] = a;
            }
        }
        countWhite += 2;
        countBlack += 2;
        grid[3][3] = Colors.White;
        grid[4][3] = Colors.Black;
        grid[3][4] = Colors.Black;
        grid[4][4] = Colors.White;
    }

    private boolean notOutOfRange (int x, int y) {
        int width = 8;
        int height = 8;
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    private boolean canWePut (int x, int y) {
        return (notOutOfRange(x, y)) || (valueAt(x, y) == Colors.Empty && valueAt(x, y) == Colors.CanPut);
    }

    public Colors valueAt (int x, int y) {
        if (notOutOfRange(x, y)) return grid[x][y];
        return null;
    }


    protected void putChip (Integer x, Integer y, Colors value) {
        if (canWePut(x, y))
            if (value == Colors.Black) {
                countBlack += 1;
            } else if (value == Colors.White) countWhite += 1;
        grid[x][y] = value;
    }

    private void change (int x, int y, Colors color) {
        if(valueAt(x, y) != color) {
            grid[x][y] = color;
        }
        if (color == Colors.Black) {
            countWhite -= 1;
            countBlack += 1;
        } else {
            countBlack -= 1;
            countWhite += 1;
        }
    }
    public boolean putTheChip (Integer x, Integer y, Colors color, List<Pair<Integer, Integer>> canBePut) {
        if (!notOutOfRange(x, y)) throw new IllegalArgumentException();
        Pair<Integer, Integer> checking = new Pair<Integer, Integer>(x, y);
        if (canBePut.isEmpty() || !canBePut.contains(checking)) return false;
        putChip(x, y, color);
        return true;
    }

    public void changeColor (Integer x, Integer y, Colors color) {
        List<Pair<Integer, Integer>> shouldBeChanged = sсannerOfNearToChange(x, y, color);
        for (Pair i: shouldBeChanged) {
            Integer newX = (Integer) i.getKey();
            Integer newY = (Integer) i.getValue();
            if (valueAt(newX, newY) == Colors.Black || valueAt(newX, newY) == Colors.White) {
                change(newX, newY, color);
            }
        }
    }

    public  void whereToStandPut( List<Pair<Integer, Integer>> canBePut) {
        for (Pair<Integer, Integer> i: canBePut) {
            putChip(i.getKey(), i.getValue(), Colors.CanPut);
        }
    }

    public void deleteCanPut(List<Pair<Integer, Integer>> canBePut) {
        for (Pair<Integer, Integer> i: canBePut) {
            if (valueAt(i.getKey(), i.getValue()) == Colors.CanPut) {
                putChip(i.getKey(), i.getValue(), Colors.Empty);
            }
        }
    }

    private  List<Pair<Integer, Integer>> sсannerOfNearToChange (Integer x, Integer y, Colors color) {
        List<Pair<Integer, Integer>> answer = new ArrayList<Pair<Integer, Integer>>();
        List<Pair<Integer, Integer>> between = new ArrayList<Pair<Integer, Integer>>();
        Integer savedX = x;
        Integer savedY = y;
        Colors saved = color;
        for (Pair i : sidesOfLook) {
            Integer naviX = (Integer) i.getKey();
            Integer naviY = (Integer) i.getValue();
            while (notOutOfRange(x, y)) {
                if (valueAt(x + naviX, y + naviY) != color
                        && valueAt(x + naviX, y + naviY) != Colors.Empty
                        && valueAt(x + naviX, y + naviY) != Colors.CanPut
                        && notOutOfRange(x + 2 * naviX, y + 2 * naviY)) {
                    Pair<Integer, Integer> b = new Pair<Integer, Integer>(x + naviX, y + naviY);
                    between.add(b);
                } else if (valueAt(x + naviX, y + naviY) == color
                        && notOutOfRange(x + naviX, y + naviY)) {
                    answer.addAll(between);
                    between.clear();
                    break;
                } else {
                    between.clear();
                    break;
                }
                x = x + naviX;
                y = y + naviY;
                saved = color;
            }
            x = savedX;
            y = savedY;
            color = saved;
        }
        return answer;
    }

    public List<Pair<Integer, Integer>> whereToStand(Colors color) {
        ArrayList<Pair<Integer, Integer>> placesToStand = new ArrayList<Pair<Integer, Integer>>();
        findPlaceAbleToPut(color,placesToStand);
        return placesToStand;
    }

    private void findPlaceAbleToPut(Colors color, ArrayList<Pair<Integer, Integer>> places) {
        for (int i = 0; i < 8; ++i) {
            for (int j= 0; j < 8; ++j) {
                int savedI = i;
                int savedJ = j;
                for (Pair<Integer, Integer> navi: sidesOfLook) {
                    if (valueAt(i, j) != color && valueAt(i, j) != Colors.Empty && valueAt(i, j) != Colors.CanPut) {
                        if (i - navi.getKey() >= 0 && j - navi.getValue() >= 0 && i - navi.getKey() <= 7 && j - navi.getValue() <= 7
                                && valueAt(i - navi.getKey(), j - navi.getValue()) == Colors.Empty) {
                            i = i + navi.getKey();
                            j = j + navi.getValue();
                            while (notOutOfRange(i, j) && valueAt(i, j) != color && valueAt(i, j) != Colors.Empty
                                    && valueAt(i, j) != Colors.CanPut) {
                                i += navi.getKey();
                                j += navi.getValue();
                            }
                            if (notOutOfRange(i, j) && valueAt(i, j) == color &&
                                    !places.contains(new Pair<Integer, Integer>(savedI - navi.getKey(), savedJ - navi.getValue()))) {
                                places.add(new Pair<Integer, Integer>(savedI - navi.getKey(), savedJ - navi.getValue()));
                            }
                        }
                    }
                    i = savedI;
                    j = savedJ;
                }
            }
        }
    }
}
