package com.example.vocabvault

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class CustomAdapter(context: Context, private var dataList: MutableList<MeaningResult>) :
    ArrayAdapter<MeaningResult>(context, 0, dataList), UpdateDefinitionCallback {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemLayoutView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_layout, parent, false)

        val partOfSpeechTextView =
            itemLayoutView.findViewById<TextView>(R.id.part_of_speech_text_view_id)
        val definitionTextView = itemLayoutView.findViewById<TextView>(R.id.definition_text_view_id)
        val moreTextView = itemLayoutView.findViewById<TextView>(R.id.more_text_view_id)

        val meaningResult = dataList[position]
        val definitions = meaningResult.definitions

        partOfSpeechTextView.text = meaningResult.partOfSpeech
        definitionTextView.text = definitions.firstOrNull()?.definition ?: ""

        moreTextView.setOnClickListener {
            val inflater = LayoutInflater.from(context)
            val alertDialogView = inflater.inflate(R.layout.definitions_dialog_layout, null)
            val listView = alertDialogView.findViewById<ListView>(R.id.list_view1_id)
            val adapter = DefinitionsDialogCustomAdapter(context, definitions, this, position)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()

            val headerAlertView =
                alertDialogView.findViewById<LinearLayout>(R.id.header_alert_linear_id)
            val backButton = headerAlertView.findViewById<ImageView>(R.id.arrow_back_icon_id)
            backButton.setOnClickListener {
                customAlert.hideAlert()
            }
            customAlert.showAlert(alertDialogView, context)

        }
        return itemLayoutView
    }

    override fun onUpdateDefinition(definition: String, position: Int) {
        dataList[position].definitions.firstOrNull()?.definition = definition
        // Notify the adapter that the data has changed
        notifyDataSetChanged()
    }

    companion object {
        var customAlert = Alert()
    }
}


