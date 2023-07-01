package com.example.madcamp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
            // 클릭된 이미지를 원본 크기로 로드
            val bitmap = BitmapFactory.decodeResource(it.context.resources, datas[position])

            // 화면의 크기를 가져오기
            val displayMetrics = DisplayMetrics()
            (it.context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val deviceWidth = displayMetrics.widthPixels
            val deviceHeight = displayMetrics.heightPixels

            // 이미지의 크기와 화면 크기를 비교하여 AlertDialog의 크기를 결정
            val dialogWidth = if(bitmap.width > deviceWidth) deviceWidth else bitmap.width
            val dialogHeight = if(bitmap.height > deviceHeight) deviceHeight else bitmap.height

            // 커스텀 다이얼로그를 생성
            val dialog = Dialog(it.context).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_image)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            // dialog_image.xml에서 ImageView 찾기
            val dialogImageView = dialog.findViewById<ImageView>(R.id.dialog_image_view)

            // ImageView에 Bitmap 설정
            dialogImageView.setImageBitmap(bitmap)

            // 다이얼로그 크기 설정
            dialog.window?.setLayout(dialogWidth, dialogHeight)

            // 다이얼로그 표시
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class TwoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTwoBinding.inflate(inflater, container, false)

        val datas = mutableListOf<Int>()
        for(i in 1..8){
            val id = resources.getIdentifier("img$i", "drawable", requireContext().packageName)
            datas.add(id)
        }

        val gridLayoutManager = GridLayoutManager(activity, 3) // Grid size를 3로 설정했습니다.
        binding.recyclerview.layoutManager = gridLayoutManager
        binding.recyclerview.adapter = MyAdapter2(datas)

        return binding.root
    }
}
