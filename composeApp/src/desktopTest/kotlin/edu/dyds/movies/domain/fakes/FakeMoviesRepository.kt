package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository : MoviesRepository {
    var movies: List<Movie> = emptyList()
    var movieToReturn: Movie? = null

    override suspend fun getPopularMovies(): List<Movie> = movies


    override suspend fun getMovieByTitle(title: String): Movie? = movieToReturn ?: movies.find { it.title == title }
}
