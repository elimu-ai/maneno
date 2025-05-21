package ai.elimu.maneno

import ai.elimu.content_provider.utils.ContentProviderUtil.getAllWordGsons
import ai.elimu.model.v2.gson.content.WordGson
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LetterSoundActivity : AppCompatActivity() {
    
    private val TAG = "LetterSoundActivity"
    private var wordsWith3Letters: MutableList<WordGson>? = null

    private var wordsSeen: MutableList<WordGson?>? = null

    private var word1TextView: TextView? = null
    private var word2TextView: TextView? = null
    private var word3TextView: TextView? = null

    private var nextButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_letter_sound)

        wordsSeen = ArrayList<WordGson?>()

        word1TextView = findViewById<View?>(R.id.word1TextView) as TextView
        word2TextView = findViewById<View?>(R.id.word2TextView) as TextView
        word3TextView = findViewById<View?>(R.id.word3TextView) as TextView

        nextButton = findViewById<View?>(R.id.nextButton) as ImageButton
        nextButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.i(TAG, "nextButton onClick")

                loadNextWord()
            }
        })

        val allWords: List<WordGson> =
            getAllWordGsons(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID)
        // TODO: dynamically fetch words that only contain the student's unlocked letter-sound correspondences
        Log.i(TAG, "allWords.size(): " + allWords.size)

        // TODO: filter words by SpellingConsistency?
        wordsWith3Letters = ArrayList<WordGson>()
        for (word in allWords) {
            Log.i(
                TAG,
                "word.getText(): " + word.getText() + ", word.getWordType(): " + word.getWordType()
            )

            // TODO: dynamically start with shorter words, then gradually increase length
            if (word.getText().length == 3) {
                wordsWith3Letters!!.add(word)
            }

            if (wordsWith3Letters!!.size == 10) {
                break
            }
        }
        Log.i(TAG, "wordsWith3Letters.size(): " + wordsWith3Letters!!.size)
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        loadNextWord()
    }

    private fun loadNextWord() {
        Log.i(TAG, "loadNextWord")

        if (wordsSeen!!.size == wordsWith3Letters!!.size) {
            // TODO: show congratulations page
            finish()
            return
        }

        word1TextView!!.setVisibility(View.INVISIBLE)
        word2TextView!!.setVisibility(View.INVISIBLE)
        word3TextView!!.setVisibility(View.INVISIBLE)
        nextButton!!.setVisibility(View.INVISIBLE)

        val currentWord = wordsWith3Letters!!.get(wordsSeen!!.size)
        Log.i(TAG, "currentWord.getText(): " + currentWord.getText())

        word1TextView!!.postDelayed(object : Runnable {
            override fun run() {
                val letter1 = currentWord.getText().substring(0, 1)
                Log.i(TAG, "letter1: " + letter1)
                word1TextView!!.setText(letter1)

                val letter2 = currentWord.getText().substring(1, 2)
                Log.i(TAG, "letter2: " + letter2)
                word2TextView!!.setText(letter2)

                val letter3 = currentWord.getText().substring(2, 3)
                Log.i(TAG, "letter3: " + letter3)
                word3TextView!!.setText(letter3)

                word1TextView!!.setVisibility(View.VISIBLE)
                word1TextView!!.postDelayed(object : Runnable {
                    override fun run() {
                        playLetterSound(letter1)


                        word2TextView!!.postDelayed(object : Runnable {
                            override fun run() {
                                word2TextView!!.setVisibility(View.VISIBLE)
                                word2TextView!!.postDelayed(object : Runnable {
                                    override fun run() {
                                        playLetterSound(letter2)


                                        word3TextView!!.postDelayed(object : Runnable {
                                            override fun run() {
                                                word3TextView!!.setVisibility(View.VISIBLE)
                                                word3TextView!!.postDelayed(object : Runnable {
                                                    override fun run() {
                                                        playLetterSound(letter3)


                                                        word3TextView!!.postDelayed(object :
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
        Log.i(TAG, "playLetterSound")

        // TODO
    }

    private fun playWord(word: WordGson?) {
        Log.i(TAG, "playWord")

        // TODO
    }
}
