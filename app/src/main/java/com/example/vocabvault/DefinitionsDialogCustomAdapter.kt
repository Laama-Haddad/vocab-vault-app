package com.example.vocabvault

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class DefinitionsDialogCustomAdapter(
    context: Context,
    private val dataList: MutableList<DefinitionEntry>,
    private val updateDefinitionCallback: UpdateDefinitionCallback,
    private val position1: Int
) : ArrayAdapter<DefinitionEntry>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemLayoutView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.definitions_list_item_layout, parent, false)
        val textView = itemLayoutView.findViewById<TextView>(R.id.textView)

        val definitions = dataList[position]

        textView.text = definitions.definition

        textView.setOnClickListener {
            //here I want to update definitionTextView
            updateDefinitionCallback.onUpdateDefinition(definitions.definition, position1)
            CustomAdapter.customAlert.hideAlert()
        }
        return itemLayoutView
    }
}


