package io.github.dellisd.wordsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val generator: WordSearchGenerator) : ViewModel() {

    private val _letterGrid = MutableLiveData<Array<Array<Char>>>()
    val letterGrid: LiveData<Array<Array<Char>>> = _letterGrid

    fun generateWordSearch() {

    }

}