package com.zas.chit.shuffle

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zas.chit.shuffle.databinding.ActivityMainBinding
import com.zas.chit.shuffle.databinding.WordsChipBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var shuffleAdapter: ShuffleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreference = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        shuffleAdapter = ShuffleAdapter(this@MainActivity)
        binding.shuffledWords.adapter = shuffleAdapter
        updateWords()

        binding.shuffle.performClick()
    }

    fun shuffleWords(@Suppress("UNUSED_PARAMETER") view: View? = null) {
        val words: HashSet<String> = getWords(true)

        words.let {
            if (words.size > 0) {
                val shuffledList = words.toMutableList()
                shuffledList.shuffle()
                shuffleAdapter.updateList(shuffledList)
                when (shuffledList.size) {
                    1, 2 -> {
                        binding.shuffledWords.numColumns = 1
                    }

                    3, 4, 5, 6, 7, 8 -> {
                        binding.shuffledWords.numColumns = 2
                    }

                    9, 10, 11, 12, 13, 14, 15 -> {
                        binding.shuffledWords.numColumns = 3
                    }

                    else -> {
                        binding.shuffledWords.numColumns = 4
                    }
                }
            }
        }
    }

    fun addWords(@Suppress("UNUSED_PARAMETER") view: View) {
        val words: HashSet<String> = getWords()
        words.add(binding.wordInput.editText?.text.toString())
        sharedPreference.edit().putStringSet("words", words).apply()
        updateWords()
        binding.wordInput.editText?.setText("")
    }

    private fun getWords(forShuffle: Boolean = false): HashSet<String> {
        val wordList = HashSet<String>()
        val wordsEntered =
            HashSet(sharedPreference.getStringSet("words", HashSet<String>()) ?: HashSet())
        wordList.addAll(wordsEntered)
        
        if (forShuffle) {
            val multiplesOfEntries =
                binding.noOfDuplicateSlots.editText?.text.toString().toIntOrNull()
            val noOfEmptySlots = binding.noOfEmptySlots.editText?.text.toString().toIntOrNull()
            if ((multiplesOfEntries ?: 0) > 1) {
                for (i in 1..<multiplesOfEntries!!) {
                    wordsEntered.forEach {
                        wordList.add("$it $i")
                    }
                }
            }
            if ((noOfEmptySlots ?: 0) > 0) {
                for (i in 1..noOfEmptySlots!!) {
                    wordList.add(" ".repeat(i))
                }
            }
        }
        return wordList
    }

    private fun updateWords() {
        val words: HashSet<String> = getWords()
        binding.wordChipGroup.removeAllViews()
        words.forEach { word ->
            val view = WordsChipBinding.inflate(layoutInflater)
            view.chip.text = word
            view.root.setOnClickListener { deleteWord(word) }
            binding.wordChipGroup.addView(view.root)
        }
    }

    fun clearWords(@Suppress("UNUSED_PARAMETER") view: View) {
        sharedPreference.edit().putStringSet("words", HashSet()).apply()
        updateWords()
        shuffleAdapter.updateList(arrayListOf())
    }

    private fun deleteWord(item: String) {
        val words: HashSet<String> = getWords()
        words.remove(item)
        sharedPreference.edit().putStringSet("words", words).apply()
        updateWords()
    }
}