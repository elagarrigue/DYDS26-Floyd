package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie

class FakeMovieExternalSource : MovieDetailExternalSource {
    var shouldFail = false
    var movieToReturn: Movie? = Movie(
        id = 1, title = "Title1", overview = "Overview1", releaseDate = "2023-01-01",
        poster = "poster1", backdrop = "backdrop1", originalTitle = "Original1",
        originalLanguage = "en", popularity = 10.0, voteAverage = 7.0
    )

    override suspend fun getMovieByTitle(title: String): Movie? {
        if (shouldFail) throw Exception("API error")
        return movieToReturn
    }
}
