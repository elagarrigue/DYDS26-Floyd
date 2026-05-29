package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBRemoteMovie(
    @SerialName("imdbID") val imdbId: String = "",
    @SerialName("Title") val title: String = "",
    @SerialName("Plot") val plot: String = "",
    @SerialName("Released") val released: String = "",
    @SerialName("Poster") val poster: String = "",
    @SerialName("originalTitle") val originalTitle: String = "",
    @SerialName("Language") val language: String = "",
    @SerialName("imdbRating") val imdbRating: String = "N/A",
    @SerialName("Response") val response: String = "False"
) {
    fun toDomainMovie(): Movie {
        val rating = imdbRating.takeIf { it != "N/A" }?.toDoubleOrNull() ?: 0.0
        val posterUrl = poster.takeIf { it != "N/A" } ?: ""
        return Movie(
            id = imdbId.hashCode(),
            title = title,
            overview = plot.takeIf { it != "N/A" } ?: "",
            releaseDate = released.takeIf { it != "N/A" } ?: "",
            poster = posterUrl,
            backdrop = null,
            originalTitle = originalTitle.ifEmpty { title },
            originalLanguage = language.takeIf { it != "N/A" } ?: "",
            popularity = 0.0,
            voteAverage = rating
        )
    }
}
