package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
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
class DetailViewModelTest {

    private class FakeGetMovieDetailsUseCase : GetMovieDetailsUseCase {
        var shouldFail = false
        var movie: Movie? = Movie(
            id = 1,
            title = "Title1",
            overview = "Overview1",
            releaseDate = "2023-01-01",
            poster = "poster1",
            backdrop = "backdrop1",
            originalTitle = "Original1",
            originalLanguage = "en",
            popularity = 10.0,
            voteAverage = 7.0
        )

        override suspend fun execute(id: Int): Movie? {
            if (shouldFail) throw Exception("Use case error")
            return movie
        }
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: FakeGetMovieDetailsUseCase
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setup() {
        // arrange comun
        Dispatchers.setMain(testDispatcher)
        useCase = FakeGetMovieDetailsUseCase()
        viewModel = DetailViewModel(useCase)
    }

    @Test
    fun `loadMovie deberia actualizar uiState con la pelicula cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<DetailUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie(1)
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertEquals(useCase.movie, states.last().movie)
        }

    @Test
    fun `loadMovie deberia dejar isLoading en false cuando el caso de uso tiene exito`() =
        runTest(testDispatcher) {
            // arrange
            val states = mutableListOf<DetailUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie(1)
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }

    @Test
    fun `loadMovie deberia actualizar uiState con pelicula null cuando el caso de uso falla`() =
        runTest(testDispatcher) {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<DetailUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie(1)
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertEquals(null, states.last().movie)
        }

    @Test
    fun `loadMovie deberia dejar isLoading en false cuando el caso de uso falla`() =
        runTest(testDispatcher) {
            // arrange
            useCase.shouldFail = true
            val states = mutableListOf<DetailUiState>()
            val job = launch { viewModel.uiState.collect { states.add(it) } }

            // act
            viewModel.loadMovie(1)
            testDispatcher.scheduler.advanceUntilIdle()

            // assert
            job.cancel()
            assertFalse(states.last().isLoading)
        }
}
