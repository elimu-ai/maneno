package ai.elimu.maneno;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ai.elimu.content_provider.utils.ContentProviderUtil;
import ai.elimu.model.v2.gson.content.WordGson;

import java.util.ArrayList;
import java.util.List;

public class LetterSoundActivity extends AppCompatActivity {

    private List<WordGson> wordsWith3Letters;

    private List<WordGson> wordsSeen;

    private TextView word1TextView;
    private TextView word2TextView;
    private TextView word3TextView;

    private ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_letter_sound);

        wordsSeen = new ArrayList<>();

        word1TextView = (TextView) findViewById(R.id.word1TextView);
        word2TextView = (TextView) findViewById(R.id.word2TextView);
        word3TextView = (TextView) findViewById(R.id.word3TextView);

        nextButton = (ImageButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "nextButton onClick");

                loadNextWord();
            }
        });

        List<WordGson> allWords = ContentProviderUtil.INSTANCE.getAllWordGsons(getApplicationContext(), BuildConfig.CONTENT_PROVIDER_APPLICATION_ID);
        // TODO: dynamically fetch words that only contain the student's unlocked letter-sound correspondences
        Log.i(getClass().getName(), "allWords.size(): " + allWords.size());

        // TODO: filter words by SpellingConsistency?

        wordsWith3Letters = new ArrayList<>();
        for (WordGson word : allWords) {
            Log.i(getClass().getName(), "word.getText(): " + word.getText() + ", word.getWordType(): " + word.getWordType());

            // TODO: dynamically start with shorter words, then gradually increase length
            if (word.getText().length() == 3) {
                wordsWith3Letters.add(word);
            }

            if (wordsWith3Letters.size() == 10) {
                break;
            }
        }
        Log.i(getClass().getName(), "wordsWith3Letters.size(): " + wordsWith3Letters.size());
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        loadNextWord();
    }

    private void loadNextWord() {
        Log.i(getClass().getName(), "loadNextWord");

        if (wordsSeen.size() == wordsWith3Letters.size()) {
            // TODO: show congratulations page
            finish();
            return;
        }

        word1TextView.setVisibility(View.INVISIBLE);
        word2TextView.setVisibility(View.INVISIBLE);
        word3TextView.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        final WordGson currentWord = wordsWith3Letters.get(wordsSeen.size());
        Log.i(getClass().getName(), "currentWord.getText(): " + currentWord.getText());

        word1TextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String letter1 = currentWord.getText().substring(0, 1);
                Log.i(getClass().getName(), "letter1: " + letter1);
                word1TextView.setText(letter1);

                final String letter2 = currentWord.getText().substring(1, 2);
                Log.i(getClass().getName(), "letter2: " + letter2);
                word2TextView.setText(letter2);

                final String letter3 = currentWord.getText().substring(2, 3);
                Log.i(getClass().getName(), "letter3: " + letter3);
                word3TextView.setText(letter3);

                word1TextView.setVisibility(View.VISIBLE);
                word1TextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playLetterSound(letter1);


                        word2TextView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                word2TextView.setVisibility(View.VISIBLE);
                                word2TextView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        playLetterSound(letter2);


                                        word3TextView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                word3TextView.setVisibility(View.VISIBLE);
                                                word3TextView.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        playLetterSound(letter3);


                                                        word3TextView.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                playWord(currentWord);
                                                                wordsSeen.add(currentWord);
                                                                nextButton.setVisibility(View.VISIBLE);
                                                            }
                                                        }, 2000);
                                                    }
                                                }, 1000);
                                            }
                                        }, 2000);
                                    }
                                }, 1000);
                            }
                        }, 2000);
                    }
                }, 1000);
            }
        }, 1000);
    }

    private void playLetterSound(String letter) {
        Log.i(getClass().getName(), "playLetterSound");

        // TODO
    }

    private void playWord(WordGson word) {
        Log.i(getClass().getName(), "playWord");

        // TODO
    }
}
