package edu.dyds.movies.data.external.broker

import edu.dyds.movies.data.fakes.FakeMovieExternalSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MovieBrokerTest {

    private lateinit var fakeTmdb: FakeMovieExternalSource
    private lateinit var fakeOmdb: FakeMovieExternalSource
    private lateinit var broker: MovieBroker

    private val tmdbMovie = Movie(
        id = 1, title = "Inception", overview = "A thief who steals via dreams",
        releaseDate = "2010-07-16", poster = "https://tmdb/poster.jpg",
        backdrop = "https://tmdb/backdrop.jpg", originalTitle = "Inception",
        originalLanguage = "en", popularity = 95.0, voteAverage = 8.8
    )

    private val omdbMovie = Movie(
        id = 0, title = "Inception", overview = "A thief enters people's dreams",
        releaseDate = "16 Jul 2010", poster = "https://omdb/poster.jpg",
        backdrop = null, originalTitle = "Inception",
        originalLanguage = "English", popularity = 0.0, voteAverage = 8.8
    )

    @BeforeTest
    fun setup() {
        fakeTmdb = FakeMovieExternalSource()
        fakeOmdb = FakeMovieExternalSource()
        broker = MovieBroker(fakeTmdb, fakeOmdb)
    }

    @Test
    fun `getMovieByTitle deberia combinar tmdb y omdb cuando ambos responden`() = runTest {
        fakeTmdb.movieToReturn = tmdbMovie
        fakeOmdb.movieToReturn = omdbMovie

        val result = broker.getMovieByTitle("Inception")

        assertEquals(tmdbMovie.id, result?.id)
        assertEquals(tmdbMovie.title, result?.title)
        assertEquals(omdbMovie.overview, result?.overview)
        assertEquals(tmdbMovie.poster, result?.poster)
        assertEquals(tmdbMovie.backdrop, result?.backdrop)
        assertEquals(tmdbMovie.voteAverage, result?.voteAverage)
    }

    @Test
    fun `getMovieByTitle deberia retornar pelicula TMDB con prefijo cuando solo TMDB responde`() = runTest {
        fakeTmdb.movieToReturn = tmdbMovie
        fakeOmdb.shouldFail = true

        val result = broker.getMovieByTitle("Inception")

        assertTrue(result?.overview?.startsWith("TMDB: ") == true)
        assertEquals(tmdbMovie.id, result?.id)
        assertEquals(tmdbMovie.title, result?.title)
    }

    @Test
    fun `getMovieByTitle deberia retornar pelicula OMDB con prefijo cuando solo OMDB responde`() = runTest {
        fakeTmdb.shouldFail = true
        fakeOmdb.movieToReturn = omdbMovie

        val result = broker.getMovieByTitle("Inception")

        assertTrue(result?.overview?.startsWith("OMDB: ") == true)
        assertEquals(omdbMovie.title, result?.title)
    }

    @Test
    fun `getMovieByTitle deberia retornar null cuando ambos servicios fallan`() = runTest {
        fakeTmdb.shouldFail = true
        fakeOmdb.shouldFail = true

        val result = broker.getMovieByTitle("Inception")

        assertNull(result)
    }
}
