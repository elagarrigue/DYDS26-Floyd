package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val apiService: MoviesApiService,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cached = localDataSource.getMovies()
        if (cached.isNotEmpty()) {
            return cached
        }
        return try {
            val movies = apiService.getPopularMovies().results.map { it.toDomainMovie() }
            localDataSource.saveMovies(movies)
            movies
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            apiService.getMovieDetails(id).toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        return try {
            apiService.getMovieByTitle(title).toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}