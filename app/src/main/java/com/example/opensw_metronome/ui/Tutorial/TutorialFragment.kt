package com.example.opensw_metronome.ui.Tutorial
import com.example.opensw_metronome.MainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.opensw_metronome.R
import com.example.opensw_metronome.databinding.FragmentTutorialBinding
import android.content.Context

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.buttonYes.setOnClickListener {
            // 첫 번째 레슨 detail 화면으로 이동
            val lessonImages = intArrayOf(
                R.drawable.lesson_img1,
                R.drawable.lesson_img2,
                R.drawable.lesson_img3,
                R.drawable.lesson_img4,
                R.drawable.lesson_img5
            )
            val lessonTexts = arrayOf(
                "Lesson Part 1 : 일렉기타의 기본 구조",
                "Lesson Part 2 : 타브 악보 읽는 법",
                "Lesson Part 3 : 메트로놈과 BPM",
                "Lesson Part 4 : 크로매틱 연습과 모드 스케일",
                "Lesson Part 5 : 크로매틱 연습 방법 가이드"
            )
            val bundle = Bundle().apply {
                putInt("position", 0)
                putInt("totalLessons", lessonImages.size)
                putIntArray("lessonImages", lessonImages)
                putBoolean("hideBack", true)  // 뒤로가기 버튼 숨김 플래그
            }
            findNavController().navigate(R.id.lessonDetailFragment, bundle)
        }
        binding.buttonNo.setOnClickListener {
            prefs.edit().putBoolean("isItFirst", false).apply()
            findNavController().navigate(R.id.navigation_Metronome)
            (requireActivity() as MainActivity).NavControl_Class.show()
            (requireActivity() as MainActivity).SetControl_Class.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}