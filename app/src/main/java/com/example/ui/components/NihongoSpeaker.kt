package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class NihongoSpeaker(private val tts: TextToSpeech?) {
    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}

@Composable
fun rememberNihongoSpeaker(): NihongoSpeaker {
    val context = LocalContext.current
    val tts = remember {
        var ttsRef: TextToSpeech? = null
        ttsRef = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsRef?.apply {
                    language = Locale.JAPANESE
                    setPitch(1.0f)
                    setSpeechRate(0.80f) // Slower pacing for optimal listening clarity
                }
            }
        }
        ttsRef
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    return remember(tts) { NihongoSpeaker(tts) }
}
