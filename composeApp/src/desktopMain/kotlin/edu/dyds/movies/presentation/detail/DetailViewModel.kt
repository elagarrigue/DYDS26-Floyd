package edu.dyds.movies.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())

    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadMovie(title: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)
            try {
                val movie = getMovieDetailsUseCase(title)
                _uiState.value = DetailUiState(isLoading = false, movie = movie)
            } catch (e: Exception) {
                _uiState.value = DetailUiState(isLoading = false, movie = null)
            }
        }
    }
}
