package com.rohan.newsexplorer.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rohan.newsexplorer.databinding.DiscoverCategoryItemBinding
import com.rohan.newsexplorer.ui.adapters.click_listeners.CategoryItemOnClick
import com.rohan.newsexplorer.ui.view_models.Category
import com.rohan.newsexplorer.ui.view_models.CategoryItemUtils


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    lateinit var context: Context
    lateinit var categoryItemOnClick: CategoryItemOnClick


    inner class CategoryViewHolder(private val binding: DiscoverCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(category: Category) {

            binding.categoryTitleTV.setOnClickListener {
                categoryItemOnClick.onCategoryClick(category.endPoint)
            }
            val layout: View = binding.categoryCard

            layout.background = linearGradientDrawable(category)
            binding.categoryTitleTV.text = category.title
            binding.categoryIconIV.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    category.icon
                )
            )


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(
            DiscoverCategoryItemBinding.inflate(
                inflater, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindTo(CategoryItemUtils.categoryList[position])
    }

    override fun getItemCount(): Int {
        return CategoryItemUtils.categoryList.size
    }

    private fun linearGradientDrawable(category: Category): GradientDrawable {

        return GradientDrawable().apply {
            colors = if (category.title == "India") intArrayOf(
                category.color1,
                category.color1,
                category.color1,
                category.color1,
                category.color1,
                category.color1,
                Color.parseColor("#ffffff"),
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2,
                category.color2
            ) else intArrayOf(
                category.color1,
                category.color2
            )

            cornerRadius = 12f

            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
            orientation = GradientDrawable.Orientation.TL_BR

        }
    }

}