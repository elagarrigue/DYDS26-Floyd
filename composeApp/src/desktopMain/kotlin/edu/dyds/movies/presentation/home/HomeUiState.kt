package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.QualifiedMovie

data class HomeUiState(
    val isLoading: Boolean = false,
    val movies: List<QualifiedMovie> = emptyList(),
)
