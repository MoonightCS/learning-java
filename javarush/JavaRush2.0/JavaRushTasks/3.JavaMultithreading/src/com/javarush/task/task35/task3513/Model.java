package com.javarush.task.task35.task3513;

import java.util.*;

/*
Твой прогресс впечатляет! Для разнообразия, предлагаю дать игре возможность самостоятельно
выбирать следующий ход.

Начнем с простого, реализуем метод randomMove в классе Model, который будет вызывать один из методов движения случайным образом. Можешь реализовать это вычислив целочисленное n = ((int) (Math.random() * 100)) % 4.
Это число будет содержать целое псевдослучайное число в диапазоне [0..3], по каждому из которых можешь вызывать один из методов left, right, up, down.

Не забудь добавить в метод keyPressed класса Controller вызов метода randomMove по нажатию на клавишу R (код — KeyEvent.VK_R).

P.S. Проверку корректности работы метода randomMove оставляю полностью под твою ответственность, проверю только наличие вызова метода Math.random().


 */


public class Model {

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    protected int score = 0;
    protected int maxTile = 2;
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
    }

    public static void main(String[] args) {
        Model model = new Model();
        model.gameTiles = new Tile[FIELD_WIDTH][];
        model.gameTiles[0] = new Tile[]{new Tile(2), new Tile(2), new Tile(2), new Tile(2)};
        model.gameTiles[1] = new Tile[]{new Tile(2), new Tile(4), new Tile(4), new Tile(4)};
        model.gameTiles[2] = new Tile[]{new Tile(4), new Tile(8), new Tile(8), new Tile(8)};
        model.gameTiles[3] = new Tile[]{new Tile(4), new Tile(16), new Tile(16), new Tile(16)};

        model.print();
        model.up();
        model.print();

    }

    public void randomMove() {
        int randomNumber = ((int)(Math.random() * 4));
        if (randomNumber == 0) {
            left();
        } else if (randomNumber == 1) {
            right();
        } else if (randomNumber == 2) {
            up();
        } else if (randomNumber == 3) {
            down();
        }
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            score = previousScores.pop();
            gameTiles = previousStates.pop();
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }


    private Tile[][] copy(Tile[][] arr) {
        Tile[][] copyArr = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                copyArr[i][j] = new Tile(arr[i][j].value);
            }
        }
        return copyArr;
    }


    public boolean canMove() {
        boolean canMove = false;

        Tile[][] copy = copy(gameTiles);

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (compressTiles(copy[j]) | mergeTiles(copy[j])) {
                    return true;
                }
            }
            clockwiseRotate(copy);
        }
        return false;
    }

    private void print() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.printf("%3d ", this.gameTiles[i][j].value);
            }
            System.out.println();
        }
        System.out.println();
    }


    private void copy(Tile[][] dest, Tile[][] source) {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                dest[i][j] = new Tile(source[i][j].value);
            }
        }
    }

    protected void up() {
        saveState(gameTiles);
        counterClockwiseRotate(gameTiles);
        left();
        clockwiseRotate(gameTiles);

    }

    protected void down() {
        saveState(gameTiles);
        clockwiseRotate(gameTiles);
        left();
        counterClockwiseRotate(gameTiles);
    }

    protected void right() {
        saveState(gameTiles);
        counterClockwiseRotate(gameTiles);
        counterClockwiseRotate(gameTiles);
        left();
        clockwiseRotate(gameTiles);
        clockwiseRotate(gameTiles);
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] copy = copy(tiles);
        previousScores.push(score);
        previousStates.push(copy);
        isSaveNeeded = false;
    }


    private static void counterClockwiseRotate(Tile[][] arr) {
        int len = arr.length;
        for (int i = 0; i < len / 2; i++) {
            for (int j = i; j < len - 1 - i; j++) {
                Tile tmp = arr[i][j];
                arr[i][j] = arr[j][len - 1 - i];
                arr[j][len - 1 - i] = arr[len - 1 - i][len - 1 - j];
                arr[len - 1 - i][len - 1 - j] = arr[len - 1 - j][i];
                arr[len - 1 - j][i] = tmp;
            }
        }
    }

    private static void clockwiseRotate(Tile[][] arr) {

        int len = arr.length;
        for (int i = 0; i < len / 2; i++) {
            for (int j = len - 1 - i; j > i; j--) {
                Tile tmp = arr[i][j];
                arr[i][j] = arr[len - 1 - j][i];
                arr[len - 1 - j][i] = arr[len - i - 1][len - 1 - j];
                arr[len - 1 - i][len - 1 - j] = arr[j][len - 1 - i];
                arr[j][len - 1 - i] = tmp;
            }
        }

    }

    protected boolean canMove(Tile[][] array) {
        boolean isCompressed;
        boolean isMerged;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            isCompressed = compressTiles(array[i]);
            isMerged = mergeTiles(array[i]);
            if (isCompressed || isMerged) {
                return true;
            }
        }
        return false;

    }


    protected void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) {
            addTile();
        }
        isSaveNeeded = true;
    }


    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = 1; i < tiles.length; i++) {
            if ((tiles[i - 1].value == tiles[i].value) && !tiles[i - 1].isEmpty() && !tiles[i].isEmpty()) {
                tiles[i - 1].value *= 2;
                int value = tiles[i - 1].value;
                score += value;
                maxTile = maxTile > value ? maxTile : value;
                tiles[i] = new Tile();
                compressTiles(tiles);
                isChanged = true;
            }
        }
        return isChanged;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean change = false;
        for (int i = 1; i < tiles.length; i++) {
            for (int j = 1; j < tiles.length; j++) {
                if (tiles[j - 1].isEmpty() && !tiles[j].isEmpty()) {
                    change = true;
                    tiles[j - 1] = tiles[j];
                    tiles[j] = new Tile();
                }
            }
        }
        return change;
    }

    protected void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            emptyTiles.get((int) (Math.random() * emptyTiles.size())).value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    private List<Tile> getEmptyTiles() {

        List<Tile> emptyTiles = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTiles;
    }
}
