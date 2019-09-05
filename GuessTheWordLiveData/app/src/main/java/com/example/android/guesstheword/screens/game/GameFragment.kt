/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )
        Log.i("GameFragment", "Called ViewModelProviders.of")

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        /** Setting up LiveData observation relationship **/
        viewModel.word.observe(this, Observer { newWord ->
            binding.wordText.text = newWord
        })

        viewModel.score.observe(this, Observer { newScore ->
            binding.scoreText.text = newScore.toString()

        })

        // Observer for the Game finished event
        viewModel.eventGameFinish.observe(this, Observer<Boolean> { hasFinished ->
            if (hasFinished) gameFinished()
        })

        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        binding.endGameButton.setOnClickListener { onEndGame() }
        return binding.root
    }


    /** Methods for buttons presses **/

    private fun onSkip() {
        viewModel.onSkip()
    }
    private fun onCorrect() {
        viewModel.onCorrect()
    }
    private fun onEndGame() {
        gameFinished()
    }

    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score.value?:0
        findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }
}
