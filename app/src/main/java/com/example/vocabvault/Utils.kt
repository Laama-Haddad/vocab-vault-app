package com.example.vocabvault

import android.util.Log
import com.google.gson.Gson

class Utils {
    companion object {
        fun extractInfoFromJson(jsonString: String): ResultOutput? {
            val gson = Gson()
            val wordEntries = gson.fromJson(jsonString, Array<WordEntry>::class.java)
            var word = wordEntries[0].word
            var phoneticList = wordEntries.flatMap { it.phonetics }
            var audioList = phoneticList.map { it.audio }.distinct()
            var meaningList = mutableListOf<MeaningResult>()
            val mergedMeaningMap = mutableMapOf<String, MutableList<String>>()
            wordEntries.forEach { it ->
                for (item in it.meanings) {
                    var speech = item.partOfSpeech
                    var defs = item.definitions.map { it.definition }
                    var meaningResultItem = MeaningResult(speech, defs)
                    meaningList.add(meaningResultItem)
                }
            }
            meaningList.forEach { meaning ->
                val partOfSpeech = meaning.partOfSpeech
                val definitions = meaning.definitions
                val mergedDefinitions = mergedMeaningMap.getOrDefault(partOfSpeech, mutableListOf())
                mergedDefinitions.addAll(definitions)
                mergedMeaningMap[partOfSpeech] = mergedDefinitions.distinct().toMutableList()
            }
            val mergedMeaningList = mergedMeaningMap.entries.map { entry ->
                MeaningResult(entry.key, entry.value)
            }
            val result = ResultOutput(word, audioList, mergedMeaningList)
            //Print Result
            Log.d("HOHO", "${result.word}${result.audio}${result.meanings}")
            return result
        }
    }
}
