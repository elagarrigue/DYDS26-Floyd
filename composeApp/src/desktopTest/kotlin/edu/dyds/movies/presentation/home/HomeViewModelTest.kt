package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    init {
        Dispatchers.setMain(testDispatcher)
    }

    private class FakeGetPopularMoviesUseCase : GetPopularMoviesUseCase {
        var shouldFail = false
        var movies = listOf(
            QualifiedMovie(Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0), true)
        )

        override suspend fun execute(): List<QualifiedMovie> {
            if (shouldFail) throw Exception("Use case error")
            return movies
        }
    }

    @Test
    fun `loadMovies should update uiState with movies on success`() = runTest(testDispatcher) {
        // arrange
        val useCase = FakeGetPopularMoviesUseCase()
        val viewModel = HomeViewModel(useCase)
        val states = mutableListOf<HomeUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        // act
        viewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // assert
        job.cancel()
        val finalState = states.last()
        assertEquals(false, finalState.isLoading)
        assertEquals(useCase.movies, finalState.movies)
    }

    @Test
    fun `loadMovies should update uiState with empty movies on failure`() = runTest(testDispatcher) {
        // arrange
        val useCase = FakeGetPopularMoviesUseCase().apply { shouldFail = true }
        val viewModel = HomeViewModel(useCase)
        val states = mutableListOf<HomeUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        // act
        viewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // assert
        job.cancel()
        val finalState = states.last()
        assertEquals(false, finalState.isLoading)
        assertEquals(emptyList<QualifiedMovie>(), finalState.movies)
    }
}
