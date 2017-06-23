package org.literacyapp.familiar_word_reading;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.literacyapp.contentprovider.ContentProvider;
import org.literacyapp.contentprovider.model.content.Word;
import org.literacyapp.model.enums.content.SpellingConsistency;

import java.util.List;

public class LetterSoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_letter_sound);

        List<Word> words = ContentProvider.getAllWords(SpellingConsistency.PERFECT);
        Log.i(getClass().getName(), "words.size(): " + words.size());
        for (Word word : words) {
            Log.i(getClass().getName(), "word.getText(): " + word.getText() + ", word.getPhonetics(): " + word.getPhonetics() + ", word.getSpellingConsistency(): " + word.getSpellingConsistency());
        }

//        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.???);
//        mediaPlayer.start();
    }
}
