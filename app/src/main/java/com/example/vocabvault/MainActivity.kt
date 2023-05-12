package com.example.vocabvault

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText



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
            }else{
                translateBtn.isEnabled = true
            }
        }
        translateBtn.setOnClickListener {
            resultEditText.text = wordInputText.text
        }
    }
    private fun initializeViews() {
        wordInputText = findViewById(R.id.word_text_input_id)
        translateBtn = findViewById(R.id.translate_button_id)
        resultEditText = findViewById(R.id.result_edit_text_id)
        translateBtn.isEnabled = false
    }
}