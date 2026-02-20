package ai.elimu.maneno

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.common.utils.data.model.tts.QueueMode
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModel
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModelImpl
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllWordGsons
import ai.elimu.model.v2.gson.content.WordGson
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.UUID

@AndroidEntryPoint
class LetterSoundActivity : AppCompatActivity() {
    
    private val TAG = "LetterSoundActivity"
    private var wordsWith3LetterSounds: MutableList<WordGson>? = null

    private var wordsSeen: MutableList<WordGson?>? = null

    private var letterSound1TextView: TextView? = null
    private var letterSound2TextView: TextView? = null
    private var letterSound3TextView: TextView? = null

    private var nextButton: ImageButton? = null
    private lateinit var ttsViewModel: TextToSpeechViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        ttsViewModel = ViewModelProvider(this)[TextToSpeechViewModelImpl::class.java]

        setContentView(R.layout.activity_letter_sound)

        wordsSeen = ArrayList<WordGson?>()

        letterSound1TextView = findViewById<View?>(R.id.letterSound1TextView) as TextView
        letterSound2TextView = findViewById<View?>(R.id.letterSound2TextView) as TextView
        letterSound3TextView = findViewById<View?>(R.id.letterSound3TextView) as TextView

        nextButton = findViewById<View?>(R.id.nextButton) as ImageButton
        nextButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.i(TAG, "nextButton onClick")

                loadNextWord()
            }
        })

        val allWords: List<WordGson> = getAllWordGsons(applicationContext, BuildConfig.CONTENT_PROVIDER_APPLICATION_ID)
        Log.i(TAG, "allWords.size: " + allWords.size)

        wordsWith3LetterSounds = ArrayList<WordGson>()
        for (word in allWords) {
            Log.i(TAG, "word: " + word + ", word.wordType: " + word.wordType)

            if (word.text.length == 3) {
                wordsWith3LetterSounds!!.add(word)
            }

            if (wordsWith3LetterSounds!!.size == 10) {
                break
            }
        }
        Log.i(TAG, "wordsWith3LetterSounds.size: " + wordsWith3LetterSounds!!.size)
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        loadNextWord()
    }

    private fun loadNextWord() {
        Log.i(TAG, "loadNextWord")

        if (wordsSeen!!.size == wordsWith3LetterSounds!!.size) {
            // TODO: show congratulations page
            finish()
            return
        }

        letterSound1TextView!!.visibility = View.INVISIBLE
        letterSound2TextView!!.visibility = View.INVISIBLE
        letterSound3TextView!!.visibility = View.INVISIBLE
        nextButton!!.setVisibility(View.INVISIBLE)

        val currentWord = wordsWith3LetterSounds!![wordsSeen!!.size]
        Log.i(TAG, "currentWord: " + currentWord)

        letterSound1TextView!!.postDelayed(object : Runnable {
            override fun run() {
                val letter1 = currentWord.text.substring(0, 1)
                Log.i(TAG, "letter1: $letter1")
                letterSound1TextView!!.text = letter1

                val letter2 = currentWord.text.substring(1, 2)
                Log.i(TAG, "letter2: $letter2")
                letterSound2TextView!!.text = letter2

                val letter3 = currentWord.text.substring(2, 3)
                Log.i(TAG, "letter3: $letter3")
                letterSound3TextView!!.text = letter3

                letterSound1TextView!!.visibility = View.VISIBLE
                letterSound1TextView!!.postDelayed(object : Runnable {
                    override fun run() {
                        playLetterName(letter1)


                        letterSound2TextView!!.postDelayed(object : Runnable {
                            override fun run() {
                                letterSound2TextView!!.visibility = View.VISIBLE
                                letterSound2TextView!!.postDelayed(object : Runnable {
                                    override fun run() {
                                        playLetterName(letter2)


                                        letterSound3TextView!!.postDelayed(object : Runnable {
                                            override fun run() {
                                                letterSound3TextView!!.visibility = View.VISIBLE
                                                letterSound3TextView!!.postDelayed(object : Runnable {
                                                    override fun run() {
                                                        playLetterName(letter3)


                                                        letterSound3TextView!!.postDelayed(object :
                                                            Runnable {
                                                            override fun run() {
                                                                playWord(currentWord)
                                                                wordsSeen!!.add(currentWord)
                                                                nextButton!!.setVisibility(View.VISIBLE)
                                                            }
                                                        }, 2000)
                                                    }
                                                }, 1000)
                                            }
                                        }, 2000)
                                    }
                                }, 1000)
                            }
                        }, 2000)
                    }
                }, 1000)
            }
        }, 1000)
    }

    /**
     * Speaks the names of the letter(s), e.g. /nɔɔ-nǔu/ for "น".
     *
     * TODO: Skip letters that are diacritics.
     *
     * TODO: Once the TTS engine gets better support for speaking IPA symbols, switch from playing
     * the letter names to playing the actual sounds.
     */
    private fun playLetterName(letter: String?) {
        Log.i(TAG, "playLetterName: ${letter}")
        letter ?: return
        ttsViewModel.speak(text = letter, queueMode = QueueMode.FLUSH, utteranceId = UUID.randomUUID().toString())
    }

    private fun playWord(word: WordGson?) {
        Log.i(TAG, "playWord: $word")
        val spokenText = word?.text ?: return
        val utteranceId = word.id?.toString() ?: UUID.randomUUID().toString()
        ttsViewModel.speak(text = spokenText, queueMode = QueueMode.FLUSH, utteranceId = utteranceId)

        LearningEventUtil.reportWordLearningEvent(
            wordGson = word,
            additionalData = JSONObject().apply {
                put("spokenText", spokenText)
            },
            context = applicationContext,
            analyticsApplicationId = BuildConfig.ANALYTICS_APPLICATION_ID
        )
    }
}
