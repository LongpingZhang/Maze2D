package com.example.maze2d;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

// The idea of how to create a maze using back tracking recursion algorithm is from
// https://www.youtube.com/watch?v=kiG1BUa34lc.
public class GameView extends View {
    private Cell[][] cells;
    private int rows = 10;
    private int columns = 10;
    private float cellSize;
    private float hMargin;
    private float vMargin;
    private Paint paint;
    private Paint playerPaint;
    private Paint endPaint;
    private Cell player;
    private Cell end;
    private float playerLeft;
    private float playerRight;
    private float playerTop;
    private float playerBottom;
    private float height;
    private float width;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Initialize paint instances.
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        endPaint = new Paint();
        endPaint.setColor(Color.GREEN);
        System.out.print(cellSize);

        createMaze();
    }

    // Use back-tracking recursion algorithm to create a maze.
    private void createMaze() {
        // Initialize cells array..
        cells = new Cell[columns][rows];
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        // Initiate player and end for later use in onDraw method.
        player = cells[0][0];
        end = cells[columns - 1][rows - 1];

        Stack<Cell> stack = new Stack<>();
        Cell current = cells[0][0];
        do {
            current.visited = true;
            Cell next = getNeighbour(current);
            if (next != null) {
                clearWall(current, next);
                stack.push(current);
                current = next;
            } else {
                current = stack.pop();
            }
        } while (!stack.empty());
    }

    // Draw the maze, player, and end.
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.YELLOW);

        // Get the height and width of the screen.
        int height = getHeight();
        int width = getWidth();

        // Calculate cellSize.
        if (columns / rows > width / height) {
            // "+1" refers to one more space for margin
            cellSize = width / (columns + 1);
        } else {
            cellSize = height / (rows + 1);
        }
        // Calculate margin.
        hMargin = (width - cellSize * columns) / 2;
        vMargin = (height - cellSize * rows) / 2;

        // Change the reference point (0,0) to the top left of the maze instead of the screen,
        canvas.translate(hMargin, vMargin);

        // Draw the grids.
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                if (cells[x][y].topWall) {
                    canvas.drawLine(x * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            y * cellSize,
                            paint);
                }
                if (cells[x][y].rightWall) {
                    System.out.println("I have printed the wall");
                    canvas.drawLine((x + 1) * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            paint);
                }
                if (cells[x][y].bottomWall) {
                    canvas.drawLine(x * cellSize,
                            (y + 1) * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            paint);
                }
                if (cells[x][y].leftWall) {
                    canvas.drawLine(x * cellSize,
                            y * cellSize,
                            x * cellSize,
                            (y + 1) * cellSize,
                            paint);
                }
            }
        }

        float margin = cellSize / 10;
        // Draw the player
        playerLeft = player.column * cellSize + margin;
        playerRight = (player.column + 1) * cellSize - margin;
        playerTop = player.row * cellSize + margin;
        playerBottom = (player.row + 1) * cellSize - margin;
        this.height = playerTop - playerBottom;
        this.width = playerRight - playerLeft;
        canvas.drawRect(playerLeft, playerTop, playerRight, playerBottom, playerPaint);

        // Draw the end
        float endLeft = end.column * cellSize + margin;
        float endRight = (end.column + 1) * cellSize - margin;
        float endTop = end.row * cellSize + margin;
        float endBottom = (end.row + 1) * cellSize - margin;
        canvas.drawRect(endLeft, endTop, endRight, endBottom, endPaint);
    }
    public boolean onTouchEvent(MotionEvent event) {
        playerLeft = event.getX() - width / 2;
        playerRight = event.getX() + width / 2;
        playerTop = event.getY() + height / 2;
        playerBottom = event.getY() - height / 2;
        this.invalidate();
        return true;
    }

    // Get the neighbour cell of the current cell randomly.
    private Cell getNeighbour(Cell current) {
        ArrayList<Cell> neighbours = new ArrayList<>();

        // left neighbour
        if (current.column > 0) {
            if (!cells[current.column - 1][current.row].visited) {
                neighbours.add(cells[current.column - 1][current.row]);
            }
        }
        // right neighbour
        if (current.column < columns - 1) {
            if (!cells[current.column + 1][current.row].visited) {
                neighbours.add(cells[current.column + 1][current.row]);
            }
        }
        // top neighbour
        if (current.row > 0) {
            if (!cells[current.column][current.row - 1].visited) {
                neighbours.add(cells[current.column][current.row - 1]);
            }
        }
        // bottom neighbour
        if (current.row < rows - 1) {
            if (!cells[current.column][current.row + 1].visited) {
                neighbours.add(cells[current.column][current.row + 1]);
            }
        }
        Random random = new Random();
        if (neighbours.size() > 0) {
            return neighbours.get(random.nextInt(neighbours.size()));
        }
        return null;
    }

    // Clear the wall between two adjacent cells.
    private void clearWall(Cell current, Cell next) {
        // left wall
        if (current.column == next.column + 1 && current.row == next.row) {
            current.leftWall = false;
            next.rightWall = false;
        }
        // right wall
        if (current.column == next.column - 1 && current.row == next.row) {
            current.rightWall = false;
            next.leftWall = false;
        }
        // top wall
        if (current.column == next.column && current.row == next.row + 1) {
            current.topWall = false;
            next.bottomWall = false;
        }
        // bottom wall
        if (current.column == next.column && current.row == next.row - 1) {
            current.bottomWall = false;
            next.topWall = false;
        }

    }

    // Private class for storing necessary information of a cell.
    private class Cell {
        // Set the boolean value for each wall of the cell.
        // True => wall presented.
        boolean topWall = true;
        boolean bottomWall = true;
        boolean leftWall = true;
        boolean rightWall = true;
        int column, row;
        boolean visited = false;

        Cell(int column, int row) {
            this.column = column;
            this.row = row;
        }
    }

}
