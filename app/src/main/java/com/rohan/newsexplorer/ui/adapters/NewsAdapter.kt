package com.rohan.newsexplorer.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.color.MaterialColors
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.DownloadsNewsItemBinding
import com.rohan.newsexplorer.databinding.NewItemLayoutBinding
import com.rohan.newsexplorer.ui.adapters.click_listeners.DoubleClickListener
import com.rohan.newsexplorer.ui.adapters.click_listeners.DownloadsItemOnClick
import com.rohan.newsexplorer.ui.adapters.click_listeners.NewsItemOnClick
import com.rohan.newsexplorer.ui.view_models.NewsItemVm
import com.rohan.newsexplorer.utils.Constants.DOWNLOADS
import com.rohan.newsexplorer.utils.Constants.HOME


class NewsAdapter(private val typeTag: Int) :
    ListAdapter<NData, RecyclerView.ViewHolder>(diffUtil) {

    lateinit var newsItemClickListener: NewsItemOnClick
    lateinit var downloadsItemClickListener: DownloadsItemOnClick
    lateinit var mContext: Context

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

        /*
        This is what I did. (If you cant access class "context" inside SharedPreferences)

  Create a context of the adapter class by Context mConext; or private WeakReference<Context> mContext;
  Instead giving mContext use this.mContext.get() wherever you need to use context inside the SharedPrefernce.
   like SharedPreferences preferences = this.mContext.get().getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
         */

    }


    inner class HomeViewHolder(private val binding: NewItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(nData: NData, position: Int) {
            val newsItemVm = NewsItemVm(nData)
            binding.itemVm = newsItemVm
            Glide.with(binding.root)
                .load(newsItemVm.img)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.newsItemPB.visibility = GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.newsItemPB.visibility = GONE
                        return false
                    }

                })
                .into(binding.newsImgIV)



            binding.titleTV.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    Log.d("NewsAdapter", "DOUBLE TAP")
                    newsItemClickListener.onTitleDoubleTap(nData)
                }

                override fun onSingleClick() {
                    Log.d("NewsAdapter", "Single TAP")
                }
            })

            //Show tip for first element only
            if (position == 0) {
                val tipViewStub: ViewStub? = binding.guidelineVS.viewStub
                val inflatedTip: View? = tipViewStub?.inflate()
                val tipCloseBtn = inflatedTip?.findViewById<ImageView>(R.id.tipCloseBtn)

                tipCloseBtn?.setOnClickListener {
                    Log.d("NewsAdapter", "ViewStub should be Gone")
                    val inflatedView: View = binding.root.findViewById(R.id.guidelineInfVS)
                    inflatedView.visibility = GONE
                }
            }

            binding.descriptionTV.text =
                descriptionTextConfigH(nData.content, newsItemVm.readMoreUrl ?: "")
            binding.descriptionTV.isClickable = true;
            binding.descriptionTV.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    inner class DownloadsViewHolder(private val binding: DownloadsNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(nData: NData) {
            val newsItemVm = NewsItemVm(nData)
            binding.itemVm = newsItemVm
            Glide.with(binding.root)
                .load(newsItemVm.img)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.downloadsItemPB.visibility = GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.downloadsItemPB.visibility = GONE
                        return false
                    }

                })
                .into(binding.downloadsImgIV)

            binding.downloadsDeleteBtn.setOnClickListener {
                downloadsItemClickListener.onDeleteClick(nData)
            }

            binding.downloadsDescripTV.text =
                descriptionTextConfigD(
                    nData.content,
                    newsItemVm.readMoreUrl!!,
                    binding.downloadsDescripTV
                )
            binding.downloadsDescripTV.isClickable = true;
            binding.downloadsDescripTV.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return typeTag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == HOME) {
            HomeViewHolder(NewItemLayoutBinding.inflate(inflater, parent, false))
        } else {
            DownloadsViewHolder(
                DownloadsNewsItemBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (typeTag == DOWNLOADS) {
            (holder as DownloadsViewHolder).bindTo(getItem(position))
        } else {
            (holder as HomeViewHolder).bindTo(getItem(position), position)
        }
    }

    fun descriptionTextConfigH(content: String, readMoreUrl: String): SpannableStringBuilder {
        val description = content.removeSuffix(".").removeSuffix(" ") + "...read more"
        val spannable = SpannableStringBuilder(description)

        spannable.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.WHITE
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                newsItemClickListener.onReadMore(readMoreUrl)
            }
        }, description.length - 9, description.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            description.length - 9,
            description.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    fun descriptionTextConfigD(
        content: String,
        readMoreUrl: String,
        downloadsDescripTV: TextView
    ): SpannableStringBuilder {
        val description = content.removeSuffix(".").removeSuffix(" ") + "...read more"
        val spannable = SpannableStringBuilder(description)
        spannable.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(widget: View) {
                downloadsItemClickListener.onReadMore(readMoreUrl)
            }
        }, description.length - 9, description.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),

            description.length - 9,
            description.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}
