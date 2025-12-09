package com.example.simplegame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView scoreText, bestScoreText;
    private LinearLayout menuLayout, gameHud;
    private Button btnStart, btnRestart, btnPause, btnMenu;
    private Handler gameHandler;
    private Runnable gameRunnable;
    private boolean isGameRunning = false;
    private boolean isPaused = false;
    private int bestScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadBestScore();
        setupClickListeners();
    }

    private void initViews() {
        gameView = findViewById(R.id.gameView);
        scoreText = findViewById(R.id.scoreText);
        bestScoreText = findViewById(R.id.bestScoreText);
        menuLayout = findViewById(R.id.menuLayout);
        gameHud = findViewById(R.id.gameHud);

        btnStart = findViewById(R.id.btnStart);
        btnRestart = findViewById(R.id.btnRestart);
        btnPause = findViewById(R.id.btnPause);
        btnMenu = findViewById(R.id.btnMenu);
    }

    private void loadBestScore() {
        // Загружаем рекорд из SharedPreferences
        bestScore = getSharedPreferences("GamePrefs", MODE_PRIVATE)
                .getInt("best_score", 0);
        bestScoreText.setText("Рекорд: " + bestScore);
    }

    private void saveBestScore(int score) {
        if (score > bestScore) {
            bestScore = score;
            getSharedPreferences("GamePrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("best_score", bestScore)
                    .apply();
            bestScoreText.setText("Рекорд: " + bestScore);
        }
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> startGame());

        btnRestart.setOnClickListener(v -> {
            restartGame();
            menuLayout.setVisibility(View.GONE);
            gameView.setVisibility(View.VISIBLE);
            gameHud.setVisibility(View.VISIBLE);
        });

        btnPause.setOnClickListener(v -> togglePause());

        btnMenu.setOnClickListener(v -> showMenu());

        gameView.setOnTouchListener((v, event) -> {
            if (isGameRunning && !isPaused && event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                gameView.handleTouch(event.getX(), event.getY());
                updateScore();
                return true;
            }
            return false;
        });
    }

    private void startGame() {
        menuLayout.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);
        gameHud.setVisibility(View.VISIBLE);

        isGameRunning = true;
        isPaused = false;
        btnPause.setText("⏸");

        startGameLoop();
    }

    private void restartGame() {
        stopGameLoop();
        gameView.resetGame();
        updateScore();
        startGame();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            btnPause.setText("▶");
        } else {
            btnPause.setText("⏸");
        }
    }

    private void showMenu() {
        stopGameLoop();
        isGameRunning = false;

        // Сохраняем рекорд
        saveBestScore(gameView.getScore());

        // Показываем меню
        gameView.setVisibility(View.GONE);
        gameHud.setVisibility(View.GONE);
        menuLayout.setVisibility(View.VISIBLE);

        // Показываем кнопку "Заново"
        btnRestart.setVisibility(View.VISIBLE);
        btnStart.setText("Продолжить");

        // Обновляем счет в меню
        bestScoreText.setText("Рекорд: " + bestScore + "\nТекущий: " + gameView.getScore());
    }

    private void startGameLoop() {
        gameHandler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (isGameRunning && !isPaused) {
                    gameView.update();
                    gameView.invalidate(); // Перерисовка
                }
                gameHandler.postDelayed(this, 16); // ~60 FPS
            }
        };
        gameHandler.post(gameRunnable);
    }

    private void stopGameLoop() {
        if (gameHandler != null && gameRunnable != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
    }

    private void updateScore() {
        scoreText.setText("Очки: " + gameView.getScore());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGameRunning) {
            isPaused = true;
            btnPause.setText("▶");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // При возобновлении обновляем лучший счет
        loadBestScore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGameLoop();
        saveBestScore(gameView.getScore());
    }
}