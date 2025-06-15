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

class PracticeLogFragment : Fragment() {

    private var _binding: FragmentPracticeLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var logAdapter: ArrayAdapter<Data_PracticeLog>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
                return view
            }
        }
        binding.logListView.adapter = logAdapter

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