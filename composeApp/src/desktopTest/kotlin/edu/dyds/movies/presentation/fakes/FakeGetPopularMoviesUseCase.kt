package edu.dyds.movies.presentation.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase

class FakeGetPopularMoviesUseCase : GetPopularMoviesUseCase {
    var shouldFail = false
    var movies = listOf(
        QualifiedMovie(
            Movie(1, "Title1", "Overview1", "2023-01-01", "poster1", "backdrop1", "Original1", "en", 10.0, 7.0),
            isGoodMovie = true
        )
    )

    override suspend fun execute(): List<QualifiedMovie> {
        if (shouldFail) throw Exception("Use case error")
        return movies
    }
}
