package com.example.vocabvault

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), ResponseCallback, OnTagClickListener {
    private lateinit var wordInputText: TextInputEditText
    private lateinit var translateBtn: Button
    private lateinit var bottomLinearLayout: LinearLayout
    private lateinit var usLinearLayout: LinearLayout
    private lateinit var ukLinearLayout: LinearLayout
    private lateinit var meaningListView: ListView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var saveFab: FloatingActionButton
    private var mediaPlayerUS: MediaPlayer? = null
    private var mediaPlayerUK: MediaPlayer? = null
    private var voiceUrlUS: String? = null
    private var voiceUrlUK: String? = null
    private lateinit var tagsAdapter: TagsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        wordInputText.addTextChangedListener {
            translateBtn.isEnabled = wordInputText.text.toString().trim().isNotEmpty()
            if (wordInputText.text.toString().trim().isNotEmpty()) {
                bottomLinearLayout.visibility = View.GONE
                voiceUrlUS = null
                voiceUrlUK = null
            }
        }
        translateBtn.setOnClickListener {
            val word = wordInputText.text
            if (word.toString().trim().isNotEmpty()) {
                fetchWordDetails(word.toString(), this)
            } else {
                bottomLinearLayout.visibility = View.GONE
                saveFab.visibility = View.GONE
            }
        }
        usLinearLayout.setOnClickListener { onUSIconClick(it, voiceUrlUS!!) }
        ukLinearLayout.setOnClickListener { onUKIconClick(it, voiceUrlUK!!) }

        saveFab.setOnClickListener {
            Log.d("MainActivity", "Fab Clicked!!")
        }
    }

    private fun initializeViews() {
        wordInputText = findViewById(R.id.word_text_input_id)
        translateBtn = findViewById(R.id.translate_button_id)
        bottomLinearLayout = findViewById(R.id.bottom_linear_layout_id)
        usLinearLayout = findViewById(R.id.us_linear_layout_id)
        ukLinearLayout = findViewById(R.id.uk_linear_layout_id)
        meaningListView = findViewById(R.id.meanings_list_view_id)
        tagsRecyclerView = findViewById(R.id.tags_recycler_view)
        saveFab = findViewById(R.id.save_floating_action_button_id)
        translateBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary))
        translateBtn.isEnabled = false
        bottomLinearLayout.visibility = View.GONE
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
                ukLinearLayout.visibility = View.GONE
            } else {
                ukLinearLayout.visibility = View.VISIBLE
            }
            if (voiceUrlUS == null) {
                usLinearLayout.visibility = View.GONE
            } else {
                usLinearLayout.visibility = View.VISIBLE
            }

            val adapter = CustomAdapter(this@MainActivity, result.meanings)
            meaningListView.adapter = adapter
            adapter.notifyDataSetChanged()

            val tagsList: MutableList<String> = result.meanings.map { it ->
                "${it.partOfSpeech}"
            }.toMutableList()

            tagsAdapter = TagsAdapter(tagsList, this)
            tagsRecyclerView.adapter = tagsAdapter
            tagsRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            tagsAdapter.notifyDataSetChanged()

            hideKeyboard(this, wordInputText)
            bottomLinearLayout.visibility = View.VISIBLE
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

    override fun onTagClick(position: Int) {
        val selectedItem = tagsAdapter.getSelectedItem(position)
        val formattedMessage: String = if (selectedItem != null) {
            getString(R.string.defaultTagToast) + " $selectedItem"
        } else {
            getString(R.string.wrongToast)
        }
        Toast.makeText(this, formattedMessage, Toast.LENGTH_SHORT).show()
    }
}