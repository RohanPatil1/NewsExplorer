package com.rohan.newsexplorer.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.DiscoverNewsItemLargeBinding
import com.rohan.newsexplorer.databinding.DiscoverNewsItemSmallBinding
import com.rohan.newsexplorer.ui.adapters.click_listeners.DiscoverNewsItemOnClick
import com.rohan.newsexplorer.utils.NetworkImageUtils
import java.util.*


class DiscoverNewsAdapter : ListAdapter<NData, RecyclerView.ViewHolder>(diffUtil) {

    lateinit var discoverNewsItemOnClick: DiscoverNewsItemOnClick
    lateinit var context: Context

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<NData>() {
            override fun areItemsTheSame(
                oldItem: NData,
                newItem: NData
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: NData,
                newItem: NData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class DiscoverNewsLargeVH(private val binding: DiscoverNewsItemLargeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(nData: NData, position: Int) {

            NetworkImageUtils.loadImage(
                binding.root,
                nData.imageUrl,
                {binding.discoverItemLargePB.visibility = View.GONE},
                {binding.discoverItemLargePB.visibility = View.GONE},
                binding.discoverCatImgLargeIV
            )
            binding.discoverCatTitleLargeTV.text = nData.content


            binding.discoverCatTitleLargeTV.isSelected = true
            binding.marqTV.text = nData.title

            binding.discoverLBottomShadow.backgroundTintList = ColorStateList.valueOf(
                getRandomColor()
            )

            binding.discoverLBottomShadow.backgroundTintBlendMode = BlendMode.COLOR
            binding.root.setOnClickListener {
                discoverNewsItemOnClick.onNewsClick(nData)
            }

        }
    }

    inner class DiscoverNewsSmallVH(private val binding: DiscoverNewsItemSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(nData: NData) {
            NetworkImageUtils.loadImage(
                binding.root,
                nData.imageUrl,
                { binding.discoverItemSmallPB.visibility = View.GONE },
                { binding.discoverItemSmallPB.visibility = View.GONE },
                binding.discoverCatImgSmallIV
            )
            binding.root.setOnClickListener {
                discoverNewsItemOnClick.onNewsClick(nData)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (position % 6 == 0 || position % 6 == 4) {
            0
        } else {
            1
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == 0) {
            DiscoverNewsLargeVH(DiscoverNewsItemLargeBinding.inflate(inflater, parent, false))
        } else {
            DiscoverNewsSmallVH(DiscoverNewsItemSmallBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType: Int = getItemViewType(position)
        if (viewType == 0)
            (holder as DiscoverNewsLargeVH).bindTo(getItem(position), position)
        else
            (holder as DiscoverNewsSmallVH).bindTo(getItem(position))

    }

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}