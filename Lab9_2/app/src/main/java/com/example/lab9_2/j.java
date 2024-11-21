package com.example.lab9_2;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // 建立變數以利後續綁定元件
    private Button btnCalculate;
    private EditText edHeight, edWeight, edAge;
    private TextView tvWeightResult, tvFatResult, tvBmiResult, tvProgress;
    private ProgressBar progressBar;
    private LinearLayout llProgress;
    private RadioButton btnBoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 將變數與 XML 元件綁定
        btnCalculate = findViewById(R.id.btnCalculate);
        edHeight = findViewById(R.id.edHeight);
        edWeight = findViewById(R.id.edWeight);
        edAge = findViewById(R.id.edAge);
        tvWeightResult = findViewById(R.id.tvWeightResult);
        tvFatResult = findViewById(R.id.tvFatResult);
        tvBmiResult = findViewById(R.id.tvBmiResult);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        llProgress = findViewById(R.id.llProgress);
        btnBoy = findViewById(R.id.btnBoy);

        // 對計算按鈕設定監聽器
        btnCalculate.setOnClickListener(v -> {
            if (edHeight.getText().toString().isEmpty()) {
                showToast("請輸入身高");
            } else if (edWeight.getText().toString().isEmpty()) {
                showToast("請輸入體重");
            } else if (edAge.getText().toString().isEmpty()) {
                showToast("請輸入年齡");
            } else {
                runThread(); // 執行 runThread 方法
            }
        });
    }

    // 建立 showToast 方法顯示 Toast 訊息
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 用 Thread 模擬檢測過程
    private void runThread() {
        tvWeightResult.setText("標準體重\n無");
        tvFatResult.setText("體脂肪\n無");
        tvBmiResult.setText("BMI\n無");

        // 初始化進度條
        progressBar.setProgress(0);
        tvProgress.setText("0%");
        llProgress.setVisibility(View.VISIBLE);

        // 建立執行緒
        new Thread(() -> {
            int progress = 0;
            // 建立迴圈執行一百次共延長五秒
            while (progress < 100) {
                try {
                    Thread.sleep(50); // 執行緒延遲 50ms 後執行
                } catch (InterruptedException ignored) {
                }
                progress++; // 計數加一

                // 切換到 Main Thread 執行進度更新
                int finalProgress = progress;
                runOnUiThread(() -> {
                    progressBar.setProgress(finalProgress);
                    tvProgress.setText(finalProgress + "%");
                });
            }

            // 計算標準體重與體脂肪
            double height = Double.parseDouble(edHeight.getText().toString()); // 身高
            double weight = Double.parseDouble(edWeight.getText().toString()); // 體重
            double age = Double.parseDouble(edAge.getText().toString()); // 年齡
            double bmi = weight / Math.pow(height / 100, 2); // BMI

            double standWeight, bodyFat;
            if (btnBoy.isChecked()) {
                standWeight = (height - 80) * 0.7;
                bodyFat = 1.39 * bmi + 0.16 * age - 19.34;
            } else {
                standWeight = (height - 70) * 0.6;
                bodyFat = 1.39 * bmi + 0.16 * age - 9;
            }

            // 切換到 Main Thread 更新畫面
            runOnUiThread(() -> {
                llProgress.setVisibility(View.GONE);
                tvWeightResult.setText("標準體重 \n" + String.format("%.2f", standWeight));
                tvFatResult.setText("體脂肪 \n" + String.format("%.2f", bodyFat));
                tvBmiResult.setText("BMI \n" + String.format("%.2f", bmi));
            });
        }).start();
    }

    private void enableEdgeToEdge() {
        // Enable edge-to-edge display. This method can include additional configuration if needed.
    }
}
