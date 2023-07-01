package com.example.madcamp

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.ActivityMainBinding
import com.example.madcamp.databinding.FragmentOneBinding
import com.example.madcamp.databinding.ItemRecyclerview1Binding
import java.util.Random

class MyViewHolder1(val binding: ItemRecyclerview1Binding): RecyclerView.ViewHolder(binding.root)

class Person(val name:String, val phone_number:String,val email:String) {
    var profile_num:Int = 0
    init {
        val random = Random()
        profile_num = random.nextInt(5)
    }
}

class MyAdapter1(val datas: MutableList<Person>,val fragmentBinding: FragmentOneBinding): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder1(ItemRecyclerview1Binding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    fun change_detail(position: Int) {
        Log.d("chan","clickeditem: ${position}")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder1).binding
        binding.itemData.text = datas[position].name
        binding.profileData.text = datas[position].name.substring(0,1)

        val num = datas[position].profile_num
        when(num) {
            0 ->binding.profileData.setBackgroundResource(R.drawable.round_button_1)
            1 ->binding.profileData.setBackgroundResource(R.drawable.round_button_2)
            2 ->binding.profileData.setBackgroundResource(R.drawable.round_button_3)
            3 ->binding.profileData.setBackgroundResource(R.drawable.round_button_4)
            else ->binding.profileData.setBackgroundResource(R.drawable.round_button_5)
        }

        holder.binding.itemData.setOnClickListener {
            change_detail(position)
            fragmentBinding.detailInfo.visibility=View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MyDecoration(val context: Context): RecyclerView.ItemDecoration() {
    // 모든 항목이 출력된 후 호출
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        // 리사이클러 뷰의 크기 계산
        val width = parent.width
        val height = parent.height
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // 각 항목의 위치 획득
        val index = parent.getChildAdapterPosition(view) + 1

//        if(index % 3 == 0){
//            outRect.set(10, 10, 10, 60)
//        }else {
//            outRect.set(10, 10, 10, 0)
//        }
        outRect.set(10, 10, 10, 0)

        view.setBackgroundColor(Color.parseColor("#28a0ff"))
        ViewCompat.setElevation(view, 20.0f)
    }
}

class OneFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOneBinding.inflate(inflater, container, false)

        val datas = mutableListOf<Person>()
        for(i in 1..9){
            datas.add(Person("item $i","010-0000-0000","dd@gmail.com"))
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = MyAdapter1(datas, binding)
        binding.recyclerview.addItemDecoration(MyDecoration(activity as Context))

        binding.backButton.setOnClickListener {
            binding.detailInfo.visibility=View.GONE
        }

        return binding.root
    }

}