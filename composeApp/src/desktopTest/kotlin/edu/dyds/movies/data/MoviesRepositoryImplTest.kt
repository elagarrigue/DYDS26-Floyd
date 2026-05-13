package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import edu.dyds.movies.data.local.LocalDataSourceImpl
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesRepositoryImplTest {

    private val localDataSource = LocalDataSourceImpl()

    private class FakeMoviesApiService : MoviesApiService {
        var shouldFail = false
        var popularMovies = listOf(
            RemoteMovie(1, "Title1", "Overview1", "2023-01-01", "/poster1.jpg", "/backdrop1.jpg", "Original1", "en", 10.0, 7.0),
            RemoteMovie(2, "Title2", "Overview2", "2023-01-02", "/poster2.jpg", "/backdrop2.jpg", "Original2", "en", 9.0, 6.0)
        )
        var movieDetails: RemoteMovie? = RemoteMovie(1, "Title1", "Overview1", "2023-01-01", "/poster1.jpg", "/backdrop1.jpg", "Original1", "en", 10.0, 7.0)

        override suspend fun getPopularMovies(): RemoteResult {
            if (shouldFail) throw Exception("API error")
            return RemoteResult(1, popularMovies, 1, popularMovies.size)
        }

        override suspend fun getMovieDetails(id: Int): RemoteMovie {
            if (shouldFail) throw Exception("API error")
            return movieDetails ?: throw Exception("Not found")
        }
    }

    @Test
    fun `getPopularMovies should return cached movies if available`() = runTest {
        // arrange
        val apiService = FakeMoviesApiService()
        val repository = MoviesRepositoryImpl(apiService, localDataSource)
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
    fun `getPopularMovies should fetch from api and cache when cache empty`() = runTest {
        // arrange
        val apiService = FakeMoviesApiService()
        val repository = MoviesRepositoryImpl(apiService, localDataSource)

        // act
        val result = repository.getPopularMovies()

        // assert
        val expected = apiService.popularMovies.map { it.toDomainMovie() }
        assertEquals(expected, result)
        assertEquals(expected, localDataSource.getMovies())
    }

    @Test
    fun `getPopularMovies should return empty list on api failure`() = runTest {
        // arrange
        val apiService = FakeMoviesApiService().apply { shouldFail = true }
        val repository = MoviesRepositoryImpl(apiService, localDataSource)

        // act
        val result = repository.getPopularMovies()

        // assert
        assertEquals(emptyList<Movie>(), result)
    }

    @Test
    fun `getMovieDetails should return movie on success`() = runTest {
        // arrange
        val apiService = FakeMoviesApiService()
        val repository = MoviesRepositoryImpl(apiService, localDataSource)

        // act
        val result = repository.getMovieDetails(1)

        // assert
        val expected = apiService.movieDetails?.toDomainMovie()
        assertEquals(expected, result)
    }

    @Test
    fun `getMovieDetails should return null on failure`() = runTest {
        // arrange
        val apiService = FakeMoviesApiService().apply { shouldFail = true }
        val repository = MoviesRepositoryImpl(apiService, localDataSource)

        // act
        val result = repository.getMovieDetails(1)

        // assert
        assertEquals(null, result)
    }
}
