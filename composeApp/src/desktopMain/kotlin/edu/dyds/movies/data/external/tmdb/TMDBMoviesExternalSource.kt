package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MoviesApiService
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TMDBMoviesExternalSource(private val httpClient: HttpClient) : MoviesApiService {

    override suspend fun getPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    override suspend fun getMovieDetails(id: Int): RemoteMovie =
        httpClient.get("/3/movie/$id").body()

    override suspend fun getMovieByTitle(title: String): RemoteMovie =
        getTMDBMovieDetails(title)

    private suspend fun getTMDBMovieDetails(title: String): RemoteMovie =
        httpClient.get("/3/search/movie?query=${title}").body<RemoteResult>().results.first()
}
