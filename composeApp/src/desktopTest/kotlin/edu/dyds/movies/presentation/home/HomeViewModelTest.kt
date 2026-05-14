package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.presentation.fakes.FakeGetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var useCase: FakeGetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = FakeGetPopularMoviesUseCase()
        viewModel = HomeViewModel(useCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovies deberia actualizar uiState con las peliculas cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<HomeUiState>()
            val job = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()

            // assert
            job.cancel()
            assertEquals(useCase.movies, states.last().movies)
        }

    @Test
    fun `loadMovies deberia dejar isLoading en false cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<HomeUiState>()
            val job = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()

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
            val job = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()

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
            val job = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovies()

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }
}
