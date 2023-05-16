package com.example.vocabvault

import android.os.Bundle
import android.text.Editable
import android.util.Log.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var wordInputText: TextInputEditText
    lateinit var translateBtn: Button
    lateinit var resultEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        wordInputText.addTextChangedListener {
            if (wordInputText.text.toString().trim().isEmpty()) {
                resultEditText.text.clear()
                translateBtn.isEnabled = false
            } else {
                translateBtn.isEnabled = true
            }
        }
        translateBtn.setOnClickListener {
            val word = wordInputText.text
            if (word.toString().trim().isNotEmpty()) {
                fetchWordDetails(word.toString())
            }

        }
    }
    private fun initializeViews() {
        wordInputText = findViewById(R.id.word_text_input_id)
        translateBtn = findViewById(R.id.translate_button_id)
        resultEditText = findViewById(R.id.result_edit_text_id)
        translateBtn.isEnabled = false
    }
    private fun fetchWordDetails(word: String) {
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error here
//                println("Request failed: ${e.message}")
                d("TAG", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the response here
                try {
                    val body = response.body?.string()
                    // Handle the response here
                    body?.let {
                        val result : ResultOutput? = Utils.extractInfoFromJson(it)
                        val editable = Editable.Factory.getInstance().newEditable(
                            result?.meanings?.get(0)?.definitions?.get(0) ?: "")
                        resultEditText.text = editable
                    }
                } catch (e: Exception) {
                    // Handle the exception or log the error message
                    e.printStackTrace()
                    d("ERRor", e.message.toString())
                }
            }
        })
    }// end function fetchWordDetails

}