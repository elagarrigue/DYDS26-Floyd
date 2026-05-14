package edu.dyds.movies.presentation.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase

class FakeGetMovieDetailsUseCase : GetMovieDetailsUseCase {
    var shouldFail = false
    var movie: Movie? = Movie(
        id = 1,
        title = "Title1",
        overview = "Overview1",
        releaseDate = "2023-01-01",
        poster = "poster1",
        backdrop = "backdrop1",
        originalTitle = "Original1",
        originalLanguage = "en",
        popularity = 10.0,
        voteAverage = 7.0
    )

    override suspend fun execute(id: Int): Movie? {
        if (shouldFail) throw Exception("Use case error")
        return movie
    }
}
