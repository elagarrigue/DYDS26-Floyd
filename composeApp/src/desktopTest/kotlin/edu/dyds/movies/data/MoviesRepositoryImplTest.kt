package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplTest {

    private class FakeMoviesApiService : MoviesApiService {
        var shouldFail = false
        var popularMovies = listOf(
            RemoteMovie(1, "Title1", "Overview1", "2023-01-01", "/poster1.jpg", "/backdrop1.jpg", "Original1", "en", 10.0, 7.0),
            RemoteMovie(2, "Title2", "Overview2", "2023-01-02", "/poster2.jpg", "/backdrop2.jpg", "Original2", "en", 9.0, 6.0)
        )
        var movieDetails: RemoteMovie? =
            RemoteMovie(1, "Title1", "Overview1", "2023-01-01", "/poster1.jpg", "/backdrop1.jpg", "Original1", "en", 10.0, 7.0)

        override suspend fun getPopularMovies(): RemoteResult {
            if (shouldFail) throw Exception("API error")
            return RemoteResult(1, popularMovies, 1, popularMovies.size)
        }

        override suspend fun getMovieDetails(id: Int): RemoteMovie {
            if (shouldFail) throw Exception("API error")
            return movieDetails ?: throw Exception("Not found")
        }
    }

    private class FakeLocalDataSource : LocalDataSource {
        private val cache: MutableList<Movie> = mutableListOf()

        override fun getMovies(): List<Movie> = cache.toList()

        override fun saveMovies(movies: List<Movie>) {
            cache.clear()
            cache.addAll(movies)
        }
    }

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
        // arrange
        // localDataSource vacío por defecto

        // act
        val result = repository.getPopularMovies()

        // assert
        val expected = apiService.popularMovies.map { it.toDomainMovie() }
        assertEquals(expected, result)
    }

    @Test
    fun `getPopularMovies deberia cachear las peliculas obtenidas de la API cuando el cache esta vacio`() = runTest {
        // arrange
        // localDataSource vacío por defecto

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
        // arrange

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
}
