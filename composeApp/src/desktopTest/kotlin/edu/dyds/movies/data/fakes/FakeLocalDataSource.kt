package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.local.LocalDataSource
import edu.dyds.movies.domain.entity.Movie

class FakeLocalDataSource : LocalDataSource {
    private val cache: MutableList<Movie> = mutableListOf()

    override fun getMovies(): List<Movie> = cache.toList()

    override fun saveMovies(movies: List<Movie>) {
        cache.clear()
        cache.addAll(movies)
    }
}
