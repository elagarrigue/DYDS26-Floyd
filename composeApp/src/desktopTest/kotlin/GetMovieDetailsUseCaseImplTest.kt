package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMovieDetailsUseCaseImplTest {

    private class FakeMoviesRepository : MoviesRepository {
        var movie: Movie? = Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0)

        override suspend fun getPopularMovies(): List<Movie> = emptyList()

        override suspend fun getMovieDetails(id: Int): Movie? = if (id == 1) movie else null
    }

    @Test
    fun `execute should return movie when found`() = runTest {
        // arrange
        val repository = FakeMoviesRepository()
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase.execute(1)

        // assert
        assertEquals(repository.movie, result)
    }

    @Test
    fun `execute should return null when not found`() = runTest {
        // arrange
        val repository = FakeMoviesRepository()
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase.execute(2)

        // assert
        assertEquals(null, result)
    }
}
