package com.rohan.newsexplorer.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.FragmentDiscoverBinding
import com.rohan.newsexplorer.ui.adapters.CategoryAdapter
import com.rohan.newsexplorer.ui.adapters.DiscoverNewsAdapter
import com.rohan.newsexplorer.ui.adapters.click_listeners.CategoryItemOnClick
import com.rohan.newsexplorer.ui.adapters.click_listeners.DiscoverNewsItemOnClick
import com.rohan.newsexplorer.ui.view_models.MainViewModel
import com.rohan.newsexplorer.ui.widgets.SpannedGridLayoutManager
import com.rohan.newsexplorer.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment(), CategoryItemOnClick, DiscoverNewsItemOnClick {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding: FragmentDiscoverBinding
        get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var discoverNewsAdapter: DiscoverNewsAdapter
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryAdapter = CategoryAdapter()
        categoryAdapter.categoryItemOnClick = this
        discoverNewsAdapter = DiscoverNewsAdapter()
        discoverNewsAdapter.discoverNewsItemOnClick = this
        categoryAdapter.context = requireContext()
        discoverNewsAdapter.context = requireContext()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Category List setup
        setUpCategoryList()

        //DiscoverNews Grid Setup
        setUpDiscoverNewsGridRV()

        //Search Setup
        handleSearch()


        //Manage UI State
        mainViewModel.discoverNewsUiState.observe(viewLifecycleOwner) {
            binding.discoverShimmer.isVisible = it is UiState.Loading
            binding.discoverNewsRV.isVisible = it !is UiState.Loading

            if (it is UiState.Success) {
                Log.d("ROHAN", "onCreate: " + it.data.newDataList.size)
                binding.discoverErrorView.visibility = View.GONE
                discoverNewsAdapter.submitList(it.data.newDataList)
                binding.discoverNewsRV.visibility = View.VISIBLE
            } else if (it is UiState.Error) {
                binding.discoverErrorView.visibility = View.VISIBLE
                binding.discoverNewsRV.visibility = View.INVISIBLE
                binding.discoverErrorViewTV.text = it.data
                Log.d("ROHAN", "onCreate: Error" + it.data)

            }
        }

        binding.discoverBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.discoverTryAgainBtn.setOnClickListener {
            mainViewModel.fetchDiscoverNews()
        }
        if (mainViewModel.discoverNewsList.isEmpty()) mainViewModel.fetchDiscoverNews()
    }

    private fun searchNews(query: String) {
        var dataList: List<NData> = mainViewModel.discoverNewsList

        dataList = dataList.filter {
            it.title.contains(query, ignoreCase = true)
        }

        if (dataList.isEmpty()) {
            Toast.makeText(requireContext(), "No data found", Toast.LENGTH_LONG).show()
        } else {
            discoverNewsAdapter.submitList(dataList)
        }

    }

    private fun handleSearch() {
        binding.discoverSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchNews(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        binding.discoverSearchView.setOnCloseListener {
            binding.discoverSearchView.setQuery("", false);
            binding.discoverSearchView.clearFocus();
            val dataList: List<NData> = mainViewModel.newsDataList
            discoverNewsAdapter.submitList(dataList)

            false //Default Behaviour
        }
    }

    private fun setUpDiscoverNewsGridRV() {
        val manager = SpannedGridLayoutManager(
            object : SpannedGridLayoutManager.GridSpanLookup {
                override fun getSpanInfo(position: Int): SpannedGridLayoutManager.SpanInfo {
                    return if (position % 6 == 0 || position % 6 == 4) {
                        SpannedGridLayoutManager.SpanInfo(2, 2)
                    } else {
                        SpannedGridLayoutManager.SpanInfo(1, 1)
                    }
                }
            },
            3,
            1f
        )
        binding.discoverNewsRV.layoutManager = manager
        binding.discoverNewsRV.adapter = discoverNewsAdapter
    }

    private fun setUpCategoryList() {
        binding.discoverCategoryRV.adapter = categoryAdapter
        binding.discoverCategoryRV.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    override fun onCategoryClick(title: String) {
        binding.discoverNewsRV.visibility = View.INVISIBLE
        mainViewModel.fetchDiscoverNews(title)
//        Toast.makeText(requireContext(), "Fetching $title...", LENGTH_SHORT).show()
    }

    override fun onNewsClick(nData: NData) {
        val action = DiscoverFragmentDirections.actionDiscoverFragmentToDetailsFragment(
            nData = nData
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}