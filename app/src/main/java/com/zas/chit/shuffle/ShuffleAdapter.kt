package com.zas.chit.shuffle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zas.chit.shuffle.databinding.ShuffleLayoutBinding


class ShuffleAdapter(context: Context): BaseAdapter() {
    private val list: ArrayList<String>  = arrayListOf()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    internal class ViewHolder(val binding: ShuffleLayoutBinding) {
        var show = false
    }

    override fun getCount(): Int {
        Log.d("Tag", "Count: ${list.size}")
        return list.size
    }

    override fun getItem(position: Int): String {
        Log.d("Tag", "Retrieving item $position")
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        Log.d("Tag", "At getView for $position")
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.shuffle_layout, parent, false)
            holder = ViewHolder(ShuffleLayoutBinding.bind(view))
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        Log.d("Tag", "ViewTag: ${view?.tag}")

        if(holder.show) {
            holder.binding.word.text = getItem(position)
        } else {
            val str = (position + 1).toString()
            holder.binding.word.text = str
        }
        holder.binding.root.setOnClickListener {
            holder.show = !holder.show
            holder.binding.show = holder.show
            notifyDataSetChanged()
        }

        return view!!
    }

    fun updateList(shuffledList: MutableList<String>) {
        list.clear()
        list.addAll(shuffledList)
        notifyDataSetChanged()
    }
}