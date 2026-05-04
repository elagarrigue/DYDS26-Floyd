package edu.dyds.movies.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                val movies = getPopularMoviesUseCase.execute()
                _uiState.value = HomeUiState(isLoading = false, movies = movies)
            } catch (e: Exception) {
                _uiState.value = HomeUiState(isLoading = false, movies = emptyList())
            }
        }
    }
}
