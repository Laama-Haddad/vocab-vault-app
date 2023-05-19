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
            wordEntries.forEach { wordEntry ->
                val meaningsByPartOfSpeech = wordEntry.meanings.groupBy { it.partOfSpeech }
                meaningsByPartOfSpeech.forEach { (partOfSpeech, meanings) ->
                    val definitions = meanings.flatMap { it.definitions }
                    val matchingItem = meaningList.find { it.partOfSpeech == partOfSpeech }
                    if (matchingItem != null) {
                        matchingItem.definitions.addAll(definitions)
                    } else {
                        val meaningResultItem =
                            MeaningResult(partOfSpeech, definitions.toMutableList())
                        meaningList.add(meaningResultItem)
                    }
                }
            }
            result = ResultOutput(word, audioList, meaningList)
            return result
        }
    }
}
