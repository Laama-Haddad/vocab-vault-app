package com.example.vocabvault

data class WordEntry(
    val word: String, val phonetics: List<AudioEntry>, val meanings: List<MeaningEntry>
)

data class AudioEntry(
    val audio: String
)

data class MeaningEntry(
    val partOfSpeech: String, val definitions: List<DefinitionEntry>
)

data class DefinitionEntry(
    val definition: String
)

data class MeaningResult(
    val partOfSpeech: String, val definitions: List<String>
)

data class ResultOutput(
    val word: String, val audio: List<String>, val meanings: List<MeaningResult>
)