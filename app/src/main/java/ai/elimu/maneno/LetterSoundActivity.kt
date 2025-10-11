package ai.elimu.maneno

import ai.elimu.analytics.utils.LearningEventUtil
import ai.elimu.common.utils.data.model.tts.QueueMode
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModel
import ai.elimu.common.utils.viewmodel.TextToSpeechViewModelImpl
import ai.elimu.content_provider.utils.ContentProviderUtil.getAllWordGsons
import ai.elimu.model.v2.gson.content.LetterGson
import ai.elimu.model.v2.gson.content.LetterSoundGson
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
import java.util.stream.Collectors

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

            if (word.letterSounds.size == 3) {
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
                val letterSound1 = currentWord.letterSounds.get(0)
                Log.i(TAG, "letterSound1: $letterSound1")
                val letterSound1Letters: String = letterSound1.letters.stream().map { obj: LetterGson -> obj.text }.collect(Collectors.joining())
                Log.i(this::class.simpleName, "letterSound1Letters: ${letterSound1Letters}")
                letterSound1TextView!!.text = " ${letterSound1Letters}"

                val letterSound2 = currentWord.letterSounds.get(1)
                Log.i(TAG, "letterSound2: $letterSound2")
                val letterSound2Letters: String = letterSound2.letters.stream().map { obj: LetterGson -> obj.text }.collect(Collectors.joining())
                Log.i(this::class.simpleName, "letterSound2Letters: ${letterSound2Letters}")
                letterSound2TextView!!.text = " ${letterSound2Letters}"

                val letterSound3 = currentWord.letterSounds.get(2)
                Log.i(TAG, "letterSound3: $letterSound3")
                val letterSound3Letters: String = letterSound3.letters.stream().map { obj: LetterGson -> obj.text }.collect(Collectors.joining())
                Log.i(this::class.simpleName, "letterSound3Letters: ${letterSound3Letters}")
                letterSound3TextView!!.text = " ${letterSound3Letters}"

                letterSound1TextView!!.visibility = View.VISIBLE
                letterSound1TextView!!.postDelayed(object : Runnable {
                    override fun run() {
                        playLetterNames(letterSound1)


                        letterSound2TextView!!.postDelayed(object : Runnable {
                            override fun run() {
                                letterSound2TextView!!.visibility = View.VISIBLE
                                letterSound2TextView!!.postDelayed(object : Runnable {
                                    override fun run() {
                                        playLetterNames(letterSound2)


                                        letterSound3TextView!!.postDelayed(object : Runnable {
                                            override fun run() {
                                                letterSound3TextView!!.visibility = View.VISIBLE
                                                letterSound3TextView!!.postDelayed(object : Runnable {
                                                    override fun run() {
                                                        playLetterNames(letterSound3)


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
    private fun playLetterNames(letterSound: LetterSoundGson) {
        Log.i(TAG, "playLetterNames")
        Log.i(this::class.simpleName, "letterSound.id: ${letterSound.id}")
        letterSound ?: return
        val letterSoundLetters: String = letterSound.letters.stream().map { obj: LetterGson -> obj.text }.collect(Collectors.joining())
        Log.i(this::class.simpleName, "letterSoundLetters: ${letterSoundLetters}")
        ttsViewModel.speak(text = letterSoundLetters, queueMode = QueueMode.FLUSH, utteranceId = UUID.randomUUID().toString())
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
