package com.example.vocabvault

import CustomAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), ResponseCallback {
    lateinit var wordInputText: TextInputEditText
    lateinit var translateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        wordInputText.addTextChangedListener {
            translateBtn.isEnabled = !wordInputText.text.toString().trim().isEmpty()
        }
        translateBtn.setOnClickListener {
            val word = wordInputText.text
            if (word.toString().trim().isNotEmpty()) {
                fetchWordDetails(word.toString(), this)
            }
        }
    }

    private fun initializeViews() {
        wordInputText = findViewById(R.id.word_text_input_id)
        translateBtn = findViewById(R.id.translate_button_id)
        translateBtn.isEnabled = false
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
            d("MainActivity", "Result: $result")
            val listView = findViewById<ListView>(R.id.list_view_id)
            val adapter = CustomAdapter(this@MainActivity, result.meanings)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            hideKeyboard(this,wordInputText)
        }
    }


    override fun onFailure(error: String) {
        // Handle the failure
    }
    private fun hideKeyboard(context: Context, view: TextInputEditText) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}