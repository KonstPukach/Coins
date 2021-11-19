package com.oukachkosnt.coins.ui.news

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.TabLayoutProvider
import com.oukachkosnt.coins.data.domain.NewsItemData
import com.oukachkosnt.coins.mvp.list.ListMvpFragment
import com.oukachkosnt.coins.recycler.*
import com.oukachkosnt.coins.utils.viewInBrowser

class NewsListFragment : ListMvpFragment<List<NewsItemData>,
        EndlessListAdapter<NewsItemData>,
        NewsListPresenter>(),
        NewsListView {

    override fun onResume() {
        super.onResume()
        (activity as TabLayoutProvider).getTabLayout().isVisible = false
    }

    override fun createPresenter() = NewsListPresenter(this)

    override fun createAdapter() = EndlessListAdapter(
        { R.layout.news_list_item },
        { NewsItemViewHolder(it) { _, news -> goToUrl(news.sourceUrl) } },
        null,
        this::calculateDiff
    )

    override fun initRecycler(recycler: RecyclerView) {
        recycler.addOnScrollListener(EndlessScrollListener({ presenter?.requestNextNewsPage() }))
    }

    override fun setPageLoading(isLoading: Boolean) {
        listAdapter.setLoading(isLoading)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun calculateDiff(
        oldList: List<NewsItemData>,
        newList: List<NewsItemData>,
        adapter: ListAdapter<NewsItemData>
    ) {
        when {
            oldList.isEmpty() && newList.isNotEmpty() -> {
                adapter.notifyItemRangeInserted(0, newList.size)
            }
            oldList.isNotEmpty() && oldList.size < newList.size -> {
                adapter.notifyItemRangeInserted(oldList.size, newList.size - oldList.size)
            }
            else -> adapter.notifyDataSetChanged()
        }
    }

    private fun goToUrl(url: String) {
        viewInBrowser(requireActivity(), url) { showSnack(R.string.unable_to_open_browser) }
    }

    private class NewsItemViewHolder(
        rootView: View,
        private val itemClickListener: ItemClickListener<NewsItemData>? = null
    ) : ListAdapterViewHolder<NewsItemData>(rootView) {

        private val titleView: TextView      = rootView.findViewById(R.id.news_title)
        private val sourceView: TextView     = rootView.findViewById(R.id.news_source)
        private val postedAtView: TextView   = rootView.findViewById(R.id.news_posted_at)
        private val imageView: ImageView     = rootView.findViewById(R.id.news_image)
        private val viewsCountView: TextView = rootView.findViewById(R.id.news_views_count)

        override fun bind(data: NewsItemData) {
            titleView.text      = data.title
            sourceView.text     = data.source
            postedAtView.text   = data.postedAt
            viewsCountView.text = data.viewsCount.toString()

            Glide.with(imageView)
                .load(data.imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.image_placeholder).error(R.drawable.error_placeholder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

            imageView.setOnClickListener {
                itemClickListener?.invoke(adapterPosition, data)
            }
        }
    }
}