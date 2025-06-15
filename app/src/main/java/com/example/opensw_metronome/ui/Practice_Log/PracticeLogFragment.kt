package com.example.opensw_metronome.ui.Practice_Log

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import java.time.format.DateTimeFormatter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.opensw_metronome.R
import com.example.opensw_metronome.databinding.FragmentPracticeLogBinding
import com.example.opensw_metronome.model.Data_PracticeLog
import com.example.opensw_metronome.model.PracticeLogRepository
import android.app.AlertDialog
import android.widget.LinearLayout
import android.widget.EditText
import android.text.InputType
import android.widget.ImageButton
import java.time.LocalDateTime
import android.widget.Spinner

class PracticeLogFragment : Fragment() {

    private var _binding: FragmentPracticeLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var logAdapter: ArrayAdapter<Data_PracticeLog>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 로그 데이터 불러오기
        PracticeLogRepository.loadFromPrefs(requireContext())

        _binding = FragmentPracticeLogBinding.inflate(inflater, container, false)
        logAdapter = object : ArrayAdapter<Data_PracticeLog>(
            requireContext(),
            R.layout.item_practice_log,
            PracticeLogRepository.logs
        ) {
            @SuppressLint("SetTextI18n")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView
                    ?: layoutInflater.inflate(R.layout.item_practice_log, parent, false)
                val entry = getItem(position)!!
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                view.findViewById<TextView>(R.id.Log1_todayDate).text =
                    entry.when_now.format(formatter)
                view.findViewById<TextView>(R.id.Log2_BPM).text =
                    "BPM: ${entry.what_BPM}"
                view.findViewById<TextView>(R.id.Log3_timerGoal).text =
                    "Goal: ${entry.time_goal} sec"
                view.findViewById<TextView>(R.id.Log4_timerRealPractice).text =
                    "Duration: ${entry.time_how} sec"
                view.findViewById<TextView>(R.id.Log5_scalePosition).text =
                    "Scale: ${entry.what_scale}"
                view.findViewById<TextView>(R.id.Log6_rating).text =
                    "Rating: ${entry.rating}/5"


                val deleteBtn = view.findViewById<ImageButton>(R.id.ibDelete)
                deleteBtn.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Log 삭제")
                        .setMessage("이 Log를 삭제하시겠습니까?")
                        .setPositiveButton("삭제") { _, _ ->
                            // 해당 위치의 항목 삭제
                            (PracticeLogRepository.logs as MutableList<Data_PracticeLog>).removeAt(
                                position
                            )
                            PracticeLogRepository.saveToPrefs(requireContext())
                            notifyDataSetChanged()
                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
                return view
            }
        }
        binding.logListView.adapter = logAdapter

        // 직접 로그 추가 버튼 클릭
        binding.fabAddLog.setOnClickListener {
            val context = requireContext()
            // 입력 폼 레이아웃 생성
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 40, 50, 10)
            }


            // 1) BPM 입력 행
            val bpmRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val tvBpmLabel = TextView(context).apply {
                    text = "BPM : "
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }
                val etBpm = EditText(context).apply {
                    hint = "숫자 입력"
                    inputType = InputType.TYPE_CLASS_NUMBER
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    tag = "etBpm"
                }
                addView(tvBpmLabel)
                addView(etBpm)
            }
            container.addView(bpmRow)

            // 2) 목표 시간 선택 행
            val goalRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val tvGoalLabel = TextView(context).apply {
                    text = "목표 시간 : "
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }
                val spinnerGoal = Spinner(context).apply {
                    adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf("30","60","120","300")
                    )
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    tag = "spinnerGoal"
                }
                addView(tvGoalLabel)
                addView(spinnerGoal)
            }
            container.addView(goalRow)

            // 3) 실제 시간 입력 행
            val actualRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val tvActualLabel = TextView(context).apply {
                    text = "실제 시간 : "
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }
                val etActual = EditText(context).apply {
                    hint = "숫자 입력"
                    inputType = InputType.TYPE_CLASS_NUMBER
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    tag = "etActual"
                }
                addView(tvActualLabel)
                addView(etActual)
            }
            container.addView(actualRow)

            // 4) 스케일 선택 행
            val scaleRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val tvScaleLabel = TextView(context).apply {
                    text = "scale : "
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }
                val spinnerScale = Spinner(context).apply {
                    adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf("Lydian MODE","Mixolydian MODE","Aeolian MODE","Locrian MODE","Ionian MODE")
                    )
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    tag = "spinnerScale"
                }
                addView(tvScaleLabel)
                addView(spinnerScale)
            }
            container.addView(scaleRow)

            // 5) 평가 선택 행
            val ratingRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                val tvRatingLabel = TextView(context).apply {
                    text = "평가 : "
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                }
                val spinnerRating = Spinner(context).apply {
                    adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf("0", "1", "2", "3", "4", "5")
                    )
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    tag = "spinnerRating"
                }
                addView(tvRatingLabel)
                addView(spinnerRating)
            }
            // 레이아웃에 추가
            container.addView(ratingRow)

                // 다이얼로그 표시
            AlertDialog.Builder(context)
                .setTitle("로그 직접 추가")
                .setView(container)
                .setPositiveButton("추가") { dialog, _ ->
                    // 입력값 파싱
                    val bpm = (container.findViewWithTag<EditText>("etBpm")).text.toString().toIntOrNull() ?: 0
                    val goal = (container.findViewWithTag<Spinner>("spinnerGoal")).selectedItem.toString().toInt()
                    val actual = (container.findViewWithTag<EditText>("etActual")).text.toString().toIntOrNull() ?: 0
                    val scale = (container.findViewWithTag<Spinner>("spinnerScale")).selectedItem.toString()
                    val rating = (container.findViewWithTag<Spinner>("spinnerRating")).selectedItem.toString().toInt()
                    // 로그 항목 생성 및 저장
                    val entry = Data_PracticeLog(
                        when_now = LocalDateTime.now(),
                        what_BPM = bpm,
                        time_goal = goal,
                        time_how = actual,
                        what_scale = scale,
                        rating = rating
                    )
                    PracticeLogRepository.add_log(entry)
                    PracticeLogRepository.saveToPrefs(context)
                    logAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("취소", null)
                .show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 데이터 추가시, 새로 고침
        logAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}