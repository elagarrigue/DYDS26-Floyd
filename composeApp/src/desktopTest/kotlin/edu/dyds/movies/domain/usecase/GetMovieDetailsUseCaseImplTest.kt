package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeMoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMovieDetailsUseCaseImplTest {

    private fun buildMovie(id: Int) = Movie(
        id = id,
        title = "Title$id",
        overview = "Overview$id",
        releaseDate = "2023-01-01",
        poster = "poster$id",
        backdrop = "backdrop$id",
        originalTitle = "Original$id",
        originalLanguage = "en",
        popularity = 10.0,
        voteAverage = 7.0
    )

    @Test
    fun `execute deberia retornar la pelicula cuando el repositorio la encuentra`() = runTest {
        // arrange
        val movie = buildMovie(1)
        val repository = FakeMoviesRepository().apply { movieToReturn = movie }
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase.execute(1)

        // assert
        assertEquals(movie, result)
    }

    @Test
    fun `execute deberia retornar null cuando el repositorio no encuentra la pelicula`() = runTest {
        // arrange
        val repository = FakeMoviesRepository().apply { movieToReturn = null }
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase.execute(99)

        // assert
        assertEquals(null, result)
    }
}
