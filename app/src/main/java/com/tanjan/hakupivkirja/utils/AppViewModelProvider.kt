package com.tanjan.hakupivkirja.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tanjan.hakupivkirja.model.repository.HakupivkirjaRepository
import com.tanjan.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

// Import other ViewModels and their dependencies if this factory handles more

object AppViewModelProvider {

  fun factory(
              trainingSessionRepository: HakupivkirjaRepository

  ): ViewModelProvider.Factory = viewModelFactory {
    initializer {
      TrainingSessionViewModel(repository = trainingSessionRepository)
    }

  }

}