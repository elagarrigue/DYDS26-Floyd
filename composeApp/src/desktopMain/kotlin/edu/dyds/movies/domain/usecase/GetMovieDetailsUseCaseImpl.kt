package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetMovieDetailsUseCaseImpl(private val repository: MoviesRepository) : GetMovieDetailsUseCase {

    override suspend fun invoke(title: String): Movie? = repository.getMovieByTitle(title)
}