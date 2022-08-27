package com.rohan.newsexplorer.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.model.NewsData
import com.rohan.newsexplorer.data.repository.NewsRepository
import com.rohan.newsexplorer.utils.ConnectionLiveData
import com.rohan.newsexplorer.utils.Constants.DEFAULT_DISCOVER_CATEGORY
import com.rohan.newsexplorer.utils.DataResult
import com.rohan.newsexplorer.utils.IoDispatcher
import com.rohan.newsexplorer.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    connectionLiveData: ConnectionLiveData,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    //Monitor Network Changes
    val networkLiveData: LiveData<Boolean> = connectionLiveData

    //HomeFragment UiState
    private val _homeNewsUiState = MutableLiveData<UiState<NewsData>>()
    val homeNewsUiState: LiveData<UiState<NewsData>> = _homeNewsUiState
    var newsDataList: List<NData> = listOf()

    //DownloadFragment UiState
    private val _downloadsNewsUiState = MutableLiveData<UiState<NewsData>>()
    var downloadedNewsList: MutableList<NData> = mutableListOf()
    val downloadsNewsUiState: LiveData<UiState<NewsData>> = _downloadsNewsUiState

    //DiscoverFragment UiState
    private val _discoverNewsUiState = MutableLiveData<UiState<NewsData>>()
    val discoverNewsUiState: LiveData<UiState<NewsData>> = _discoverNewsUiState
    var discoverNewsList: List<NData> = listOf()

    init {
        getDownloadedNews()
    }

    //Fetch News For Feed in HomeFragment
    fun fetchNewsData(category: String) {
        _homeNewsUiState.value = UiState.Loading
        viewModelScope.launch(ioDispatcher) {
            when (val apiResult = newsRepository.fetchNewsData(category)) {
                is DataResult.Error -> {
                    _homeNewsUiState.postValue(UiState.Error(apiResult.exception.message.toString()))
                }
                is DataResult.Success -> {
                    newsDataList = apiResult.data.newDataList
                    _homeNewsUiState.postValue(UiState.Success(data = apiResult.data))
                }
            }
        }
    }

    //Fetch Locally Saved News
    fun getDownloadedNews() {
        _downloadsNewsUiState.value = UiState.Loading
        viewModelScope.launch(ioDispatcher) {
            when (val d = newsRepository.fetchDownloadedNews()) {
                is DataResult.Error -> {
                    _downloadsNewsUiState.postValue(UiState.Error(d.exception.message.toString()))
                }
                is DataResult.Success -> {
                    Log.d("MainViewModel", "Downloaded List: " + d.data.newDataList.size)
                    downloadedNewsList = d.data.newDataList.toMutableList()
                    _downloadsNewsUiState.postValue(UiState.Success(d.data))
                }
            }
        }
    }

    //Save news in local db
    fun saveNewsData(newsData: NData) {
        downloadedNewsList.add(newsData)
        _downloadsNewsUiState.postValue(
            UiState.Success(
                NewsData(
                    category = "", success = true,
                    newDataList = downloadedNewsList
                )
            )
        )
        viewModelScope.launch(ioDispatcher) {
            newsRepository.saveNewsToDb(newsData)
        }
    }

    //Remove news from local db
    fun deleteDownloadedNews(newsData: NData) {
        downloadedNewsList.remove(newsData)
        _downloadsNewsUiState.postValue(
            UiState.Success(
                NewsData(
                    category = "", success = true,
                    newDataList = downloadedNewsList
                )
            )
        )
        newsRepository.deleteNews(newsData)
    }

    //Fetch news for Discover fragment based on category choice
    fun fetchDiscoverNews(category: String = DEFAULT_DISCOVER_CATEGORY) {
        _discoverNewsUiState.value = UiState.Loading
        viewModelScope.launch(ioDispatcher) {
            when (val apiResult = newsRepository.fetchDiscoverNewsData(category)) {
                is DataResult.Error -> {
                    _discoverNewsUiState.postValue(UiState.Error(apiResult.exception.message.toString()))
                }
                is DataResult.Success -> {
                    discoverNewsList = apiResult.data.newDataList
                    _discoverNewsUiState.postValue(UiState.Success(data = apiResult.data))
                }
            }
        }
    }

}