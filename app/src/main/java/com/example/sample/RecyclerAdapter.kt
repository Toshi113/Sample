package com.example.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val context: Context, private val itemClickListener: RecyclerViewHolder.ItemClickListener, private val itemList:List<MemoStructure>) : RecyclerView.Adapter<RecyclerViewHolder>()  {

    private var mRecyclerView : RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.let {
            it.idContainer.setTag(itemList[position].id)
            it.titleTextView.text = itemList[position].title
            it.detailTextView.text = itemList[position].detail
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.list_item, parent, false)

        // ここでRecyclerViewの各項目にView.setOnClickListenerでonItemClickをクリックされたときに実行しろと指定している。
        // ラムダ式が絡むから文法はよう知らん。
        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }
        return RecyclerViewHolder(mView)
    }
}