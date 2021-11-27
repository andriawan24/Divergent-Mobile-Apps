package com.andriawan.divergent_mobile_apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan.divergent_mobile_apps.databinding.ItemArticlesBinding
import com.andriawan.divergent_mobile_apps.models.articles.Article
import com.andriawan.divergent_mobile_apps.utils.RecyclerDIffUtil

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private var articleList = emptyList<Article>()

    class ViewHolder(private val binding: ItemArticlesBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.article = article
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemArticlesBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articleList.size

    fun setData(newList: List<Article>) {
        val onboardDiffUtil = RecyclerDIffUtil(articleList, newList)
        val diffUtilResult = DiffUtil.calculateDiff(onboardDiffUtil)
        articleList = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}