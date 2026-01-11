package com.tanjan.hakupivkirja.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tanjan.hakupivkirja.model.AppDatabase
import com.tanjan.hakupivkirja.model.UserEntity
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

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()
    
    private val _uiState = MutableStateFlow(AuthUiState(currentUser = auth.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

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
        }
    }

    fun signUp(email: String, salasana: String, username: String, dogName: String) {
        if (email.isBlank() || salasana.isBlank() || username.isBlank() || dogName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Täytä kaikki kentät")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            auth.createUserWithEmailAndPassword(email, salasana)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid
                        if (uid != null) {
                            saveUserProfile(uid, username, dogName)
                        }
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, 
                            error = task.exception?.message ?: "Rekisteröinti epäonnistui"
                        )
                    }
                }
        }
    }

    private fun saveUserProfile(uid: String, username: String, dogName: String) {
        viewModelScope.launch {
            try {
                userDao.insertUser(UserEntity(uid, username, dogName))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Profiilin tallennus epäonnistui: ${e.message}")
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
