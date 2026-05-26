package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeMoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMovieDetailsUseCaseImplTest {

    private fun buildMovie(id: Int, title: String = "Title$id") = Movie(
        id = id,
        title = title,
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
    fun `invoke deberia retornar la pelicula cuando el repositorio la encuentra por titulo`() = runTest {
        // arrange
        val movie = buildMovie(1, "Title1")
        val repository = FakeMoviesRepository().apply { movieToReturn = movie }
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase("Title1")

        // assert
        assertEquals(movie, result)
    }

    @Test
    fun `invoke deberia retornar null cuando el repositorio no encuentra la pelicula`() = runTest {
        // arrange
        val repository = FakeMoviesRepository().apply { movieToReturn = null }
        val useCase = GetMovieDetailsUseCaseImpl(repository)

        // act
        val result = useCase("TituloInexistente")

        // assert
        assertEquals(null, result)
    }
}
