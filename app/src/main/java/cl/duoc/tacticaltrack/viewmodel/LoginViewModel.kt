package cl.duoc.tacticaltrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.tacticaltrack.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val role: String = "Operario",
    val usernameError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val remembered = preferencesRepository.lastUsername.first()
            if (remembered.isNotBlank()) {
                _uiState.update { it.copy(username = remembered) }
            }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, usernameError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRoleChange(value: String) {
        _uiState.update { it.copy(role = value) }
    }

    fun validateAndRemember(): Boolean {
        val state = _uiState.value
        val userError = if (state.username.trim().length < 3) {
            "Ingresa al menos 3 caracteres."
        } else null

        val passError = if (state.password.length < 4) {
            "La clave demo debe tener al menos 4 caracteres."
        } else null

        _uiState.update {
            it.copy(usernameError = userError, passwordError = passError)
        }

        val valid = userError == null && passError == null
        if (valid) {
            viewModelScope.launch {
                preferencesRepository.saveLastUsername(state.username.trim())
            }
        }
        return valid
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(preferencesRepository) as T
        }
    }
}
