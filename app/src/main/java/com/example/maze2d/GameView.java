package com.example.maze2d;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    private Cell[][] cells;
    private int rows = 5;
    private int columns = 5;
    private float cellSize;
    private float hMargin;
    private float vMargin;
    private Paint paint;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Initialize paint instance;
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);

        createMaze();
    }

    // Initialize
    private void createMaze() {
        // Initialize cells array..
        cells = new Cell[columns][rows];
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
    }

    // Fulfill the background with yellow.
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
    }

    private class Cell {
        // Set the boolean value for each wall of the cell.
        // True => wall presented.
        boolean topWall = true;
        boolean bottomWall = true;
        boolean leftWall = true;
        boolean rightWall = true;
        int column, row;

        Cell(int column, int row) {
            this.column = column;
            this.row = row;
        }
    }

}
