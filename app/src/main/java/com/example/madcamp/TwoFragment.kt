package com.example.madcamp

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.FragmentTwoBinding
import com.example.madcamp.databinding.ItemRecyclerview2Binding
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MyViewHolder2(val binding: ItemRecyclerview2Binding) :
        RecyclerView.ViewHolder(binding.root)
class MyAdapter2(val datas: MutableList<Any>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder2(ItemRecyclerview2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }
    fun showImageDialog(activity: Activity, bitmap: Bitmap) {
        // 화면의 크기를 가져오기
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels

        // 이미지의 크기와 화면 크기를 비교하여 AlertDialog의 크기를 결정
        val dialogWidth = if(bitmap.width > deviceWidth) deviceWidth else bitmap.width
        val dialogHeight = if(bitmap.height > deviceHeight) deviceHeight else bitmap.height

        // 커스텀 다이얼로그를 생성
        val dialog = Dialog(activity).apply {
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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder2).binding
        when (val item = datas[position]) {
            is Int -> binding.itemData.setImageResource(item)
            is Uri -> Glide.with(holder.itemView.context).load(item).into(binding.itemData)
            else -> throw IllegalArgumentException("Unexpected data type")
        }

        holder.binding.itemData.setOnClickListener {
            if (datas[position] is Int) {
                // Local resource image
                val bitmap = BitmapFactory.decodeResource(it.context.resources, datas[position] as Int)
                showImageDialog(it.context as Activity, bitmap)
            } else if (datas[position] is Uri) {
                // Uri image from gallery
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        showImageDialog(it.context as Activity, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //추상 메소드이기때문에 구현해놓음
                    }
                }

                Glide.with(it.context)
                    .asBitmap()
                    .load(datas[position])
                    .into(target)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class TwoFragment : Fragment() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 101
    }

    private lateinit var binding: FragmentTwoBinding
    private var datas = mutableListOf<Any>()

    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // 선택한 이미지의 Uri를 받아서 처리하는 부분
        uri?.let {
            datas.add(it)
            (binding.recyclerview.adapter as MyAdapter2).notifyItemInserted(datas.size - 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTwoBinding.inflate(inflater, container, false)

        for(i in 1..8){
            val id = resources.getIdentifier("img$i", "drawable", requireContext().packageName)
            datas.add(id)
        }

        val gridLayoutManager = GridLayoutManager(activity, 3)
        binding.recyclerview.layoutManager = gridLayoutManager
        binding.recyclerview.adapter = MyAdapter2(datas)
        binding.recyclerview.addItemDecoration(MyDecoration2(activity as Context))

        binding.floatingActionButton.setOnClickListener {
            // Permission check and request
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
            } else {
                openGallery()
            }
        }

        return binding.root
    }

    private fun openGallery() {
        galleryResultLauncher.launch("image/*")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, open gallery
                    openGallery()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }
}

class MyDecoration2(val context: Context): RecyclerView.ItemDecoration() {
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

        view.setBackgroundColor(Color.parseColor("#ffffff"))
        ViewCompat.setElevation(view, 20.0f)
    }
}