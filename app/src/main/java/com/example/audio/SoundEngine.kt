package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

class SoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var enabled: Boolean = true

    private enum class Wave { SINE, TRIANGLE, SQUARE, SAWTOOTH }

    fun move() {
        playTone(freq = 190.0, slide = -50.0, durationSec = 0.06, wave = Wave.TRIANGLE, volume = 0.22)
    }

    fun merge(value: Int) {
        val log2Val = max(1.0, ln(max(2, value).toDouble()) / ln(2.0))
        val base = 240.0 + log2Val * 44.0
        playTone(freq = base, durationSec = 0.14, wave = Wave.SINE, volume = 0.28)
        playTone(freq = base * 1.5, durationSec = 0.12, wave = Wave.SINE, volume = 0.14, delayMs = 35L)
    }

    fun win() {
        val freqs = doubleArrayOf(523.0, 659.0, 784.0, 1046.0)
        freqs.forEachIndexed { i, f ->
            playTone(freq = f, durationSec = 0.28, wave = Wave.TRIANGLE, volume = 0.26, delayMs = i * 100L)
        }
    }

    fun over() {
        val freqs = doubleArrayOf(392.0, 330.0, 262.0, 196.0)
        freqs.forEachIndexed { i, f ->
            playTone(freq = f, durationSec = 0.30, wave = Wave.SINE, volume = 0.26, delayMs = i * 120L)
        }
    }

    fun click() {
        playTone(freq = 540.0, durationSec = 0.05, wave = Wave.SQUARE, volume = 0.10)
    }

    fun nope() {
        playTone(freq = 140.0, slide = -30.0, durationSec = 0.09, wave = Wave.SAWTOOTH, volume = 0.15)
    }

    private fun playTone(
        freq: Double,
        slide: Double = 0.0,
        durationSec: Double,
        wave: Wave,
        volume: Double,
        delayMs: Long = 0L
    ) {
        if (!enabled) return
        scope.launch {
            if (delayMs > 0L) delay(delayMs)
            if (!enabled) return@launch
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * durationSec).toInt().coerceAtLeast(1)
                val pcm = ShortArray(numSamples)
                val endFreq = max(40.0, freq + slide)
                var phase = 0.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val curFreq = if (slide != 0.0) {
                        freq * (endFreq / freq).pow(progress)
                    } else {
                        freq
                    }
                    phase += 2.0 * PI * curFreq / sampleRate
                    val wrapped = (phase % (2.0 * PI)) / (2.0 * PI)
                    val raw = when (wave) {
                        Wave.SINE -> sin(phase)
                        Wave.TRIANGLE -> 1.0 - 4.0 * abs(wrapped - 0.5)
                        Wave.SQUARE -> if (wrapped < 0.5) 1.0 else -1.0
                        Wave.SAWTOOTH -> 2.0 * wrapped - 1.0
                    }

                    val attack = (t / 0.012).coerceAtMost(1.0)
                    val decay = exp(-3.6 * progress) * (1.0 - progress).coerceAtLeast(0.0)
                    val sample = raw * volume * attack * decay
                    pcm[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                }

                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferBytes = max(minBuf, pcm.size * 2)
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferBytes)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                delay((durationSec * 1000).toLong() + 60L)
                track.stop()
                track.release()
            } catch (_: Throwable) {
                // Ignore audio hardware unavailability gracefully
            }
        }
    }
}
