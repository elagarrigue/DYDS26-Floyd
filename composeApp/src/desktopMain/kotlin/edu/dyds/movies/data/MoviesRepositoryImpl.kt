package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val moviesExternalSource: PopularMoviesExternalSource,
    private val movieExternalSource: MovieDetailExternalSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getMovies()
        if (cached.isNotEmpty()) {
            return cached
        }
        return try {
            val movies = moviesExternalSource.getPopularMovies()
            localDataSource.saveMovies(movies)
            movies
        } catch (e: Exception) {
            emptyList()
        }
    }


    override suspend fun getMovieByTitle(title: String): Movie? {
        return try {
            movieExternalSource.getMovieByTitle(title)
        } catch (e: Exception) {
            null
        }
    }
}
