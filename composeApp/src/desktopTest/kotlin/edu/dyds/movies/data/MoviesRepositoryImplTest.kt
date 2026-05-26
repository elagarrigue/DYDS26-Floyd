package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.data.fakes.FakeMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplTest {

    private lateinit var moviesExternalSource: FakeMoviesExternalSource
    private lateinit var movieExternalSource: FakeMovieExternalSource
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var repository: MoviesRepositoryImpl

    @BeforeTest
    fun setup() {
        moviesExternalSource = FakeMoviesExternalSource()
        movieExternalSource = FakeMovieExternalSource()
        localDataSource = FakeLocalDataSource()
        repository = MoviesRepositoryImpl(moviesExternalSource, movieExternalSource, localDataSource)
    }

    @Test
    fun `getPopularMovies deberia retornar peliculas del cache si hay datos cacheados`() = runTest {
        val cachedMovies = listOf(
            Movie(3, "Cached", "Cached overview", "2023-01-03", "poster3", "backdrop3", "Cached", "en", 8.0, 5.0)
        )
        localDataSource.saveMovies(cachedMovies)
        val result = repository.getPopularMovies()
        assertEquals(cachedMovies, result)
    }

    @Test
    fun `getPopularMovies deberia devolver las peliculas de la API cuando el cache esta vacio`() = runTest {
        val result = repository.getPopularMovies()
        assertEquals(moviesExternalSource.movies, result)
    }

    @Test
    fun `getPopularMovies deberia cachear las peliculas obtenidas de la API cuando el cache esta vacio`() = runTest {
        repository.getPopularMovies()
        assertEquals(moviesExternalSource.movies, localDataSource.getMovies())
    }

    @Test
    fun `getPopularMovies deberia retornar lista vacia cuando la API falla`() = runTest {
        moviesExternalSource.shouldFail = true
        val result = repository.getPopularMovies()
        assertEquals(emptyList<Movie>(), result)
    }

    @Test
    fun `getPopularMovies no deberia actualizar el cache cuando la API falla`() = runTest {
        moviesExternalSource.shouldFail = true
        repository.getPopularMovies()
        assertEquals(emptyList<Movie>(), localDataSource.getMovies())
    }

    @Test
    fun `getMovieByTitle deberia retornar la pelicula cuando la fuente externa la encuentra`() = runTest {
        val result = repository.getMovieByTitle("Title1")
        assertEquals(movieExternalSource.movieToReturn, result)
    }

    @Test
    fun `getMovieByTitle deberia retornar null cuando la fuente externa falla`() = runTest {
        movieExternalSource.shouldFail = true
        val result = repository.getMovieByTitle("Title1")
        assertEquals(null, result)
    }
}
