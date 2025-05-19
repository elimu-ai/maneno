package ai.elimu.maneno;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ai.elimu.maneno.contentprovider.ContentProvider;
import ai.elimu.maneno.contentprovider.model.content.Word;
import ai.elimu.maneno.contentprovider.model.content.multimedia.Audio;
import ai.elimu.maneno.contentprovider.util.MultimediaHelper;
import ai.elimu.maneno.model.enums.content.SpellingConsistency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LetterSoundActivity extends AppCompatActivity {

    private List<Word> words;

    private List<Word> wordsSeen;

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

        List<Word> wordsWithPerfectSpelling = ContentProvider.getAllWords(SpellingConsistency.PERFECT);
        Log.i(getClass().getName(), "wordsWithPerfectSpelling.size(): " + wordsWithPerfectSpelling.size());

        // TODO: dynamically fetch words that only contain the current student's unlocked letters (& syllables)
        words = new ArrayList<>();
        for (Word word : wordsWithPerfectSpelling) {
            Log.i(getClass().getName(), "word.getText(): " + word.getText() + ", word.getPhonetics(): " + word.getPhonetics() + ", word.getSpellingConsistency(): " + word.getSpellingConsistency());

            // Skip if corresponding Audio is missing
            Audio audio = ContentProvider.getAudio(word.getText());
            if (audio == null) {
                continue;
            }

            // TODO: dynamically start with shorter words, then gradually increase length
            if (word.getText().length() == 3) {
                words.add(word);
            }

            if (words.size() == 10) {
                break;
            }
        }
        Log.i(getClass().getName(), "words.size(): " + words.size());
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        loadNextWord();
    }

    private void loadNextWord() {
        Log.i(getClass().getName(), "loadNextWord");

        if (wordsSeen.size() == words.size()) {
            // TODO: show congratulations page
            finish();
            return;
        }

        word1TextView.setVisibility(View.INVISIBLE);
        word2TextView.setVisibility(View.INVISIBLE);
        word3TextView.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        final Word currentWord = words.get(wordsSeen.size());
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

        Audio audio = ContentProvider.getAudio("letter_sound_" + letter);
        File audioFile = MultimediaHelper.getFile(audio);
        Uri uri = Uri.parse(audioFile.getAbsolutePath());
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(getClass().getName(), "playLetterSound onCompletion");
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }

    private void playWord(Word word) {
        Log.i(getClass().getName(), "playWord");

        Audio audio = ContentProvider.getAudio(word.getText());
        File audioFile = MultimediaHelper.getFile(audio);
        Uri uri = Uri.parse(audioFile.getAbsolutePath());
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(getClass().getName(), "playWord onCompletion");
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }
}
