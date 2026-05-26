package edu.dyds.movies.data

import edu.dyds.movies.data.fakes.FakeLocalDataSource
import edu.dyds.movies.data.fakes.FakeMoviesApiService
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplTest {

    private lateinit var apiService: FakeMoviesApiService
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var repository: MoviesRepositoryImpl

    @BeforeTest
    fun setup() {
        apiService = FakeMoviesApiService()
        localDataSource = FakeLocalDataSource()
        repository = MoviesRepositoryImpl(apiService, localDataSource)
    }

    @Test
    fun `getPopularMovies deberia retornar peliculas del cache si hay datos cacheados`() = runTest {
        // arrange
        val cachedMovies = listOf(
            Movie(3, "Cached", "Cached overview", "2023-01-03", "poster3", "backdrop3", "Cached", "en", 8.0, 5.0)
        )
        localDataSource.saveMovies(cachedMovies)

        // act
        val result = repository.getPopularMovies()

        // assert
        assertEquals(cachedMovies, result)
    }

    @Test
    fun `getPopularMovies deberia devolver las peliculas de la API cuando el cache esta vacio`() = runTest {
        // act
        val result = repository.getPopularMovies()

        // assert
        val expected = apiService.popularMovies.map { it.toDomainMovie() }
        assertEquals(expected, result)
    }

    @Test
    fun `getPopularMovies deberia cachear las peliculas obtenidas de la API cuando el cache esta vacio`() = runTest {
        // act
        repository.getPopularMovies()

        // assert
        val expected = apiService.popularMovies.map { it.toDomainMovie() }
        assertEquals(expected, localDataSource.getMovies())
    }

    @Test
    fun `getPopularMovies deberia retornar lista vacia cuando la API falla`() = runTest {
        // arrange
        apiService.shouldFail = true

        // act
        val result = repository.getPopularMovies()

        // assert
        assertEquals(emptyList<Movie>(), result)
    }

    @Test
    fun `getPopularMovies no deberia actualizar el cache cuando la API falla`() = runTest {
        // arrange
        apiService.shouldFail = true

        // act
        repository.getPopularMovies()

        // assert
        assertEquals(emptyList<Movie>(), localDataSource.getMovies())
    }

    @Test
    fun `getMovieDetails deberia retornar la pelicula cuando la API responde correctamente`() = runTest {
        // act
        val result = repository.getMovieDetails(1)

        // assert
        val expected = apiService.movieDetails?.toDomainMovie()
        assertEquals(expected, result)
    }

    @Test
    fun `getMovieDetails deberia retornar null cuando la API falla`() = runTest {
        // arrange
        apiService.shouldFail = true

        // act
        val result = repository.getMovieDetails(1)

        // assert
        assertEquals(null, result)
    }

    @Test
    fun `getMovieByTitle deberia retornar la pelicula cuando la API la encuentra`() = runTest {
        // act
        val result = repository.getMovieByTitle("Title1")

        // assert
        val expected = apiService.popularMovies.first { it.title == "Title1" }.toDomainMovie()
        assertEquals(expected, result)
    }

    @Test
    fun `getMovieByTitle deberia retornar null cuando la API falla`() = runTest {
        // arrange
        apiService.shouldFail = true

        // act
        val result = repository.getMovieByTitle("Title1")

        // assert
        assertEquals(null, result)
    }
}
