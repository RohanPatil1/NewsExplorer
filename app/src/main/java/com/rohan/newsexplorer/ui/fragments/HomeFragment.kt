package com.rohan.newsexplorer.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.FragmentHomeBinding
import com.rohan.newsexplorer.ui.adapters.NewsAdapter
import com.rohan.newsexplorer.ui.adapters.click_listeners.NewsItemOnClick
import com.rohan.newsexplorer.ui.view_models.MainViewModel
import com.rohan.newsexplorer.utils.ConnectionLiveData
import com.rohan.newsexplorer.utils.Constants.DEFAULT_CATEGORY
import com.rohan.newsexplorer.utils.Constants.HOME
import com.rohan.newsexplorer.utils.UiState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), NewsItemOnClick {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    //private lateinit var connectionLiveData: ConnectionLiveData

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  connectionLiveData = ConnectionLiveData(requireContext())
        newsAdapter = NewsAdapter(HOME)
        newsAdapter.newsItemClickListener = this
        newsAdapter.mContext = requireContext()
        //  mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainViewModel.networkLiveData.observe(viewLifecycleOwner) {

            val hasNewsData = mainViewModel.newsDataList.isNotEmpty()
            if (!hasNewsData && it) {
                Log.d("ConnManager", "HomeFragment: Cond2")
                mainViewModel.fetchNewsData(DEFAULT_CATEGORY)
            }
        }

        binding.searchView.clearFocus()
        val navCtrl = findNavController()


        //RecyclerView Setup
        binding.newsRecyclerView.adapter = newsAdapter
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.newsRecyclerView)

        //Click Listeners
        binding.discoverTV.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDiscoverFragment()
            navCtrl.navigate(action)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchNews(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        binding.searchView.setOnCloseListener {
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            val dataList: List<NData> = mainViewModel.newsDataList
            newsAdapter.submitList(dataList)
            false
        }

        binding.downloadsTV.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDownloadedFragment()
            navCtrl.navigate(action)
        }

        binding.homeTryAgainBtn.setOnClickListener {
            mainViewModel.fetchNewsData(DEFAULT_CATEGORY)
        }


        //Manage UI State
        mainViewModel.homeNewsUiState.observe(viewLifecycleOwner) {
            binding.homeShimmer.isVisible = it is UiState.Loading

            if (it is UiState.Success) {
                Log.d("ROHAN", "onCreate: " + it.data.newDataList.size)
                binding.homeErrorView.visibility = GONE
                newsAdapter.submitList(it.data.newDataList)
            } else if (it is UiState.Error) {
                val hasCacheNews = mainViewModel.downloadedNewsList.isNotEmpty()

                //Show Cached News if available
                if (!hasCacheNews) {
                    binding.homeErrorView.visibility = VISIBLE
                    binding.homeErrorViewTV.text = it.data
                } else {
                    binding.homeErrorView.visibility = GONE
                    newsAdapter.submitList(mainViewModel.downloadedNewsList.toList())
                    newsAdapter.notifyDataSetChanged()
                }
            }
        }

        //Fetch Data
        if (mainViewModel.newsDataList.isEmpty()) mainViewModel.fetchNewsData(DEFAULT_CATEGORY)
    }


    private fun searchNews(query: String) {
        var dataList: List<NData> = mainViewModel.newsDataList
        dataList = dataList.filter {
            it.title.contains(query, ignoreCase = true)
        }

        if (dataList.isEmpty()) {
            Toast.makeText(requireContext(), "No data found", LENGTH_LONG).show()
        } else {
            newsAdapter.submitList(dataList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //News Recycler View Click Callbacks---------
    override fun onTitleDoubleTap(nData: NData) {
        mainViewModel.downloadNews(nData)
        val snackBar = Snackbar.make(binding.homeBarLL, "Article Saved...", Snackbar.LENGTH_SHORT)
            .setAction("View all") {
                val action = HomeFragmentDirections.actionHomeFragmentToDownloadedFragment()
                findNavController().navigate(action)
            }
            .withColor(android.graphics.Color.parseColor("#434343"))
            .setTextColor(android.graphics.Color.parseColor("#ffffff"))
            .setActionTextColor(android.graphics.Color.parseColor("#f5c747"))
        snackBar.show()
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }

    override fun onReadMore(readMoreUrl: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(readMoreUrl))
        startActivity(browserIntent)
    }
}