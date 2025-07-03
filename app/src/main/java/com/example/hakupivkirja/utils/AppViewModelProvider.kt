package com.example.hakupivkirja.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hakupivkirja.model.repository.HakupivkirjaRepository
import com.example.hakupivkirja.ui.viewmodels.TrainingSessionViewModel

// Import other ViewModels and their dependencies if this factory handles more

object AppViewModelProvider {

  fun factory(repository: HakupivkirjaRepository): ViewModelProvider.Factory = viewModelFactory {
    initializer {
      TrainingSessionViewModel(repository = repository)
    }

  }

}