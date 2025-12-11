package com.example.simplegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

// GameView - кастомный View для отрисовки игры "Опасные шарики"
// Наследуется от View, чтобы можно было рисовать на экране
public class GameView extends View {
    
    // Список всех игровых объектов на экране
    private ArrayList<GameObject> objects;
    
    // Кисть для рисования графики
    private Paint paint;
    
    // Генератор случайных чисел
    private Random random;
    
    // Текущий счет игрока
    private int score = 0;
    
    // Размеры экрана
    private int screenWidth, screenHeight;
    
    // Флаг окончания игры
    private boolean gameOver = false;
    
    // Слушатель события окончания игры
    private GameOverListener gameOverListener;
    
    // Интерфейс для связи с Activity
    public interface GameOverListener {
        void onGameOver();
    }
    
    // Установка слушателя
    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }
    
    // Конструктор 1 - для создания View из кода
    public GameView(Context context) {
        super(context);
        init();
    }
    
    // Конструктор 2 - для создания View из XML
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    // Конструктор 3 - для создания View из XML со стилями
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    // Инициализация View
    private void init() {
        // Создаем список для объектов
        objects = new ArrayList<>();
        
        // Создаем кисть для рисования
        paint = new Paint();
        
        // Создаем генератор случайных чисел
        random = new Random();
        
        // Начинаем новую игру
        resetGame();
    }
    
    // Сброс игры в начальное состояние
    public void resetGame() {
        // Обнуляем счет
        score = 0;
        
        // Сбрасываем флаг окончания игры
        gameOver = false;
        
        // Очищаем список объектов
        objects.clear();
        
        // Добавляем начальные шарики
        for (int i = 0; i < 5; i++) {
            addBalloon();
        }
        
        // Добавляем начальные бомбы
        for (int i = 0; i < 2; i++) {
            addBomb();
        }
    }
    
    // Метод вызывается при изменении размера View
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Сохраняем новые размеры
        screenWidth = w;
        screenHeight = h;
    }
    
    // Добавление нового шарика
    private void addBalloon() {
        // Проверяем, известны ли размеры экрана
        if (screenWidth == 0 || screenHeight == 0) return;
        
        // Случайный радиус шарика от 30 до 70 пикселей
        int radius = random.nextInt(40) + 30;
        
        // Случайная позиция X (в пределах экрана)
        int x = random.nextInt(screenWidth - 2 * radius) + radius;
        
        // Случайная скорость от 2 до 5 пикселей/кадр
        int speed = random.nextInt(4) + 2;
        
        // Случайный цвет шарика
        int color = Color.rgb(
            random.nextInt(200) + 55,
            random.nextInt(200) + 55,
            random.nextInt(200) + 55
        );
        
        // Создаем и добавляем шарик
        // Начальная позиция Y - ниже экрана, чтобы появлялся снизу
        objects.add(new Balloon(x, screenHeight + radius, radius, speed, color));
    }
    
    // Добавление новой бомбы
    private void addBomb() {
        // Проверяем, известны ли размеры экрана
        if (screenWidth == 0 || screenHeight == 0) return;
        
        // Случайный радиус бомбы от 25 до 55 пикселей
        int radius = random.nextInt(30) + 25;
        
        // Случайная позиция X
        int x = random.nextInt(screenWidth - 2 * radius) + radius;
        
        // Скорость бомбы от 1 до 3 пикселей/кадр (медленнее шариков)
        int speed = random.nextInt(3) + 1;
        
        // Создаем и добавляем бомбу
        objects.add(new Bomb(x, screenHeight + radius, radius, speed));
    }
    
    // Метод отрисовки - вызывается системой для перерисовки View
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Рисуем белый фон
        canvas.drawColor(Color.WHITE);
        
        // Рисуем все объекты
        for (GameObject obj : objects) {
            if (obj instanceof Balloon) {
                // Если объект - шарик
                Balloon balloon = (Balloon) obj;
                
                // Рисуем круг шарика
                paint.setColor(balloon.color);
                canvas.drawCircle(balloon.x, balloon.y, balloon.radius, paint);
                
                // Рисуем ниточку шарика
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2);
                canvas.drawLine(
                    balloon.x, balloon.y + balloon.radius,
                    balloon.x, balloon.y + balloon.radius + 20,
                    paint
                );
            } else if (obj instanceof Bomb) {
                // Если объект - бомба
                Bomb bomb = (Bomb) obj;
                
                // Рисуем черный внешний круг бомбы
                paint.setColor(Color.BLACK);
                canvas.drawCircle(bomb.x, bomb.y, bomb.radius, paint);
                
                // Рисуем красный внутренний круг
                paint.setColor(Color.RED);
                canvas.drawCircle(bomb.x, bomb.y, bomb.radius - 5, paint);
                
                // Рисуем эмодзи бомбы
                paint.setColor(Color.WHITE);
                paint.setTextSize(30);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("💣", bomb.x, bomb.y + 10, paint);
                
                // Рисуем провод бомбы
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(3);
                canvas.drawLine(
                    bomb.x, bomb.y + bomb.radius,
                    bomb.x, bomb.y + bomb.radius + 25,
                    paint
                );
            }
        }
        
        // Если объектов мало, добавляем новый шарик
        if (objects.size() < 4) {
            addBalloon();
        }
    }
    
    // Обновление игрового состояния - вызывается каждый кадр
    public void update() {
        // Если игра окончена, ничего не обновляем
        if (gameOver) return;
        
        // Обновляем позиции всех объектов
        for (int i = objects.size() - 1; i >= 0; i--) {
            GameObject obj = objects.get(i);
            
            // Двигаем объект вверх
            obj.y -= obj.speed;
            
            // Если объект улетел за верх экрана
            if (obj.y < -obj.radius) {
                // Удаляем объект
                objects.remove(i);
                
                // Добавляем новый объект того же типа
                if (obj instanceof Balloon) {
                    addBalloon();
                } else if (obj instanceof Bomb) {
                    addBomb();
                }
            }
        }
        
        // Случайное добавление новых объектов
        if (objects.size() < 8 && random.nextInt(60) == 0) {
            // Случайно выбираем тип нового объекта
            if (random.nextBoolean()) {
                addBalloon();
            } else {
                addBomb();
            }
        }
        
        // Запрос на перерисовку
        invalidate();
    }
    
    // Обработка касания экрана
    // Возвращает true, если игра окончена (нажали на бомбу)
    public boolean handleTouch(float x, float y) {
        // Если игра окончена, ничего не делаем
        if (gameOver) return false;
        
        // Проверяем все объекты
        for (int i = objects.size() - 1; i >= 0; i--) {
            GameObject obj = objects.get(i);
            
            // Вычисляем расстояние от точки касания до центра объекта
            float distance = (float) Math.sqrt(
                Math.pow(x - obj.x, 2) + Math.pow(y - obj.y, 2)
            );
            
            // Если касание попало в объект
            if (distance <= obj.radius) {
                if (obj instanceof Balloon) {
                    // Если нажали на шарик
                    objects.remove(i);  // Удаляем шарик
                    score += 10;        // Добавляем очки
                    addBalloon();       // Добавляем новый шарик
                    
                    // С шансом 20% добавляем бомбу
                    if (random.nextInt(5) == 0) {
                        addBomb();
                    }
                } else if (obj instanceof Bomb) {
                    // Если нажали на бомбу
                    gameOver = true;  // Игра окончена
                    
                    // Уведомляем слушателя
                    if (gameOverListener != null) {
                        gameOverListener.onGameOver();
                    }
                    return true;  // Возвращаем true - игра окончена
                }
                break;  // Обрабатываем только один объект за касание
            }
        }
        return false;  // Игра продолжается
    }
    
    // Получение текущего счета
    public int getScore() {
        return score;
    }
    
    
    // Базовый класс для всех игровых объектов
    private abstract class GameObject {
        float x, y;     // Координаты центра
        int radius;     // Радиус объекта
        int speed;      // Скорость движения
        
        GameObject(float x, float y, int radius, int speed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
        }
    }
    
    // Класс шарика
    private class Balloon extends GameObject {
        int color;  // Цвет шарика
        
        Balloon(float x, float y, int radius, int speed, int color) {
            super(x, y, radius, speed);
            this.color = color;
        }
    }
    
    // Класс бомбы
    private class Bomb extends GameObject {
        Bomb(float x, float y, int radius, int speed) {
            super(x, y, radius, speed);
        }
    }
}
