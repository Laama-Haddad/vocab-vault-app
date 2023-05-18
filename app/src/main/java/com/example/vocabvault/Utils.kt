package com.example.vocabvault

import com.google.gson.Gson

class Utils {
    companion object {
        fun extractInfoFromJson(jsonString: String): ResultOutput? {
            val gson = Gson()
            val wordEntries = gson.fromJson(jsonString, Array<WordEntry>::class.java)
            var word = wordEntries[0].word
            var phoneticList = wordEntries.flatMap { it.phonetics }
            var audioList = phoneticList.map { it }.distinct().toMutableList()
            var meaningList = mutableListOf<MeaningResult>()
            var result: ResultOutput?
            wordEntries.forEach { it ->
                for (item in it.meanings) {
                    var speech = item.partOfSpeech
                    var defs = item.definitions.map { it }.toMutableList()
                    var meaningResultItem = MeaningResult(speech, defs)
                    meaningList.add(meaningResultItem)
                }
            }
            result = ResultOutput(word, audioList, meaningList)
            return result
        }
    }
}
