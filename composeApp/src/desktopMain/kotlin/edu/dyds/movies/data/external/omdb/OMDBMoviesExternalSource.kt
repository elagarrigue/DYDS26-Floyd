package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieDetailExternalSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val OMDB_API_KEY = "a96e7f78"

class OMDBMoviesExternalSource : MovieDetailExternalSource {

    private val omdbHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "www.omdbapi.com"
                parameters.append("apikey", OMDB_API_KEY)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        return try {
            omdbHttpClient.get("/?t=$title").body<OMDBRemoteMovie>().toDomainMovie()
        } catch (e: Exception) {
            null
        }
    }
}
