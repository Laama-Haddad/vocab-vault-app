import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.vocabvault.MeaningResult
import com.example.vocabvault.R

class CustomAdapter(context: Context, private val dataList: MutableList<MeaningResult>) :
    ArrayAdapter<MeaningResult>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_layout, parent, false)

        val partOfSpeech = view.findViewById<TextView>(R.id.part_of_speech_text_view_id)
        val definition = view.findViewById<TextView>(R.id.definition_text_view_id)

        val meaningResult = dataList[position]
        partOfSpeech.text = meaningResult.partOfSpeech
        definition.text = meaningResult.definitions.firstOrNull()?.definition ?: ""

        return view
    }
}


