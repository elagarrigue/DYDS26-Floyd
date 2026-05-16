package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalDataSourceImplTest {

    private lateinit var localDataSource: LocalDataSourceImpl

    @BeforeTest
    fun setUp() {
        localDataSource = LocalDataSourceImpl()
    }

    private fun buildMovie(id: Int, title: String = "Title$id") = Movie(
        id = id,
        title = title,
        overview = "Overview$id",
        releaseDate = "2023-01-0$id",
        poster = "poster$id",
        backdrop = "backdrop$id",
        originalTitle = "Original$id",
        originalLanguage = "en",
        popularity = 10.0,
        voteAverage = 7.0
    )

    @Test
    fun `getMovies deberia retornar lista vacia cuando no se guardaron peliculas`() {
        // arrange

        // act
        val result = localDataSource.getMovies()

        // assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `saveMovies deberia guardar las peliculas y getMovies deberia retornarlas`() {
        // arrange
        val movies = listOf(buildMovie(1), buildMovie(2))

        // act
        localDataSource.saveMovies(movies)
        val result = localDataSource.getMovies()

        // assert
        assertEquals(movies, result)
    }

    @Test
    fun `saveMovies deberia sobreescribir las peliculas anteriores`() {
        // arrange
        localDataSource.saveMovies(listOf(buildMovie(1)))
        val newMovies = listOf(buildMovie(2))

        // act
        localDataSource.saveMovies(newMovies)
        val result = localDataSource.getMovies()

        // assert
        assertEquals(newMovies, result)
    }

    @Test
    fun `saveMovies con lista vacia deberia limpiar el cache`() {
        // arrange
        localDataSource.saveMovies(listOf(buildMovie(1)))

        // act
        localDataSource.saveMovies(emptyList())
        val result = localDataSource.getMovies()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovies deberia retornar la cantidad correcta de peliculas guardadas`() {
        // arrange
        val movies = listOf(buildMovie(1), buildMovie(2), buildMovie(3))
        localDataSource.saveMovies(movies)

        // act
        val result = localDataSource.getMovies()

        // assert
        assertEquals(3, result.size)
    }
}
