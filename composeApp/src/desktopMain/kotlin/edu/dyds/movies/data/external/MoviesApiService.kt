package edu.dyds.movies.data.external

interface MoviesApiService {
    suspend fun getPopularMovies(): RemoteResult
    suspend fun getMovieDetails(id: Int): RemoteMovie
}