package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.sin

class SoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val pcmCache = ConcurrentHashMap<String, ShortArray>()
    private val activeTracks = AtomicInteger(0)

    @Volatile
    var enabled: Boolean = true

    private enum class Wave { SINE, TRIANGLE, SQUARE, SAWTOOTH }

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val MAX_CONCURRENT_TRACKS = 2

        private val AUDIO_ATTRIBUTES: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        private val AUDIO_FORMAT: AudioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        private val MIN_BUFFER_BYTES: Int by lazy {
            AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(1024)
        }
    }

    fun move() {
        playCached(
            cacheKey = "move",
            freq = 190.0,
            slide = -50.0,
            durationSec = 0.055,
            wave = Wave.TRIANGLE,
            volume = 0.22
        )
    }

    fun merge(value: Int) {
        val tier = max(1, (ln(max(2, value).toDouble()) / ln(2.0)).toInt()).coerceAtMost(14)
        val base = 240.0 + tier * 44.0
        playCached(
            cacheKey = "merge_a_$tier",
            freq = base,
            durationSec = 0.12,
            wave = Wave.SINE,
            volume = 0.28
        )
        playCached(
            cacheKey = "merge_b_$tier",
            freq = base * 1.5,
            durationSec = 0.10,
            wave = Wave.SINE,
            volume = 0.14,
            delayMs = 35L
        )
    }

    fun win() {
        val freqs = doubleArrayOf(523.0, 659.0, 784.0, 1046.0)
        freqs.forEachIndexed { i, f ->
            playCached(
                cacheKey = "win_$i",
                freq = f,
                durationSec = 0.24,
                wave = Wave.TRIANGLE,
                volume = 0.26,
                delayMs = i * 95L
            )
        }
    }

    fun over() {
        val freqs = doubleArrayOf(392.0, 330.0, 262.0, 196.0)
        freqs.forEachIndexed { i, f ->
            playCached(
                cacheKey = "over_$i",
                freq = f,
                durationSec = 0.25,
                wave = Wave.SINE,
                volume = 0.26,
                delayMs = i * 115L
            )
        }
    }

    fun click() {
        playCached(
            cacheKey = "click",
            freq = 540.0,
            durationSec = 0.045,
            wave = Wave.SQUARE,
            volume = 0.10
        )
    }

    fun nope() {
        playCached(
            cacheKey = "nope",
            freq = 140.0,
            slide = -30.0,
            durationSec = 0.08,
            wave = Wave.SAWTOOTH,
            volume = 0.15
        )
    }

    fun release() {
        scope.cancel()
        pcmCache.clear()
    }

    private fun playCached(
        cacheKey: String,
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
            if (activeTracks.get() >= MAX_CONCURRENT_TRACKS) return@launch

            activeTracks.incrementAndGet()
            var track: AudioTrack? = null
            try {
                val pcm = pcmCache.getOrPut(cacheKey) {
                    synthesizePcm(freq, slide, durationSec, wave, volume)
                }
                val bufferBytes = max(MIN_BUFFER_BYTES, pcm.size * 2)
                track = AudioTrack.Builder()
                    .setAudioAttributes(AUDIO_ATTRIBUTES)
                    .setAudioFormat(AUDIO_FORMAT)
                    .setBufferSizeInBytes(bufferBytes)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                delay((durationSec * 1000).toLong() + 45L)
                track.stop()
            } catch (_: Throwable) {
                // Ignore audio hardware unavailability gracefully
            } finally {
                try {
                    track?.release()
                } catch (_: Throwable) {
                }
                activeTracks.decrementAndGet()
            }
        }
    }

    private fun synthesizePcm(
        freq: Double,
        slide: Double,
        durationSec: Double,
        wave: Wave,
        volume: Double
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationSec).toInt().coerceAtLeast(1)
        val pcm = ShortArray(numSamples)
        val endFreq = max(40.0, freq + slide)
        val freqDelta = endFreq - freq
        val invSampleRate = 1.0 / SAMPLE_RATE
        val invNumSamples = 1.0 / numSamples
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i * invNumSamples
            val curFreq = freq + freqDelta * progress
            phase += twoPi * curFreq * invSampleRate
            val wrapped = (phase % twoPi) / twoPi
            val raw = when (wave) {
                Wave.SINE -> sin(phase)
                Wave.TRIANGLE -> 1.0 - 4.0 * abs(wrapped - 0.5)
                Wave.SQUARE -> if (wrapped < 0.5) 1.0 else -1.0
                Wave.SAWTOOTH -> 2.0 * wrapped - 1.0
            }

            val t = i * invSampleRate
            val attack = (t / 0.012).coerceAtMost(1.0)
            val decay = exp(-3.6 * progress) * (1.0 - progress)
            val sample = raw * volume * attack * decay
            pcm[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }
}
