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
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.madcamp.databinding.FragmentOneBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyViewHolder2(val binding: ItemRecyclerview2Binding) :
    RecyclerView.ViewHolder(binding.root)
class MyAdapter2(val datas: MutableList<Any>,val fragmentBinding: FragmentTwoBinding): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        // 다이얼로그 크기 재조정
        val dialogWidth = if (bitmap.width > deviceWidth) deviceWidth else bitmap.width
        val dialogHeight = if (bitmap.height > deviceHeight) deviceHeight else bitmap.height

        // 이미지 비율 계산
        val imageRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

        if (dialogWidth < deviceWidth && dialogHeight < deviceHeight) {
            val targetRatio = deviceWidth.toFloat() / deviceHeight.toFloat()

            val layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // 이미지의 가로 비율과 세로 비율을 비교하여 이미지뷰의 크기를 조정
            if (imageRatio > targetRatio) {
                // 이미지의 가로 비율이 더 크면 이미지의 가로 크기를 deviceWidth로 설정하고, 세로 크기는 비율에 맞게 조정
                val newHeight = (deviceWidth / imageRatio).toInt()
                if (newHeight > deviceHeight) {
                    layoutParams.width = (deviceHeight * imageRatio).toInt()
                    layoutParams.height = deviceHeight
                } else {
                    layoutParams.width = deviceWidth
                    layoutParams.height = newHeight
                }
            } else {
                // 이미지의 세로 비율이 더 크거나 같으면 이미지의 세로 크기를 deviceHeight로 설정하고, 가로 크기는 비율에 맞게 조정
                val newWidth = (deviceHeight * imageRatio).toInt()
                if (newWidth > deviceWidth) {
                    layoutParams.width = deviceWidth
                    layoutParams.height = (deviceWidth / imageRatio).toInt()
                } else {
                    layoutParams.width = newWidth
                    layoutParams.height = deviceHeight
                }
            }

            // 커스텀 다이얼로그를 생성
            val dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_image)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            val dialogImageView = dialog.findViewById<ImageView>(R.id.dialog_image_view)
            dialogImageView.layoutParams = layoutParams

            // 이미지뷰의 크기 업데이트
            dialogImageView.requestLayout()

            // ImageView에 Bitmap 설정
            dialogImageView.setImageBitmap(bitmap)

            // 다이얼로그 크기 설정
            dialog.window?.setLayout(dialogWidth, dialogHeight)
            // 다이얼로그 표시
            dialog.show()
        } else {
            // 이미지의 크기가 화면과 동일하거나 이미지가 화면보다 큰 경우에는 기존 로직을 유지
            // 커스텀 다이얼로그를 생성
            val dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_image)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            val dialogImageView = dialog.findViewById<ImageView>(R.id.dialog_image_view)

            // 이미지뷰의 크기 조정
            if (dialogWidth.toFloat() / dialogHeight.toFloat() > imageRatio) {
                // 이미지의 가로 비율이 더 작을 경우
                val newHeight = (dialogWidth / imageRatio).toInt()
                dialogImageView.layoutParams.width = dialogWidth
                dialogImageView.layoutParams.height = newHeight
            } else {
                // 이미지의 세로 비율이 더 작거나 같을 경우
                val newWidth = (dialogHeight * imageRatio).toInt()
                dialogImageView.layoutParams.width = newWidth
                dialogImageView.layoutParams.height = dialogHeight
            }

            // 이미지뷰의 크기 업데이트
            dialogImageView.requestLayout()

            // ImageView에 Bitmap 설정
            dialogImageView.setImageBitmap(bitmap)

            // 다이얼로그 크기 설정
            dialog.window?.setLayout(dialogWidth, dialogHeight)
            // 다이얼로그 표시
            dialog.show()
        }
    }

    fun showImageDialog2(activity: Activity, bitmap: Bitmap) {
        fragmentBinding.photo.setImageBitmap(bitmap)
        fragmentBinding.detailInfo.visibility=View.VISIBLE
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder2).binding
        when (val item = datas[position]) {
            is Int -> binding.itemData.setImageResource(item)
            is Uri -> Glide.with(holder.itemView.context).load(item).into(binding.itemData)
            is Bitmap -> binding.itemData.setImageBitmap(item)
            else -> throw IllegalArgumentException("Unexpected data type")
        }

        holder.binding.itemData.setOnClickListener {
            when (val data = datas[position]) {
                is Int -> {
                    // Local resource image
                    val bitmap = BitmapFactory.decodeResource(it.context.resources, data)
                    showImageDialog2(it.context as Activity, bitmap)
                }
                is Uri -> {
                    // Uri image from gallery
                    val target = object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            showImageDialog2(it.context as Activity, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            //추상 메소드이기때문에 구현해놓음
                        }
                    }

                    Glide.with(it.context)
                        .asBitmap()
                        .load(data)
                        .into(target)
                }
                is Bitmap -> {
                    // Bitmap image from camera
                    showImageDialog2(it.context as Activity, data)
                }
                else -> throw IllegalArgumentException("Unexpected data type")
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

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Handle the back button event
        }
    }

    private lateinit var binding: FragmentTwoBinding
    private var datas = mutableListOf<Any>()
    private lateinit var callback: OnBackPressedCallback

    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // 선택한 이미지의 Uri를 받아서 처리하는 부분
        uri?.let {
            datas.add(it)
            (binding.recyclerview.adapter as MyAdapter2).notifyItemInserted(datas.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                Toast.makeText(context, "Back button pressed in fragment", Toast.LENGTH_SHORT).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        binding = FragmentTwoBinding.inflate(inflater, container, false)

        for(i in 1..8){
            val id = resources.getIdentifier("img$i", "drawable", requireContext().packageName)
            datas.add(id)
        }

        val gridLayoutManager = GridLayoutManager(activity, 3)
        binding.recyclerview.layoutManager = gridLayoutManager
        binding.recyclerview.adapter = MyAdapter2(datas,binding)
        binding.recyclerview.addItemDecoration(MyDecoration2(activity as Context))

        binding.floatingActionButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.floatingActionButton)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_camera -> openCamera()
                    R.id.menu_gallery -> openGallery()
                }
                true
            }
            popupMenu.show()
        }

        binding.backButton.setOnClickListener {
            binding.detailInfo.visibility=View.GONE
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.detailInfo.visibility=View.GONE
    }

    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap: Bitmap? = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                datas.add(it)
                (binding.recyclerview.adapter as MyAdapter2).notifyItemInserted(datas.size - 1)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        if (result == PackageManager.PERMISSION_GRANTED) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(takePictureIntent)
        } else {
            requestCameraPermission()
        }
    }
    private fun openGallery() {
        galleryResultLauncher.launch("image/*")
    }

    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
    }

    fun backButtonPressed(): Int {
        if (binding.detailInfo.visibility == View.VISIBLE) {
            Log.d("CHAN","visible")
            binding.detailInfo.visibility=View.GONE
            return 1
        }
        return 0
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