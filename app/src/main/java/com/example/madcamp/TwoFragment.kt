package com.example.madcamp

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.FragmentTwoBinding
import com.example.madcamp.databinding.ItemRecyclerviewBinding

class MyViewHolder(val binding: ItemRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root)
class MyAdapter(val datas: MutableList<Int>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.setImageResource(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MyDecoration(val datas: MutableList<Int>): RecyclerView.ItemDecoration() {

    // 각 항목을 꾸미기 위해 호출
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // 동일한 간격 설정
        outRect.set(0, 0, 0, 0)

        view.setBackgroundColor(Color.parseColor("#28a0ff"))
        ViewCompat.setElevation(view, 20.0f)

    }
}


class TwoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTwoBinding.inflate(inflater, container, false)

        val datas = mutableListOf<Int>()
        for(i in 1..5){
            val id = resources.getIdentifier("img$i", "drawable", requireContext().packageName)
            datas.add(id)
        }

        val gridLayoutManager = GridLayoutManager(activity, 4) // Grid size를 4로 설정했습니다.
        binding.recyclerview.layoutManager = gridLayoutManager
        binding.recyclerview.adapter = MyAdapter(datas)
        binding.recyclerview.addItemDecoration(MyDecoration(datas))

        return binding.root
    }
    }
