package com.tanjan.hakupivkirja.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow(AuthUiState(currentUser = auth.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Tarkkaillaan kirjautumistilan muutoksia
    init {
        auth.addAuthStateListener { firebaseAuth ->
            _uiState.value = _uiState.value.copy(currentUser = firebaseAuth.currentUser)
        }
    }

    fun signIn(email: String, salasana: String) {
        if (email.isBlank() || salasana.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Täytä kaikki kentät")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                auth.signInWithEmailAndPassword(email, salasana)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false, 
                                isLoginSuccessful = true
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false, 
                                error = task.exception?.message ?: "Kirjautuminen epäonnistui"
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun signUp(email: String, salasana: String) {
        if (email.isBlank() || salasana.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Täytä kaikki kentät")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                auth.createUserWithEmailAndPassword(email, salasana)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false, 
                                error = task.exception?.message ?: "Rekisteröinti epäonnistui"
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
