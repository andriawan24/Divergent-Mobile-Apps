package com.andriawan.divergent_mobile_apps.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.databinding.ItemArticlesBinding
import com.andriawan.divergent_mobile_apps.databinding.ItemBreakingNewsBinding
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.ui.articles.NewsListener
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil
import com.andriawan.divergent_mobile_apps.utils.toDp
import timber.log.Timber

class BreakingNewsAdapter(
    private val listener: NewsListener
): RecyclerView.Adapter<BreakingNewsAdapter.ViewHolder>() {

    private var articleList = emptyList<Article>()

    class ViewHolder(private val binding: ItemBreakingNewsBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article, position: Int, size: Int, listener: NewsListener) {
            binding.article = article
            binding.listener = listener

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.descriptionTextView.text =
                    Html.fromHtml(article.description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.descriptionTextView.text =
                    Html.fromHtml(article.description)
            }

            if (position == size-1) {
                binding.dividerEnd.visibility = View.VISIBLE
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemBreakingNewsBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article, position, articleList.size, listener)
    }

    override fun getItemCount(): Int = articleList.size

    fun setData(newList: List<Article>) {
        val onboardDiffUtil = RecyclerDIffUtil(articleList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        articleList = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}