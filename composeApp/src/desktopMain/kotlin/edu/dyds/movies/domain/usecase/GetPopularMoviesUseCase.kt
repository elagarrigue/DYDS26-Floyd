package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class GetPopularMoviesUseCase(private val repository: MoviesRepository) {

    suspend fun execute(): List<QualifiedMovie> {
        val movies = repository.getPopularMovies()
        return movies
            .sortedByDescending { it.voteAverage }
            .map { QualifiedMovie(it, it.voteAverage >= MIN_VOTE_AVERAGE) }
    }
}
