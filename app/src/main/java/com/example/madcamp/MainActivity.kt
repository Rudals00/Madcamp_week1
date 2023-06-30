package com.example.madcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.madcamp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // 뷰 페이저 어댑터
    class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        val fragments = listOf(OneFragment(), TwoFragment(), ThreeFragment())
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 적용
        setSupportActionBar(binding.toolbar)
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)
    }
}