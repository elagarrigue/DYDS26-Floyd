package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesExternalSource : PopularMoviesExternalSource {
    var shouldFail = false
    var movies: List<Movie> = listOf(
        Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0),
        Movie(2, "Title2", "Overview2", "2023-01-02", "poster2", "backdrop2", "Original2", "en", 9.0, 6.0)
    )

    override suspend fun getPopularMovies(): List<Movie> {
        if (shouldFail) throw Exception("API error")
        return movies
    }
}
