package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDataSourceImplTest {

    private val localDataSource = LocalDataSourceImpl()

    @Test
    fun `getMovies should return empty list when no movies saved`() {
        // arrange
        val localDataSource = LocalDataSourceImpl()

        // act
        val result = localDataSource.getMovies()

        // assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `saveMovies should save movies and getMovies should return them`() {
        // arrange
        val localDataSource = LocalDataSourceImpl()
        val movies = listOf(
            Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0),
            Movie(2, "Title2", "Overview2", "2023-01-02", "poster2", "backdrop2", "Original2", "en", 9.0, 6.0)
        )

        // act
        localDataSource.saveMovies(movies)
        val result = localDataSource.getMovies()

        // assert
        assertEquals(movies, result)
    }

    @Test
    fun `saveMovies should overwrite previous movies`() {
        // arrange
        val localDataSource = LocalDataSourceImpl()
        val initialMovies = listOf(
            Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0)
        )
        localDataSource.saveMovies(initialMovies)
        val newMovies = listOf(
            Movie(2, "Title2", "Overview2", "2023-01-02", "poster2", "backdrop2", "Original2", "en", 9.0, 6.0)
        )

        // act
        localDataSource.saveMovies(newMovies)
        val result = localDataSource.getMovies()

        // assert
        assertEquals(newMovies, result)
    }
}
