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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private class FakeGetPopularMoviesUseCase : GetPopularMoviesUseCase {
        var shouldFail = false
        var movies = listOf(
            QualifiedMovie(
                Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0),
                isGoodMovie = true
            )
        )

        override suspend fun execute(): List<QualifiedMovie> {
            if (shouldFail) throw Exception("Use case error")
            return movies
        }
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: FakeGetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        // arrange comun
        Dispatchers.setMain(testDispatcher)
        useCase = FakeGetPopularMoviesUseCase()
        viewModel = HomeViewModel(useCase)
    }

    @Test
    fun `loadMovies deberia actualizar uiState con las peliculas cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<HomeUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            val finalState = states.last()
            assertEquals(useCase.movies, finalState.movies)
        }

    @Test
    fun `loadMovies deberia dejar isLoading en false cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<HomeUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }

    @Test
    fun `loadMovies deberia actualizar uiState con lista vacia cuando el caso de uso falla`() =
        runTest(testDispatcher) {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<HomeUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertEquals(emptyList<QualifiedMovie>(), states.last().movies)
        }

    @Test
    fun `loadMovies deberia dejar isLoading en false cuando el caso de uso falla`() =
        runTest(testDispatcher) {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<HomeUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }
}
