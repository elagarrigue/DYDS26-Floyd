package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(private val apiService: MoviesApiService) : MoviesRepository {

    private val cacheMovies: MutableList<Movie> = mutableListOf()

    override suspend fun getPopularMovies(): List<Movie> {
        if (cacheMovies.isNotEmpty()) {
            return cacheMovies
        }
        return try {
            apiService.getPopularMovies().results.map { it.toDomainMovie() }.apply {
                cacheMovies.clear()
                cacheMovies.addAll(this)
            }
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
}
