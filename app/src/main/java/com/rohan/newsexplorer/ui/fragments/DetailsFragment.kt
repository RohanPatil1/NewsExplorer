package com.rohan.newsexplorer.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.databinding.FragmentDetailsBinding
import com.rohan.newsexplorer.databinding.FragmentDiscoverBinding

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding!!

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nData: NData = args.nData
        bindNewsToView(nData)
        binding.detailsBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun bindNewsToView(nData: NData) {
        binding.detailsTitleTV.text = nData.title
        binding.detailsAuthorTV.text = nData.author
        binding.detailsDateTV.text = nData.date
        binding.detailsDescriptionTV.text =
            descriptionTextConfigD(nData)
        binding.detailsDescriptionTV.isClickable = true;
        binding.detailsDescriptionTV.movementMethod = LinkMovementMethod.getInstance()
        Glide.with(binding.root)
            .load(nData.imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.detailsPB.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.detailsPB.visibility = View.GONE
                    return false
                }

            })
            .into(binding.detailsImgIV)
    }

    private fun descriptionTextConfigD(nData: NData): SpannableStringBuilder {
        val description = nData.content.removeSuffix(".").removeSuffix(" ") + "...read more"
        val spannable = SpannableStringBuilder(description)
        spannable.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.WHITE // you can use custom color
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(widget: View) {
                Log.d("DetailsFragment", "onReadMoreClick: " + nData.readMoreUrl.toString())
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nData.readMoreUrl))
                startActivity(browserIntent)
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