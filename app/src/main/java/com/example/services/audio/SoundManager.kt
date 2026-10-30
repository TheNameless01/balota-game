package com.example.services.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
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
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playClick(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(20)
        }
    }

    fun playCorrect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(50)
        }
    }

    fun playIncorrect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 150)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrateDouble()
        }
    }

    fun playQuizComplete(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(100)
        }
    }

    fun playAchievementUnlocked(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
        if (hapticsEnabled) {
            vibrate(80)
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
                            longArrayOf(0, 40, 60, 40),
                            intArrayOf(0, 180, 0, 180),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 40, 60, 40), -1)
                }
            }
        } catch (e: Exception) {
            // Ignore gracefully
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // Ignore gracefully
        }
    }
}
