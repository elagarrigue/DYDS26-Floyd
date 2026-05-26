package edu.dyds.movies.data.external.broker

import edu.dyds.movies.data.external.MovieExternalSource
import edu.dyds.movies.domain.entity.Movie

class MovieBroker(
    private val tmdbSource: MovieExternalSource,
    private val omdbSource: MovieExternalSource
) : MovieExternalSource {

    override suspend fun getMovieByTitle(title: String): Movie? {
        val tmdbMovie = runCatching { tmdbSource.getMovieByTitle(title) }.getOrNull()
        val omdbMovie = runCatching { omdbSource.getMovieByTitle(title) }.getOrNull()

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
            omdbMovie != null -> omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
            else -> null
        }
    }

    private fun buildMovie(tmdb: Movie, omdb: Movie): Movie = Movie(
        id = tmdb.id,
        title = tmdb.title,
        overview = omdb.overview,
        releaseDate = tmdb.releaseDate.ifEmpty { omdb.releaseDate },
        poster = tmdb.poster.ifEmpty { omdb.poster },
        backdrop = tmdb.backdrop ?: omdb.backdrop,
        originalTitle = tmdb.originalTitle,
        originalLanguage = tmdb.originalLanguage,
        popularity = tmdb.popularity,
        voteAverage = if (tmdb.voteAverage != 0.0) tmdb.voteAverage else omdb.voteAverage
    )
}
