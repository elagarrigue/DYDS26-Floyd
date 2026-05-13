package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPopularMoviesUseCaseImplTest {

    private class FakeMoviesRepository : MoviesRepository {
        var movies = listOf(
            Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.5),
            Movie(2, "Title2", "Overview2", "2023-01-02", "poster2", "backdrop2", "Original2", "en", 9.0, 5.5)
        )

        override suspend fun getPopularMovies(): List<Movie> = movies

        override suspend fun getMovieDetails(id: Int): Movie? = movies.find { it.id == id }
    }

    @Test
    fun `execute should return sorted and qualified movies`() = runTest {
        // arrange
        val repository = FakeMoviesRepository()
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        val expected = listOf(
            QualifiedMovie(repository.movies[0], true), // 7.5 >= 6.0
            QualifiedMovie(repository.movies[1], false)  // 5.5 < 6.0
        )
        assertEquals(expected, result)
    }
}
