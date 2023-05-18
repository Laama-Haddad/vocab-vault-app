package com.example.vocabvault

import CustomAdapter
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), ResponseCallback {
    lateinit var wordInputText: TextInputEditText
    lateinit var translateBtn: Button
    private lateinit var volumeUKImageView: ImageView
    private lateinit var volumeUSImageView: ImageView
    private lateinit var ukTextView: TextView
    private lateinit var usTextView: TextView
    private var mediaPlayerUS: MediaPlayer? = null
    private var mediaPlayerUK: MediaPlayer? = null
    private var voiceUrlUS: String? = null
    private var voiceUrlUK: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        wordInputText.addTextChangedListener {
            translateBtn.isEnabled = !wordInputText.text.toString().trim().isEmpty()
            if (!wordInputText.text.toString().trim().isEmpty()) {
                volumeUSImageView.visibility = View.GONE
                volumeUKImageView.visibility = View.GONE
                ukTextView.visibility = View.GONE
                usTextView.visibility = View.GONE
                voiceUrlUS = null
                voiceUrlUK = null
            }
        }
        translateBtn.setOnClickListener {
            val word = wordInputText.text
            if (word.toString().trim().isNotEmpty()) {
                fetchWordDetails(word.toString(), this)
            } else {
                volumeUKImageView.visibility = View.GONE
                volumeUSImageView.visibility = View.GONE
                ukTextView.visibility = View.GONE
                usTextView.visibility = View.GONE
            }
        }
        volumeUKImageView.setOnClickListener { onUKIconClick(it, voiceUrlUK!!) }
        volumeUSImageView.setOnClickListener { onUSIconClick(it, voiceUrlUS!!) }
    }

    private fun initializeViews() {
        wordInputText = findViewById(R.id.word_text_input_id)
        translateBtn = findViewById(R.id.translate_button_id)
        volumeUKImageView = findViewById(R.id.uk_icon_id)
        volumeUSImageView = findViewById(R.id.us_icon_id)
        ukTextView = findViewById(R.id.uk_text_view_id)
        usTextView = findViewById(R.id.us_text_view_id)
        translateBtn.isEnabled = false
        volumeUSImageView.visibility = View.GONE
        volumeUKImageView.visibility = View.GONE
        ukTextView.visibility = View.GONE
        usTextView.visibility = View.GONE
    }

    private fun fetchWordDetails(word: String, callback: ResponseCallback) {
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                try {
                    body?.let {
                        val result: ResultOutput? = Utils.extractInfoFromJson(it)
                        result?.let {
                            callback.onSuccess(result)
                        }
                    }
                } catch (e: Exception) {
                    callback.onFailure(e.message.toString())
                }
            }
        })
    }

    override fun onSuccess(result: ResultOutput) {
        runOnUiThread {
            // UI update code here
            val audioList = result.audio.distinct()
            for (item in audioList) {
                if (item.audio.contains("-us", ignoreCase = true)) voiceUrlUS = item.audio
                if (item.audio.contains("-uk", ignoreCase = true)) voiceUrlUK = item.audio
            }
            if (voiceUrlUK == null) {
                volumeUKImageView.visibility = View.GONE
                ukTextView.visibility = View.GONE
            } else {
                volumeUKImageView.visibility = View.VISIBLE
                ukTextView.visibility = View.VISIBLE
            }
            if (voiceUrlUS == null) {
                volumeUSImageView.visibility = View.GONE
                usTextView.visibility = View.GONE
            } else {
                volumeUSImageView.visibility = View.VISIBLE
                usTextView.visibility = View.VISIBLE
            }
            val listView = findViewById<ListView>(R.id.list_view_id)
            val adapter = CustomAdapter(this@MainActivity, result.meanings)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            hideKeyboard(this, wordInputText)
        }
    }

    override fun onFailure(error: String) {
        // Handle the failure
    }

    private fun hideKeyboard(context: Context, view: TextInputEditText) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onUSIconClick(view: View, voiceUrl: String) {
        // Handle icon click event
        if (mediaPlayerUS == null) {
            mediaPlayerUS = MediaPlayer()
            mediaPlayerUS?.setOnCompletionListener {
                // Release the MediaPlayer resources when playback is completed
                mediaPlayerUS?.release()
                mediaPlayerUS = null
            }
        } else {
            mediaPlayerUS?.reset()
        }

        try {
            mediaPlayerUS?.setDataSource(voiceUrl)
            mediaPlayerUS?.prepare()
            mediaPlayerUS?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors that occur during MediaPlayer setup or playback
        }
    }

    private fun onUKIconClick(view: View, voiceUrl: String) {
        // Handle icon click event
        if (mediaPlayerUK == null) {
            mediaPlayerUK = MediaPlayer()
            mediaPlayerUK?.setOnCompletionListener {
                // Release the MediaPlayer resources when playback is completed
                mediaPlayerUK?.release()
                mediaPlayerUK = null
            }
        } else {
            mediaPlayerUK?.reset()
        }

        try {
            mediaPlayerUK?.setDataSource(voiceUrl)
            mediaPlayerUK?.prepare()
            mediaPlayerUK?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors that occur during MediaPlayer setup or playback
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerUS?.release()
        mediaPlayerUS = null
        mediaPlayerUK?.release()
        mediaPlayerUK = null
    }

}