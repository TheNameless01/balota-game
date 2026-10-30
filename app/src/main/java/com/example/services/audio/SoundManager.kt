package com.example.services.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var clickId: Int = 0
    private var correctId: Int = 0
    private var incorrectId: Int = 0
    private var victoryId: Int = 0
    private var achievementId: Int = 0

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build()

            clickId = soundPool?.load(context, R.raw.sfx_click, 1) ?: 0
            correctId = soundPool?.load(context, R.raw.sfx_correct, 1) ?: 0
            incorrectId = soundPool?.load(context, R.raw.sfx_incorrect, 1) ?: 0
            victoryId = soundPool?.load(context, R.raw.sfx_victory, 1) ?: 0
            achievementId = soundPool?.load(context, R.raw.sfx_achievement, 1) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playClick(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled && clickId != 0) {
            try {
                soundPool?.play(clickId, 0.9f, 0.9f, 1, 0, 1.0f)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(18)
        }
    }

    fun playCorrect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled && correctId != 0) {
            try {
                soundPool?.play(correctId, 1.0f, 1.0f, 2, 0, 1.0f)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(45)
        }
    }

    fun playIncorrect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled && incorrectId != 0) {
            try {
                soundPool?.play(incorrectId, 0.9f, 0.9f, 2, 0, 1.0f)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrateDouble()
        }
    }

    fun playQuizComplete(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled && victoryId != 0) {
            try {
                soundPool?.play(victoryId, 1.0f, 1.0f, 3, 0, 1.0f)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(90)
        }
    }

    fun playAchievementUnlocked(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled && achievementId != 0) {
            try {
                soundPool?.play(achievementId, 1.0f, 1.0f, 3, 0, 1.0f)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(70)
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {
            // Ignore gracefully
        }
    }

    private fun vibrateDouble() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 35, 50, 35),
                            intArrayOf(0, 160, 0, 160),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 35, 50, 35), -1)
                }
            }
        } catch (e: Exception) {
            // Ignore gracefully
        }
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            // Ignore gracefully
        }
    }
}
