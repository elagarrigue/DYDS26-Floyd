package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult

class FakeMoviesApiService : MoviesApiService {
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

    override suspend fun getMovieByTitle(title: String): RemoteMovie {
        if (shouldFail) throw Exception("API error")
        return popularMovies.firstOrNull { it.title == title }
            ?: movieDetails
            ?: throw Exception("Not found")
    }
}
