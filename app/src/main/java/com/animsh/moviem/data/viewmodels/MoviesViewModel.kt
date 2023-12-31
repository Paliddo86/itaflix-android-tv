package com.animsh.moviem.data.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.animsh.moviem.data.database.entity.FavoriteMovieEntity
import com.animsh.moviem.data.repositories.Repository
import com.animsh.moviem.models.movie.*
import com.animsh.moviem.util.Constants.Companion.hasInternetConnection
import com.animsh.moviem.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by animsh on 2/26/2021.
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    // Room
    var readFavMovie: LiveData<List<FavoriteMovieEntity>> =
        repository.local.readFavMovies().asLiveData()

    fun insertFavMovie(favoriteMovieEntity: FavoriteMovieEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavMovie(favoriteMovieEntity)
        }

    fun deleteFavMovie(favoriteMovieEntity: FavoriteMovieEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavMovie(favoriteMovieEntity)
        }

    fun deleteAllFavMovies() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllFavMovies()
    }

    // online
    var latestMovieResponse: MutableLiveData<NetworkResult<Movie>> = MutableLiveData()

    var movieDetailsResponse: MutableLiveData<NetworkResult<Movie>> = MutableLiveData()

    var similarMoviesResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var recommendationMoviesResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var movieCreditsResponse: MutableLiveData<NetworkResult<CreditsResponse>> =
        MutableLiveData()

    var comingSoonResponse: MutableLiveData<NetworkResult<ComingSoonResponse>> =
        MutableLiveData()


    var searchMovieResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var trendingMovieResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var popularMovieResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var topMoviesResponse: MutableLiveData<NetworkResult<CommonMovieResponse>> =
        MutableLiveData()

    var nowPlayingMoviesResponse: MutableLiveData<NetworkResult<UniqueMovieResponse>> =
        MutableLiveData()

    fun getLatestMovie(apiKey: String) = viewModelScope.launch {
        getLatestMovieSafeCall(apiKey)
    }

    fun getMovieDetails(movieId: Int, apiKey: String) = viewModelScope.launch {
        getMovieDetailsSafeCall(movieId, apiKey)
    }

    fun getSimilarMovies(movieId: Int, apiKey: String, page: Int) = viewModelScope.launch {
        getSimilarMoviesSafeCall(movieId, apiKey, page)
    }

    fun getRecommendationsMovies(movieId: Int, apiKey: String, page: Int) = viewModelScope.launch {
        getRecommendationMoviesSafeCall(movieId, apiKey, page)
    }

    fun getMovieCredits(movieId: Int, apiKey: String) = viewModelScope.launch {
        getMovieCreditsSafeCall(movieId, apiKey)
    }

    fun getComingSonMovies(apiKey: String, page: Int) = viewModelScope.launch {
        getComingSoonMoviesSafeCall(apiKey, page)
    }

    fun searchMovies(apiKey: String, query: String) = viewModelScope.launch {
        searchMoviesSafeCall(apiKey, query)
    }

    fun getTrendingMovies(apiKey: String, page: Int) = viewModelScope.launch {
        getTrendingMoviesSafeCall(apiKey, page)
    }

    fun getPopularMovies(apiKey: String, page: Int) = viewModelScope.launch {
        getPopularMoviesSafeCall(apiKey, page)
    }

    fun getTopMovies(apiKey: String, page: Int) = viewModelScope.launch {
        getTopMoviesSafeCall(apiKey, page)
    }

    fun getNowPlayingMovies(apiKey: String, page: Int) = viewModelScope.launch {
        getNowPlayingMoviesSafeCall(apiKey, page)
    }

    private suspend fun getLatestMovieSafeCall(apiKey: String) {
        latestMovieResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getLatestMovie(apiKey)
                latestMovieResponse.value = handleResponse(response)
            } catch (e: Exception) {
                latestMovieResponse.value =
                    NetworkResult.Error(message = "Movie Not Found!! " + e.message)
            }
        } else {
            latestMovieResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getMovieDetailsSafeCall(movieId: Int, apiKey: String) {
        movieDetailsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getMovieDetails(movieId, apiKey)
                movieDetailsResponse.value = handleResponse(response)
            } catch (e: Exception) {
                movieDetailsResponse.value =
                    NetworkResult.Error(message = "Movie Not Found!! " + e.message)
            }
        } else {
            movieDetailsResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private fun handleResponse(response: Response<Movie>): NetworkResult<Movie>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(message = "Timeout!!!")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(message = "Quota Exceeded!!")
            }
            response.body() == null -> {
                return NetworkResult.Error(message = "Movie not found. 2")
            }
            response.isSuccessful -> {
                val movies = response.body()
                return NetworkResult.Success(movies!!)
            }
            else -> {
                return NetworkResult.Error(message = response.message())
            }
        }
    }

    private suspend fun searchMoviesSafeCall(apiKey: String, query: String) {
        searchMovieResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.searchMovies(apiKey, query)
                searchMovieResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                searchMovieResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            searchMovieResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getNowPlayingMoviesSafeCall(apiKey: String, page: Int) {
        nowPlayingMoviesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getNowPlayingMovies(apiKey, page)
                nowPlayingMoviesResponse.value = handleUniqueResponse(response)
            } catch (e: Exception) {
                nowPlayingMoviesResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            nowPlayingMoviesResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getTopMoviesSafeCall(apiKey: String, page: Int) {
        topMoviesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getTopMovies(apiKey, page)
                topMoviesResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                topMoviesResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            topMoviesResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getPopularMoviesSafeCall(apiKey: String, page: Int) {
        popularMovieResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getPopularMovies(apiKey, page)
                popularMovieResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                popularMovieResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            popularMovieResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getTrendingMoviesSafeCall(apiKey: String, page: Int) {
        trendingMovieResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getTrendingMovies(apiKey, page)
                trendingMovieResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                trendingMovieResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            trendingMovieResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getSimilarMoviesSafeCall(movieId: Int, apiKey: String, page: Int) {
        similarMoviesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getSimilarMoviesDetails(movieId, apiKey, page)
                similarMoviesResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                similarMoviesResponse.value = NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            similarMoviesResponse.value = NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getRecommendationMoviesSafeCall(movieId: Int, apiKey: String, page: Int) {
        recommendationMoviesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response =
                    repository.remote.getRecommendationMoviesDetails(movieId, apiKey, page)
                recommendationMoviesResponse.value = handleCommonResponse(response)
            } catch (e: Exception) {
                recommendationMoviesResponse.value =
                    NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            recommendationMoviesResponse.value =
                NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getComingSoonMoviesSafeCall(apiKey: String, page: Int) {
        comingSoonResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response =
                    repository.remote.getUpcomingMovies(apiKey, page)
                comingSoonResponse.value = handleComingSoonResponse(response)
            } catch (e: Exception) {
                comingSoonResponse.value =
                    NetworkResult.Error(message = "Movies Not Found!!")
            }
        } else {
            comingSoonResponse.value =
                NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private suspend fun getMovieCreditsSafeCall(movieId: Int, apiKey: String) {
        movieCreditsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection(getApplication())) {
            try {
                val response =
                    repository.remote.getMovieCreditsDetails(movieId, apiKey)
                movieCreditsResponse.value = handleCreditsResponse(response)
            } catch (e: Exception) {
                movieCreditsResponse.value =
                    NetworkResult.Error(message = "credit Not Found!!")
            }
        } else {
            movieCreditsResponse.value =
                NetworkResult.Error(message = "No Internet Connection")
        }
    }

    private fun handleComingSoonResponse(response: Response<ComingSoonResponse>): NetworkResult<ComingSoonResponse>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(message = "Timeout!!!")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(message = "Quota Exceeded!!")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(message = "Movies not found.")
            }
            response.isSuccessful -> {
                val movies = response.body()
                return NetworkResult.Success(movies!!)
            }
            else -> {
                return NetworkResult.Error(message = response.message())
            }
        }
    }


    private fun handleCreditsResponse(response: Response<CreditsResponse>): NetworkResult<CreditsResponse>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(message = "Timeout!!!")
            }
            response.code() == 402 -> {
                NetworkResult.Error(message = "Quota Exceeded!!")
            }
            response.isSuccessful -> {
                val credits = response.body()
                NetworkResult.Success(credits!!)
            }
            else -> {
                NetworkResult.Error(message = response.message())
            }
        }
    }


    private fun handleCommonResponse(response: Response<CommonMovieResponse>): NetworkResult<CommonMovieResponse>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(message = "Timeout!!!")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(message = "Quota Exceeded!!")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(message = "Movies not found.")
            }
            response.isSuccessful -> {
                val movies = response.body()
                return NetworkResult.Success(movies!!)
            }
            else -> {
                return NetworkResult.Error(message = response.message())
            }
        }
    }

    private fun handleUniqueResponse(response: Response<UniqueMovieResponse>): NetworkResult<UniqueMovieResponse>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(message = "Timeout!!!")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(message = "Quota Exceeded!!")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(message = "Movies not found.")
            }
            response.isSuccessful -> {
                val movies = response.body()
                return NetworkResult.Success(movies!!)
            }
            else -> {
                return NetworkResult.Error(message = response.message())
            }
        }
    }

}