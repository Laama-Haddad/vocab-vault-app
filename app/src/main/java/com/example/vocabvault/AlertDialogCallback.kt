package com.example.vocabvault

import android.content.Context
import android.view.View

interface AlertDialogCallback {
    fun showAlert(view: View, context: Context)
    fun hideAlert()
}