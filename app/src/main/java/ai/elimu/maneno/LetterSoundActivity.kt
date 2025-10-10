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

    private var letter1TextView: TextView? = null
    private var letter2TextView: TextView? = null
    private var letter3TextView: TextView? = null

    private var nextButton: ImageButton? = null
    private lateinit var ttsViewModel: TextToSpeechViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        ttsViewModel = ViewModelProvider(this)[TextToSpeechViewModelImpl::class.java]

        setContentView(R.layout.activity_letter_sound)

        wordsSeen = ArrayList<WordGson?>()

        letter1TextView = findViewById<View?>(R.id.letter1TextView) as TextView
        letter2TextView = findViewById<View?>(R.id.letter2TextView) as TextView
        letter3TextView = findViewById<View?>(R.id.letter3TextView) as TextView

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

            if (word.letterSounds.size == 3) {
                wordsWith3LetterSounds!!.add(word)
            }

            if (wordsWith3LetterSounds!!.size == 10) {
                break
            }
        }
        Log.i(TAG, "wordsWith3Letters.size: " + wordsWith3LetterSounds!!.size)
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

        letter1TextView!!.visibility = View.INVISIBLE
        letter2TextView!!.visibility = View.INVISIBLE
        letter3TextView!!.visibility = View.INVISIBLE
        nextButton!!.setVisibility(View.INVISIBLE)

        val currentWord = wordsWith3LetterSounds!![wordsSeen!!.size]
        Log.i(TAG, "currentWord: " + currentWord)

        letter1TextView!!.postDelayed(object : Runnable {
            override fun run() {
                val letter1 = currentWord.text.substring(0, 1)
                Log.i(TAG, "letter1: $letter1")
                letter1TextView!!.text = " ${letter1}"

                val letter2 = currentWord.text.substring(1, 2)
                Log.i(TAG, "letter2: $letter2")
                letter2TextView!!.text = " ${letter2}"

                val letter3 = currentWord.text.substring(2, 3)
                Log.i(TAG, "letter3: $letter3")
                letter3TextView!!.text = " ${letter3}"

                letter1TextView!!.visibility = View.VISIBLE
                letter1TextView!!.postDelayed(object : Runnable {
                    override fun run() {
                        playLetterSound(letter1)


                        letter2TextView!!.postDelayed(object : Runnable {
                            override fun run() {
                                letter2TextView!!.visibility = View.VISIBLE
                                letter2TextView!!.postDelayed(object : Runnable {
                                    override fun run() {
                                        playLetterSound(letter2)


                                        letter3TextView!!.postDelayed(object : Runnable {
                                            override fun run() {
                                                letter3TextView!!.visibility = View.VISIBLE
                                                letter3TextView!!.postDelayed(object : Runnable {
                                                    override fun run() {
                                                        playLetterSound(letter3)


                                                        letter3TextView!!.postDelayed(object :
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

    private fun playLetterSound(letter: String?) {
        Log.i(TAG, "playLetterSound: $letter")
        letter ?: return
        ttsViewModel.speak(text = letter,
            queueMode = QueueMode.FLUSH, utteranceId = UUID.randomUUID().toString())
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
