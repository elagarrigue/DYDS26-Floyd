package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPopularMoviesUseCaseImplTest {

    private class FakeMoviesRepository : MoviesRepository {
        var movies: List<Movie> = emptyList()

        override suspend fun getPopularMovies(): List<Movie> = movies

        override suspend fun getMovieDetails(id: Int): Movie? = movies.find { it.id == id }
    }

    private fun buildMovie(id: Int, voteAverage: Double) = Movie(
        id = id,
        title = "Title$id",
        overview = "Overview$id",
        releaseDate = "2023-01-01",
        poster = "poster$id",
        backdrop = "backdrop$id",
        originalTitle = "Original$id",
        originalLanguage = "en",
        popularity = 10.0,
        voteAverage = voteAverage
    )

    @Test
    fun `execute deberia marcar como buena pelicula la que tiene voteAverage mayor o igual a 6`() = runTest {
        // arrange
        val goodMovie = buildMovie(id = 1, voteAverage = 6.0)
        val repository = FakeMoviesRepository().apply { movies = listOf(goodMovie) }
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        assertEquals(true, result.first().isGoodMovie)
    }

    @Test
    fun `execute deberia marcar como mala pelicula la que tiene voteAverage menor a 6`() = runTest {
        // arrange
        val badMovie = buildMovie(id = 1, voteAverage = 5.9)
        val repository = FakeMoviesRepository().apply { movies = listOf(badMovie) }
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        assertEquals(false, result.first().isGoodMovie)
    }

    @Test
    fun `execute deberia retornar las peliculas ordenadas por voteAverage de mayor a menor`() = runTest {
        // arrange
        val movieLow = buildMovie(id = 1, voteAverage = 5.0)
        val movieHigh = buildMovie(id = 2, voteAverage = 8.0)
        val movieMid = buildMovie(id = 3, voteAverage = 6.5)
        val repository = FakeMoviesRepository().apply { movies = listOf(movieLow, movieHigh, movieMid) }
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        assertEquals(listOf(8.0, 6.5, 5.0), result.map { it.movie.voteAverage })
    }

    @Test
    fun `execute deberia retornar lista vacia cuando el repositorio no tiene peliculas`() = runTest {
        // arrange
        val repository = FakeMoviesRepository().apply { movies = emptyList() }
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `execute deberia retornar la pelicula con los datos correctos`() = runTest {
        // arrange
        val movie = buildMovie(id = 1, voteAverage = 7.5)
        val repository = FakeMoviesRepository().apply { movies = listOf(movie) }
        val useCase = GetPopularMoviesUseCaseImpl(repository)

        // act
        val result = useCase.execute()

        // assert
        assertEquals(QualifiedMovie(movie, isGoodMovie = true), result.first())
    }
}
