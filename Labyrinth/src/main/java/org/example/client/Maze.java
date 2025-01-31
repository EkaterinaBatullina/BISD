package org.example.client;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

@Getter
@Setter
public class Maze {
    private final int width;
    private final int height;
    private Trap trap;
    private Point point;
    private int[][] maze;
    private Image cloudTexture;
    private Image cloudTextur;
    private Point exitPoint;

    private int exitX = -1, exitY = -1;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[height][width];
        cloudTexture = new ImageIcon(getClass().getResource("/мраморр.jpg")).getImage();
        cloudTextur = new ImageIcon(getClass().getResource("/images.jpeg")).getImage();
        this.trap = new Trap(this);
        this.point = new Point(this);
    }

    public void setMaze(int[][] newMaze) {
        this.maze = newMaze;
    }

    public void generate() {
        for (int i = 0; i < height; i++) {
            maze[i][0] = 1;  // Левый край
            maze[i][width - 1] = 1;  // Правый край
        }

        for (int j = 0; j < width; j++) {
            maze[0][j] = 1;  // Верхний край
            maze[height - 1][j] = 1;  // Нижний край
        }

        for (int i = 0; i < height; i++) {
            Arrays.fill(maze[i], 1);
        }
        maze[1][1] = 0;
        dfs(1, 1);
        trap.place();
        point.place();
        createExit();
    }

    private void dfs(int x, int y) {
        int[] directions = {0, 1, 2, 3};
        shuffle(directions);

        for (int dir : directions) {
            int nx = x, ny = y;
            switch (dir) {
                case 0: nx = x + 2; break;
                case 1: ny = y + 2; break;
                case 2: nx = x - 2; break;
                case 3: ny = y - 2; break;
            }

            if (isInBoundsL(nx, ny) && maze[ny][nx] == 1) {
                maze[ny][nx] = 0;
                maze[(y + ny) / 2][(x + nx) / 2] = 0;
                dfs(nx, ny);
            }
        }
    }

    private void shuffle(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int j = random.nextInt(array.length);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void createExit() {
        Random rand = new Random();

        // Шаг 1: Выбираем случайную сторону периметра (верх, низ, левая, правая)
        int side = rand.nextInt(4); // 0 - верх, 1 - правый, 2 - низ, 3 - левый
        int x = 0, y = 0;

        // Шаг 2: На выбранной стороне периметра выбираем случайную клетку
        switch (side) {
            case 0:  // Верхняя сторона
                x = rand.nextInt(width - 2) + 1;  // Случайный x внутри
                y = 0;  // Верхний край
                break;
            case 1:  // Правая сторона
                x = width - 1;  // Правая колонка
                y = rand.nextInt(height - 2) + 1;  // Случайный y внутри
                break;
            case 2:  // Нижняя сторона
                x = rand.nextInt(width - 2) + 1;  // Случайный x внутри
                y = height - 1;  // Нижний край
                break;
            case 3:  // Левая сторона
                x = 0;  // Левая колонка
                y = rand.nextInt(height - 2) + 1;  // Случайный y внутри
                break;
        }

        // Шаг 3: Бурим по направлению, перпендикулярному стене
        maze[y][x] = 2;
        System.out.println(maze[y][x] + " " + y + " " + x);
        while (maze[y][x] != 0) {
            if (side == 0 || side == 2) {  // Верхняя или нижняя сторона (бурим по x)
                if (isInBounds(x, y + 1) && maze[y + 1][x] == 1) {
                    y++;  // Двигаемся вниз
                } else if (isInBounds(x, y - 1) && maze[y - 1][x] == 1) {
                    y--;  // Двигаемся вверх
                } else {
                    break;
                }
            } else if (side == 1 || side == 3) {  // Правая или левая сторона (бурим по y)
                if (isInBounds(x + 1, y) && maze[y][x + 1] == 1) {
                    x++;  // Двигаемся вправо
                } else if (isInBounds(x - 1, y) && maze[y][x - 1] == 1) {
                    x--;  // Двигаемся влево
                } else {
                    break;
                }
            }
            maze[y][x] = 0;  // Прокладываем путь
            System.out.println(maze[y][x] + " " + y + " " + x);
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    private boolean isInBoundsL(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1;
    }

    // Проверка наличия ловушки на заданной клетке
    public boolean hasTrapAt(int x, int y) {
        return trap.getTraps()[y][x] != 0;
    }

    // Проверка наличия точки на заданной клетке
    public boolean hasPointAt(int x, int y) {
        return point.getPoints()[y][x] == 1;
    }

    // Удаление точки с заданной клетки
    public void removePointAt(int x, int y) {
        point.getPoints()[y][x] = 0;
    }

    public boolean isValidMove(int x, int y) {
        // Проверяем, что клетка находится в пределах лабиринта и является проходимой
        return isInBounds(x, y) && (maze[y][x] == 0 || maze[y][x] == 2);
    }

    public void setTrapAt(int x, int y, int value) {
        trap.getTraps()[y][x] = value;
    }

    public void removeTrapAt(int x, int y) {
        trap.getTraps()[y][x] = 0;  // Убираем ловушку (false - означает отсутствие ловушки)
    }

    public void draw(Graphics g, int cellSize) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j] == 1) {
                    g.drawImage(cloudTexture, j * cellSize, i * cellSize, cellSize, cellSize, null);
                } else {
                    g.drawImage(cloudTextur, j * cellSize, i * cellSize, cellSize, cellSize, null);
                }
            }
        }
    }

    public boolean trapIsCorrect(int x, int y, int id) {
        return trap.getTraps()[y][x] != id;
    }
}
