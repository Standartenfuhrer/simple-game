package com.example.simplegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private ArrayList<GameObject> objects;
    private Paint paint;
    private Random random;
    private int score = 0;
    private int screenWidth, screenHeight;
    private boolean gameOver = false;
    private GameOverListener gameOverListener;

    public interface GameOverListener {
        void onGameOver();
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        objects = new ArrayList<>();
        paint = new Paint();
        random = new Random();
        resetGame();
    }

    public void resetGame() {
        score = 0;
        gameOver = false;
        objects.clear();
        for (int i = 0; i < 5; i++) {
            addBalloon();
        }
        for (int i = 0; i < 2; i++) {
            addBomb();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }

    private void addBalloon() {
        if (screenWidth == 0 || screenHeight == 0) return;

        int radius = random.nextInt(40) + 30;
        int x = random.nextInt(screenWidth - 2 * radius) + radius;
        int speed = random.nextInt(4) + 2;
        int color = Color.rgb(
                random.nextInt(200) + 55,
                random.nextInt(200) + 55,
                random.nextInt(200) + 55
        );

        objects.add(new Balloon(x, screenHeight + radius, radius, speed, color));
    }

    private void addBomb() {
        if (screenWidth == 0 || screenHeight == 0) return;

        int radius = random.nextInt(30) + 25;
        int x = random.nextInt(screenWidth - 2 * radius) + radius;
        int speed = random.nextInt(3) + 1;

        objects.add(new Bomb(x, screenHeight + radius, radius, speed));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        for (GameObject obj : objects) {
            if (obj instanceof Balloon) {
                Balloon balloon = (Balloon) obj;
                paint.setColor(balloon.color);
                canvas.drawCircle(balloon.x, balloon.y, balloon.radius, paint);

                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2);
                canvas.drawLine(
                        balloon.x, balloon.y + balloon.radius,
                        balloon.x, balloon.y + balloon.radius + 20,
                        paint
                );
            } else if (obj instanceof Bomb) {
                Bomb bomb = (Bomb) obj;
                paint.setColor(Color.BLACK);
                canvas.drawCircle(bomb.x, bomb.y, bomb.radius, paint);

                paint.setColor(Color.RED);
                canvas.drawCircle(bomb.x, bomb.y, bomb.radius - 5, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(30);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("💣", bomb.x, bomb.y + 10, paint);

                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(3);
                canvas.drawLine(
                        bomb.x, bomb.y + bomb.radius,
                        bomb.x, bomb.y + bomb.radius + 25,
                        paint
                );
            }
        }

        if (objects.size() < 4) {
            addBalloon();
        }
    }

    public void update() {
        if (gameOver) return;

        for (int i = objects.size() - 1; i >= 0; i--) {
            GameObject obj = objects.get(i);
            obj.y -= obj.speed;

            if (obj.y < -obj.radius) {
                objects.remove(i);
                if (obj instanceof Balloon) {
                    addBalloon();
                } else if (obj instanceof Bomb) {
                    addBomb();
                }
            }
        }

        if (objects.size() < 8 && random.nextInt(60) == 0) {
            if (random.nextBoolean()) {
                addBalloon();
            } else {
                addBomb();
            }
        }

        invalidate();
    }

    public boolean handleTouch(float x, float y) {
        if (gameOver) return false;

        for (int i = objects.size() - 1; i >= 0; i--) {
            GameObject obj = objects.get(i);
            float distance = (float) Math.sqrt(
                    Math.pow(x - obj.x, 2) + Math.pow(y - obj.y, 2)
            );

            if (distance <= obj.radius) {
                if (obj instanceof Balloon) {
                    objects.remove(i);
                    score += 10;
                    addBalloon();
                    if (random.nextInt(5) == 0) {
                        addBomb();
                    }
                } else if (obj instanceof Bomb) {
                    gameOver = true;
                    if (gameOverListener != null) {
                        gameOverListener.onGameOver();
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    private abstract class GameObject {
        float x, y;
        int radius;
        int speed;

        GameObject(float x, float y, int radius, int speed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
        }
    }

    private class Balloon extends GameObject {
        int color;

        Balloon(float x, float y, int radius, int speed, int color) {
            super(x, y, radius, speed);
            this.color = color;
        }
    }

    private class Bomb extends GameObject {
        Bomb(float x, float y, int radius, int speed) {
            super(x, y, radius, speed);
        }
    }
}