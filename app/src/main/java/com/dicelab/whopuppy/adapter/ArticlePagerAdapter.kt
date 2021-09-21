package com.dicelab.whopuppy.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.fragment.ArticleFragment
import com.dicelab.whopuppy.fragment.ArticleFragment.Companion.AREA
import com.dicelab.whopuppy.fragment.ArticleFragment.Companion.TITLE
import com.dicelab.whopuppy.fragment.RefreshFragment
import com.dicelab.whopuppy.viewmodel.ArticleSearchQuery

class ArticlePagerAdapter(
    private val fragmentManager: FragmentManager,
    lifeCycle: Lifecycle,
    private val boards: Boards
) : FragmentStateAdapter(fragmentManager, lifeCycle) {
    override fun getItemCount(): Int = boards.size
    private var updateFragment: HashMap<Int, ArticleSearchQuery> = hashMapOf()

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        (fragmentManager.findFragmentByTag("f$position") as? RefreshFragment)?.let { fragment ->
            updateFragment[position]?.let {
                updateFragment.remove(position)
                fragment.refresh(bundleOf(AREA to it.area, TITLE to it.title))
            }
        }
    }

    fun updateFragment(articleSearchQuery: ArticleSearchQuery) {
        for (i in 0 until boards.size) {
            updateFragment[i] = articleSearchQuery
            notifyItemChanged(i)
        }
    }

    override fun createFragment(position: Int): Fragment {
        val boardId = boards[position].id
        val boardTitle = boards[position].board
        return ArticleFragment.newInstance(boardId, boardTitle)
    }
}
