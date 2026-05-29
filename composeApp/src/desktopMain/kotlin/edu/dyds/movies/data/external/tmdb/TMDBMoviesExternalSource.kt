package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.data.external.PopularMoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TMDBMoviesExternalSource(private val httpClient: HttpClient) : PopularMoviesExternalSource, MovieDetailExternalSource {

    override suspend fun getPopularMovies(): List<Movie> =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body<RemoteResult>().results.map { it.toDomainMovie() }

    override suspend fun getMovieByTitle(title: String): Movie? =
        runCatching {
            httpClient.get("/3/search/movie?query=${title}").body<RemoteResult>().results.firstOrNull()?.toDomainMovie()
        }.getOrNull()
}
