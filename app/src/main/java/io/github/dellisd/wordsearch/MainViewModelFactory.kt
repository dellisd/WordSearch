package io.github.dellisd.wordsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val wordSearchGenerator: WordSearchFactory) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(wordSearchGenerator) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}