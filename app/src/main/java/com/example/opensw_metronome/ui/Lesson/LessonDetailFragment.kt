package com.example.opensw_metronome.ui.Lesson

import android.view.View
import android.content.Context

import com.google.android.material.bottomnavigation.BottomNavigationView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.opensw_metronome.MainActivity
import com.example.opensw_metronome.R
import com.example.opensw_metronome.databinding.FragmentLessonDetailBinding

class LessonDetailFragment : Fragment() {

    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)

        // 뒤로가기 버튼 숨기기
        val hideBack = arguments?.getBoolean("hideBack") == true
        if (hideBack) {
            binding.btnDetailBack.visibility = View.GONE
        }

        // 환경설정 버튼 숨기기
        (requireActivity() as MainActivity).SetControl_Class.hide()

        val bundle = requireArguments()
        val pos = bundle.getInt("position", 0)
        val totalLessons = bundle.getInt("totalLessons", 0)
        val images = bundle.getIntArray("lessonImages") ?: intArrayOf()
        val texts = bundle.getStringArray("lessonTexts") ?: arrayOf()



        if (pos in images.indices) {
            binding.ivLessonImage.setImageResource(images[pos])
        }
        if (pos in texts.indices) {
            binding.tvLessonText.text = texts[pos]
        }


        binding.btnBack.isEnabled = pos > 0
        binding.btnNext.isEnabled = pos < totalLessons - 1
        if (pos == totalLessons - 1) {
            binding.btnNext.text = "Finish"
        } else {
            binding.btnNext.text = "Next"
        }
        // 마지막 레슨인 경우 Next를 Finish 버튼으로
        if (pos == totalLessons - 1) {
            binding.btnNext.isEnabled = true
        }

        binding.btnBack.setOnClickListener {
            val newPos = pos - 1
            val newBundle = Bundle().apply {
                putInt("position", newPos)
                putInt("totalLessons", totalLessons)
                putIntArray("lessonImages", images)
                putStringArray("lessonTexts", texts)
                putBoolean("hideBack", hideBack)
            }
            findNavController().navigate(R.id.lessonDetailFragment, newBundle)
        }
        binding.btnNext.setOnClickListener {
            if (pos == totalLessons - 1) {
                // 최초 실행 플래그 설정 (Tutorial 재방문 방지)
                val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("isItFirst", false).apply()

                val navController = findNavController()
                navController.popBackStack(R.id.navigation_Lesson, false)
                val bottomNav = requireActivity()
                    .findViewById<BottomNavigationView>(R.id.nav_view)
                bottomNav.selectedItemId = R.id.navigation_Metronome
                (requireActivity() as MainActivity).NavControl_Class.show()
                (requireActivity() as MainActivity).SetControl_Class.show()
            } else {
                // 다음 레슨 detail로 이동
                val newPos = pos + 1
                val newBundle = Bundle().apply {
                    putInt("position", newPos)
                    putInt("totalLessons", totalLessons)
                    putIntArray("lessonImages", images)
                    putStringArray("lessonTexts", texts)
                    putBoolean("hideBack", hideBack)  // 플래그 유지
                }
                findNavController().navigate(R.id.lessonDetailFragment, newBundle)
            }
        }

        // 상단 뒤로가기 버튼
        binding.btnDetailBack.setOnClickListener {
            findNavController().popBackStack(R.id.navigation_Lesson, false)
            (requireActivity() as MainActivity).SetControl_Class.show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}