package com.example.madcamp

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.FragmentTwoBinding
import com.example.madcamp.databinding.ItemRecyclerview2Binding

class MyViewHolder2(val binding: ItemRecyclerview2Binding) :
        RecyclerView.ViewHolder(binding.root)
class MyAdapter2(val datas: MutableList<Int>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder2(ItemRecyclerview2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder2).binding
        binding.itemData.setImageResource(datas[position])

        holder.binding.itemData.setOnClickListener {
            // 확대된 이미지를 보여주는 AlertDialog 생성
            val builder = AlertDialog.Builder(it.context)
            val inflater = LayoutInflater.from(it.context)
            val view = inflater.inflate(R.layout.dialog_image, null)

            // dialog_image.xml에서 ImageView 찾기
            val dialogImageView = view.findViewById<ImageView>(R.id.dialog_image_view)

            // 클릭된 이미지를 원본 크기로 로드
            val bitmap = BitmapFactory.decodeResource(it.context.resources, datas[position])
            dialogImageView.setImageBitmap(bitmap)

            // 이미지의 크기에 따라 ImageView의 크기를 설정
            val layoutParams = dialogImageView.layoutParams
            layoutParams.width = bitmap.width
            layoutParams.height = bitmap.height
            dialogImageView.layoutParams = layoutParams

            builder.setView(view)
            builder.create().show()
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MyDecoration2(val datas: MutableList<Int>): RecyclerView.ItemDecoration() {

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
        for(i in 1..7){
            val id = resources.getIdentifier("img$i", "drawable", requireContext().packageName)
            datas.add(id)
        }

        val gridLayoutManager = GridLayoutManager(activity, 3) // Grid size를 3로 설정했습니다.
        binding.recyclerview.layoutManager = gridLayoutManager
        binding.recyclerview.adapter = MyAdapter2(datas)
        binding.recyclerview.addItemDecoration(MyDecoration2(datas))

        return binding.root
    }
    }
