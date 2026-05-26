package edu.dyds.movies.presentation.detail

import edu.dyds.movies.presentation.fakes.FakeGetMovieDetailsUseCase
import kotlinx.coroutines.CoroutineScope
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
class DetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)
    private lateinit var useCase: FakeGetMovieDetailsUseCase
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = FakeGetMovieDetailsUseCase()
        viewModel = DetailViewModel(useCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovie deberia actualizar uiState con la pelicula cuando el caso de uso tiene exito`() =
        runTest {
            // arrange
            val states = mutableListOf<DetailUiState>()
            val job = testScope.launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie("Title1")

            // assert
            job.cancel()
            assertEquals(useCase.movie, states.last().movie)
        }

    @Test
    fun `loadMovie deberia dejar isLoading en false cuando el caso de uso tiene exito`() =
        runTest {
            // arrange
            val states = mutableListOf<DetailUiState>()
            val job = testScope.launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie("Title1")

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }

    @Test
    fun `loadMovie deberia actualizar uiState con pelicula null cuando el caso de uso falla`() =
        runTest {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<DetailUiState>()
            val job = testScope.launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie("Title1")

            // assert
            job.cancel()
            assertEquals(null, states.last().movie)
        }

    @Test
    fun `loadMovie deberia dejar isLoading en false cuando el caso de uso falla`() =
        runTest {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<DetailUiState>()
            val job = testScope.launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie("Title1")

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }
}
