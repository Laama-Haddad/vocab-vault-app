package com.example.vocabvault

import android.app.AlertDialog
import android.content.Context
import android.view.View

open class Alert : AlertDialogCallback {
    private lateinit var alertDialog: AlertDialog

    override fun showAlert(view: View, context: Context) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)
        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.show()
    }

    override fun hideAlert() {
        if (::alertDialog.isInitialized && alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }

}