package com.javarush.games.game2048;

import com.javarush.engine.cell.*;
import com.javarush.engine.cell.Game;

import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private int moveCounter = 0;
    private int maxValue = 0;
    private boolean isGameStopped = false;
    private int score = 0;

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You win!!!", Color.RED, 50);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "It's end, sorry. You lose...", Color.RED, 50);

    }

    private void moveLeft() {
        for (int i = 0; i < SIDE; i++) {
            moveCounter += compressRow(gameField[i]) ? 1 : 0;
            if (mergeRow(gameField[i])) {
                moveCounter++;
            }
            moveCounter += compressRow(gameField[i]) ? 1 : 0;
        }
        if (moveCounter != 0) {
            createNewNumber();
            moveCounter = 0;
        }
    }

    private void moveRight() {
        twoRotateClockwise();
        moveLeft();
        twoRotateClockwise();
    }

    private void moveUp() {
        threeRotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        threeRotateClockwise();
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE) {
            if (!isGameStopped) {
                return;
            }
            isGameStopped = false;
            score = 0;
            setScore(score);
            createGame();
            drawScene();
        }
        if (!canUserMove()) {
            gameOver();
            return;
        }
        if (isGameStopped) {
            return;
        }
        if (key == Key.LEFT) {
            moveLeft();
            drawScene();
        }
        if (key == Key.RIGHT) {
            moveRight();
            drawScene();
        }
        if (key == Key.UP) {
            moveUp();
            drawScene();
        }
        if (key == Key.DOWN) {
            moveDown();
            drawScene();
        }
     }

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                setCellColoredNumber(j, i, gameField[i][j]);
            }
        }
    }

    private void createNewNumber() {
        if (getMaxTileValue() == 2048) {
            win();
        }
        while (true) {
            int r1 = getRandomNumber(SIDE);
            int r2 = getRandomNumber(SIDE);
            if (gameField[r1][r2] == 0) {
                int r3 = getRandomNumber(10);
                gameField[r1][r2] = r3 == 9 ? 4 : 2;
                return;
            }
        }
    }

    //Set color, which respond value
    private Color getColorByValue(int value) {
        String s = "";
        if (value == 0) {
            s = Color.AQUA.toString();
        }
        if (value == 2) {
            s = Color.LIGHTYELLOW.toString();
        }
        if (value == 4) {
            s = Color.YELLOW.toString();
        }
        if (value == 8) {
            s = Color.ORANGE.toString();
        }
        if (value == 16) {
            s = Color.DARKORANGE.toString();
        }
        if (value == 32) {
            s = Color.RED.toString();
        }
        if (value == 64) {
            s = Color.DARKRED.toString();
        }
        if (value == 128) {
            s = Color.SKYBLUE.toString();
        }
        if (value == 256) {
            s = Color.MIDNIGHTBLUE.toString();
        }
        if (value == 512) {
            s = Color.BLUEVIOLET.toString();
        }
        if (value == 1024) {
            s = Color.VIOLET.toString();
        }
        if (value == 2048) {
            s = Color.DARKVIOLET.toString();
        }
        return Color.valueOf(s);
    }

//    Engine not working with new java language level
//    At least, I can't use switch
//    private Color getColorByValue(int value) {
//        return switch (value) {
//            case 0 -> Color.AQUA;
//            case 2 -> Color.LIGHTYELLOW;
//            case 4 -> Color.YELLOW;
//            case 8 ->  Color.ORANGE;
//            case 16 -> Color.DARKORANGE;
//            case 32 -> Color.RED;
//            case 64 -> Color.DARKRED;
//            case 128 -> Color.SKYBLUE;
//            case 256 -> Color.MIDNIGHTBLUE;
//            case 512 -> Color.BLUEVIOLET;
//            case 1024 -> Color.VIOLET;
//            case 2048 -> Color.DARKVIOLET;
//            default -> throw new IllegalStateException("Wrong number: " + value);
//        };
//    }

    private void setCellColoredNumber(int x, int y, int value) {
        Color color = getColorByValue(value);
        if (value != 0) {
            String s = String.valueOf(value);
            setCellValueEx(x, y, color, s);
        } else {
            setCellValueEx(x, y, color, "");
        }
    }

    //Move blocks left
    private boolean compressRow(int[] row) {
        int f = 0;
        int l = row.length - 1;
        int[] copyRow = Arrays.copyOf(row, row.length);
        for (int j = 0; j < row.length; j++) {
            if (copyRow[j] == 0) {
                row[l] = copyRow[j];
                l--;
            }
            if (copyRow[j] != 0) {
                row[f] = copyRow[j];
                f++;
            }
        }
        return !Arrays.equals(copyRow, row);
    }

    //Merge blocks, which equal in value
    private boolean mergeRow(int[] row) {
        int[] copyRow = Arrays.copyOf(row, row.length);
        for (int j = 1; j < row.length; j++) {
            if (row[j - 1] == copyRow[j]) {
                row[j - 1] += copyRow[j];
                row[j] = 0;
                score += row[j - 1];
                setScore(score);
            }
        }
        return !Arrays.equals(copyRow, row);
    }

    //Why I need write moveRight(), moveUp() and moveDown() function?
    //If I am able to just turn over array
    private void rotateClockwise() {
        int[][] copyArray = new int[SIDE][SIDE];
        int[] copyLine = new int[SIDE];
        for (int i = 0; i < SIDE; i++) {
            copyArray[i] = Arrays.copyOf(gameField[i], SIDE);
        }
        for (int i = 0; i < SIDE; i++) {
            for (int j = 3, j1 = 0; j >= 0; j--, j1++) {
                copyLine[j1] = copyArray[j][i];
            }
            gameField[i] = Arrays.copyOf(copyLine, SIDE);
        }
    }

    private void twoRotateClockwise() {
        rotateClockwise();
        rotateClockwise();
    }

    private void threeRotateClockwise() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private int getMaxTileValue() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                maxValue = Math.max(maxValue, gameField[i][j]);
            }
        }
        return maxValue;
    }

    private boolean canUserMove() {
        for (int[] i : gameField) {
            for (int a : i) {
                if (a == 0) {
                    return true;
                }
            }
        }
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                int[] array = getNeighbors(i, j);
                for (int a : array) {
                    if (gameField[i][j] == a) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[] getNeighbors(int i, int j) {
        int[] neighbors = new int[4];
        if (i != 0) {
            neighbors[0] = gameField[i - 1][j];
        }
        if (i != SIDE - 1) {
            neighbors[1] = gameField[i + 1][j];
        }
        if (j != 0) {
            neighbors[2] = gameField[i][j - 1];
        }
        if (j != SIDE - 1) {
            neighbors[3] = gameField[i][j + 1];
        }
        return neighbors;
    }

}
