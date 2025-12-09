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
    private ArrayList<Balloon> balloons;
    private Paint paint;
    private Random random;
    private int score = 0;
    private int screenWidth, screenHeight;

    // Конструкторы
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
        balloons = new ArrayList<>();
        paint = new Paint();
        random = new Random();
        resetGame();
    }

    public void resetGame() {
        score = 0;
        balloons.clear();
        // Создаем первые шарики
        for (int i = 0; i < 5; i++) {
            addBalloon();
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

        int radius = random.nextInt(50) + 30;
        int x = random.nextInt(screenWidth - 2 * radius) + radius;
        int speed = random.nextInt(5) + 2;
        int color = Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );

        balloons.add(new Balloon(x, screenHeight + radius, radius, speed, color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Фон
        canvas.drawColor(Color.WHITE);

        // Рисуем шарики
        for (Balloon balloon : balloons) {
            paint.setColor(balloon.color);
            canvas.drawCircle(balloon.x, balloon.y, balloon.radius, paint);

            // Нить шарика
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            canvas.drawLine(
                    balloon.x, balloon.y + balloon.radius,
                    balloon.x, balloon.y + balloon.radius + 30,
                    paint
            );
        }

        // Если шариков мало, добавляем еще
        if (balloons.size() < 3) {
            addBalloon();
        }
    }

    public void update() {
        // Обновляем позиции шариков
        for (int i = balloons.size() - 1; i >= 0; i--) {
            Balloon balloon = balloons.get(i);
            balloon.y -= balloon.speed;

            // Если шарик улетел вверх
            if (balloon.y < -balloon.radius) {
                balloons.remove(i);
                addBalloon();
            }
        }

        // Добавляем новые шарики иногда
        if (balloons.size() < 8 && random.nextInt(50) == 0) {
            addBalloon();
        }

        // Перерисовываем
        invalidate();
    }

    public void handleTouch(float x, float y) {
        for (int i = balloons.size() - 1; i >= 0; i--) {
            Balloon balloon = balloons.get(i);
            float distance = (float) Math.sqrt(
                    Math.pow(x - balloon.x, 2) + Math.pow(y - balloon.y, 2)
            );

            if (distance <= balloon.radius) {
                balloons.remove(i);
                score += 10;
                addBalloon();
                break;
            }
        }
    }

    public int getScore() {
        return score;
    }

    // Класс шарика
    private class Balloon {
        float x, y;
        int radius;
        int speed;
        int color;

        Balloon(float x, float y, int radius, int speed, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
            this.color = color;
        }
    }
}