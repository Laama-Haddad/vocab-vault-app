package com.example.vocabvault

data class WordEntry(
    val word: String, val phonetics: MutableList<AudioEntry>, val meanings: MutableList<MeaningEntry>
)

data class AudioEntry(
    val audio: String
)

data class MeaningEntry(
    val partOfSpeech: String, val definitions: MutableList<DefinitionEntry>
)

data class DefinitionEntry(
    val definition: String
)

data class MeaningResult(
    var partOfSpeech: String, var definitions: MutableList<DefinitionEntry>
)

data class ResultOutput(
    val word: String, val audio: MutableList<AudioEntry>, val meanings: MutableList<MeaningResult>
)