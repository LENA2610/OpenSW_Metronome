package com.example.opensw_metronome.ui.Settings

import android.content.Context
import android.os.Build
import com.example.opensw_metronome.model.PracticeLogRepository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.opensw_metronome.databinding.FragmentSettingsBinding
import androidx.navigation.fragment.findNavController
import com.example.opensw_metronome.MainActivity

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
//    private var set_NoScreenSleep: Boolean = true
//    private var set_SoundChoose: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val logs = listOf("App RESET")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, logs)
        binding.logListView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // 뒤로가기
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
            (requireActivity() as MainActivity).SetControl_Class.show()
        }

        binding.logListView.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                // RESET 클릭 시 로그 초기화 및 앱 재시작
                val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("isItFirst", true).apply()
                PracticeLogRepository.run {
                    loadFromPrefs(requireContext())
                    saveToPrefs(requireContext())
                }
                val logPrefs = requireActivity().getSharedPreferences("log_store", Context.MODE_PRIVATE)
                logPrefs.edit().putString("logs_json", "[]").apply()
                requireActivity().finishAffinity()
            }
        }
    }

}