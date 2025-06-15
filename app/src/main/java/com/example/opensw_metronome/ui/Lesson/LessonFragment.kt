package com.example.opensw_metronome.ui.Lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.opensw_metronome.R
import com.example.opensw_metronome.databinding.FragmentLessonBinding

class LessonFragment : Fragment() {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // 레슨 제목
        val list_lessons = listOf(
            "Lesson Part 1 : 일렉기타의 기본 구조",
            "Lesson Part 2 : 타브 악보 읽는 법",
            "Lesson Part 3 : 메트로놈과 BPM",
            "Lesson Part 4 : 크로매틱 연습과 모드 스케일",
            "Lesson Part 5 : 크로매틱 연습 방법 가이드"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_lessons)
        binding.lessonListView.adapter = adapter


        // 레슨 이미지
        val lesson_images = listOf(
            R.drawable.lesson_img1,
            R.drawable.lesson_img2,
            R.drawable.lesson_img3,
            R.drawable.lesson_img4,
            R.drawable.lesson_img5
        )

        binding.lessonListView.setOnItemClickListener { _, _, position, _ ->
            val bundle = Bundle().apply {
                putInt("position", position)
                putInt("totalLessons", lesson_images.size)
                putIntArray("lessonImages", lesson_images.toIntArray())
            }
            findNavController().navigate(R.id.lessonDetailFragment, bundle)
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}