package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    init {
        Dispatchers.setMain(testDispatcher)
    }

    private class FakeGetMovieDetailsUseCase : GetMovieDetailsUseCase {
        var shouldFail = false
        var movie: Movie? = Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0)

        override suspend fun execute(id: Int): Movie? {
            if (shouldFail) throw Exception("Use case error")
            return movie
        }
    }

    @Test
    fun `loadMovie should update uiState with movie on success`() = runTest(testDispatcher) {
        // arrange
        val useCase = FakeGetMovieDetailsUseCase()
        val viewModel = DetailViewModel(useCase)
        val states = mutableListOf<DetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        // act
        viewModel.loadMovie(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // assert
        job.cancel()
        val finalState = states.last()
        assertEquals(false, finalState.isLoading)
        assertEquals(useCase.movie, finalState.movie)
    }

    @Test
    fun `loadMovie should update uiState with null movie on failure`() = runTest(testDispatcher) {
        // arrange
        val useCase = FakeGetMovieDetailsUseCase().apply { shouldFail = true }
        val viewModel = DetailViewModel(useCase)
        val states = mutableListOf<DetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        // act
        viewModel.loadMovie(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // assert
        job.cancel()
        val finalState = states.last()
        assertEquals(false, finalState.isLoading)
        assertEquals(null, finalState.movie)
    }
}
