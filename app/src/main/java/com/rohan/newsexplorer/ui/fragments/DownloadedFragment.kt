package com.rohan.newsexplorer.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.FragmentDownloadedBinding
import com.rohan.newsexplorer.ui.adapters.click_listeners.DownloadsItemOnClick
import com.rohan.newsexplorer.ui.adapters.NewsAdapter
import com.rohan.newsexplorer.ui.view_models.MainViewModel
import com.rohan.newsexplorer.utils.Constants.DOWNLOADS
import com.rohan.newsexplorer.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadedFragment : Fragment(), DownloadsItemOnClick {

    private var _binding: FragmentDownloadedBinding? = null
    private val binding: FragmentDownloadedBinding
        get() = _binding!!


    private lateinit var newsAdapter: NewsAdapter
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsAdapter = NewsAdapter(DOWNLOADS)
        newsAdapter.downloadsItemClickListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //RecyclerView Setup
        binding.downloadsRV.adapter = newsAdapter

        //Back Button
        binding.downloadsBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        //Manage UI State
        mainViewModel.downloadsNewsUiState.observe(viewLifecycleOwner) {
            binding.downloadsProgressBar.isVisible = it is UiState.Loading
            if (it is UiState.Success) {
                binding.downloadsErrorView.visibility = GONE
                newsAdapter.submitList(it.data.newDataList)
                newsAdapter.notifyDataSetChanged()
            } else if (it is UiState.Error) {
                binding.downloadsErrorView.visibility = VISIBLE
                binding.downloadsErrorTV.text = it.data
            }
        }
        mainViewModel.getDownloadedNews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteClick(nData: NData) {
        mainViewModel.deleteDownloadedNews(nData)
        Toast.makeText(requireContext(), "Article removed!", LENGTH_SHORT).show()
    }

    override fun onReadMore(readMoreUrl: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(readMoreUrl))
        startActivity(browserIntent)
    }

}