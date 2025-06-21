package com.example.tclengineeringmenu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.DataOutputStream

class MainActivity : AppCompatActivity() {

    private val tag = "EngineeringMenuLauncher"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Найдём элементы интерфейса
        val logo = findViewById<ImageView>(R.id.tcl_logo)
        val btnRootMode = findViewById<Button>(R.id.btn_root_mode)
        val btnNoRootMode = findViewById<Button>(R.id.btn_no_root_mode)
        val tvPinHint = findViewById<TextView>(R.id.tv_pin_hint)

        // Анимация появления логотипа
        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(500).start()

        // 🔘 Root Mode
        btnRootMode.setOnClickListener {
            if (isRootAvailable()) {
                launchWithRoot()
            } else {
                Toast.makeText(this, "Root недоступен", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔘 No Root Mode
        btnNoRootMode.setOnClickListener {
            tvPinHint.visibility = View.VISIBLE
            launchWithoutRoot()
        }
    }

    // 🔍 Проверка root
    private fun isRootAvailable(): Boolean {
        return try {
            ProcessBuilder().command("su", "-c", "echo 'root ok'").start().waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    // 🚀 Запуск инженерного меню через root
    private fun launchWithRoot() {
        try {
            val process = ProcessBuilder().command("su").start()
            val outputStream = DataOutputStream(process.outputStream)

            Log.d(tag, "Запускаем инженерное меню через root...")
            outputStream.writeBytes("am start -a com.tcl.factory.intent.action.FACTORYMENU -n com.tcl.factory.view/.MainMenu\n")
            outputStream.flush()

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    sendKeyEvents(outputStream, listOf(
                        "KEYCODE_1",
                        "KEYCODE_9",
                        "KEYCODE_5",
                        "KEYCODE_0"
                    ))

                    // Подтверждение
                    outputStream.writeBytes("input keyevent KEYCODE_ENTER\n")
                    outputStream.writeBytes("input keyevent KEYCODE_BACK\n")
                    outputStream.writeBytes("input keyevent KEYCODE_DPAD_RIGHT\n")
                    outputStream.writeBytes("input keyevent KEYCODE_DPAD_CENTER\n")
                    outputStream.writeBytes("exit\n")
                    outputStream.flush()

                } catch (e: Exception) {
                    Log.e("AutoInput", "Ошибка при автовводе", e)
                }

                finishAndRemoveTask()

            }, 1200)

        } catch (e: Exception) {
            Log.e("LaunchError", "Не удалось выполнить команду", e)
            Toast.makeText(this, "Ошибка root-доступа", Toast.LENGTH_LONG).show()
        }
    }

    // 📱 Запуск без root
    private fun launchWithoutRoot() {
        try {
            val intent = Intent().apply {
                action = "com.tcl.factory.intent.action.FACTORYMENU"
                setClassName("com.tcl.factory.view", "com.tcl.factory.view.MainMenu")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть инженерное меню", Toast.LENGTH_SHORT).show()
        }
    }

    // 📡 Отправка событий ввода
    private fun sendKeyEvents(stream: DataOutputStream, keys: List<String>, delayMs: Long = 20) {
        for (key in keys) {
            stream.writeBytes("input keyevent $key\n")
            stream.flush()
            Thread.sleep(delayMs)
        }
    }
}
