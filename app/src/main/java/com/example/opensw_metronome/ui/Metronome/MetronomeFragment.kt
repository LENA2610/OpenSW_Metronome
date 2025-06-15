package com.example.opensw_metronome.ui.Metronome

import android.content.Context
import android.media.AudioManager

import com.example.opensw_metronome.MainActivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.opensw_metronome.databinding.FragmentMetronomeBinding
import android.os.Handler
import android.os.Looper
import com.example.opensw_metronome.R
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.opensw_metronome.model.Data_PracticeLog
import com.example.opensw_metronome.model.PracticeLogRepository
import java.time.LocalDateTime
import java.time.Duration
import android.widget.SeekBar

class MetronomeFragment : Fragment() {

    private var soundBeat1Id: Int = 0
    private var soundBeatOtherId: Int = 0
    private var block_sound_glitch = false

    private var _binding: FragmentMetronomeBinding? = null
    private val binding get() = _binding!!
    private var BPM: Int = 60
    private var IsPlaying: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var BeatIndex = 0
    private lateinit var soundPool: SoundPool
    private var scale_index = 0
    private var scale_Name = listOf("Lydian MODE", "Mixolydian MODE", "Aeolian MODE", "Locrian MODE", "Ionian MODE")


    private val scale_Images = listOf(
        R.drawable.scale_15,
        R.drawable.scale_37,
        R.drawable.scale_59,
        R.drawable.scale_711,
        R.drawable.scale_812
    )


    // 로그 기록
    private var Metronome_StartTime: LocalDateTime? = null
    private var Metronome_RemainTime: Long = 1 * 60 * 1000L
    private var Metronome_GoalTime: Long = Metronome_RemainTime
    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n", "DefaultLocale")
        override fun run() {

            // 타이머 완료 시 자동으로 일시정지 + 로그 기록
            if (Metronome_RemainTime <= 0L) {
                binding.tvTimer.text = "00:00"
                stopMetronome()
                return
            }
            val minutes = Metronome_RemainTime / 1000 / 60
            val seconds = (Metronome_RemainTime / 1000) % 60
            binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)

            // 1초 감소
            Metronome_RemainTime -= 1000
            timerHandler.postDelayed(this, 1000)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        binding.scaleImage.setImageResource(scale_Images[scale_index])
        binding.tvScaleName.text = scale_Name[scale_index]
        binding.tvScaleName.visibility = View.GONE

        val initMinutes = Metronome_RemainTime / 1000 / 60
        val initSeconds = (Metronome_RemainTime / 1000) % 60
        binding.tvTimer.text = String.format("%02d:%02d", initMinutes, initSeconds)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundBeat1Id = soundPool.load(requireContext(), R.raw.metronome_sound_1, 1)
        soundBeatOtherId = soundPool.load(requireContext(), R.raw.metronome_sound_2, 1)


        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && !block_sound_glitch) {
                soundPool.play(soundBeat1Id, 0f, 0f, 0, 0, 1f)
                soundPool.play(soundBeatOtherId, 0f, 0f, 0, 0, 1f)
                block_sound_glitch = true
            }
        }


        // 메트로놈 표기 (원 4개)
        // BPM 컨트롤러
        binding.NowBPM.text = "$BPM BPM"
        // +,- 버튼
        binding.BPMPlus.setOnClickListener {
            if (BPM < 300) BPM++
            binding.NowBPM.text = "$BPM BPM"
            binding.bpmSeekBar.progress = BPM - 30
        }
        binding.BPMMinus.setOnClickListener {
            if (BPM > 30) BPM--
            binding.NowBPM.text = "$BPM BPM"
            binding.bpmSeekBar.progress = BPM - 30
        }
        // 슬라이더 바
        binding.bpmSeekBar.max = 270  // 300 - 30
        binding.bpmSeekBar.progress = BPM - 30
        binding.bpmSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                BPM = progress + 30   // offset so leftmost = 30 BPM
                binding.NowBPM.text = "$BPM BPM"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        // 자율|크로매틱 모드 전환 시 스케일, 가릴 요소들 visibility 전환
        binding.scaleImage.visibility = View.GONE
        binding.tvTimer.visibility = View.GONE
        binding.timerControls.visibility = View.GONE
        binding.btnPrevScale.visibility = View.GONE
        binding.btnNextScale.visibility = View.GONE
        binding.tvScaleName.visibility = View.GONE
        binding.WhatMode.setOnCheckedChangeListener { _, checkedId ->
            val params = binding.MetronomeContainerFrame.layoutParams as ConstraintLayout.LayoutParams
            if (checkedId == R.id.Mode_chromatic) {
                scale_index = 0
                binding.scaleImage.setImageResource(scale_Images[scale_index])
                binding.scaleImage.visibility = View.VISIBLE
                binding.tvTimer.visibility = View.VISIBLE
                binding.timerControls.visibility = View.VISIBLE
                binding.btnPrevScale.visibility = View.VISIBLE
                binding.btnNextScale.visibility = View.VISIBLE
                // 크로매틱 모드일 때 스케일 이동 화살표 활성화
                binding.btnPrevScale.isEnabled = true
                binding.btnNextScale.isEnabled = true
                binding.tvScaleName.visibility = View.VISIBLE
                binding.tvScaleName.text = scale_Name[scale_index]
            } else {
                binding.scaleImage.visibility = View.GONE
                binding.tvTimer.visibility = View.GONE
                binding.timerControls.visibility = View.GONE
                binding.btnPrevScale.visibility = View.GONE
                binding.btnNextScale.visibility = View.GONE
                // 자율 모드일 때 스케일 이동 화살표 비활성화
                binding.btnPrevScale.isEnabled = false
                binding.btnNextScale.isEnabled = false
                binding.tvScaleName.visibility = View.GONE
                timerHandler.removeCallbacks(timerRunnable)
                Metronome_RemainTime = 5 * 60 * 1000L
                binding.tvTimer.text = "00:00"
            }
            binding.MetronomeContainerFrame.layoutParams = params
        }

        // 재생 | 일시정지 토글
        binding.PlayBTN.setOnClickListener {
            if (IsPlaying) {
                stopMetronome()
            } else {
                startMetronome()
            }
        }


        binding.btnTimerAdjust.setOnClickListener {
            val options = arrayOf("30초", "1분", "2분", "5분")
            AlertDialog.Builder(requireContext())
                .setTitle("타이머 설정")
                .setItems(options) { _, which ->
                    Metronome_RemainTime = when (which) {
                        0 -> 30 * 1000L
                        1 -> 1 * 60 * 1000L
                        2 -> 2 * 60 * 1000L
                        3 -> 5 * 60 * 1000L
                        else -> Metronome_RemainTime
                    }
                    Metronome_GoalTime = Metronome_RemainTime   // 목표값 동기화
                    val minutes = (Metronome_RemainTime / 1000) / 60
                    val seconds = (Metronome_RemainTime / 1000) % 60
                    binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                }
                .show()
        }

        binding.btnPrevScale.setOnClickListener {
            scale_index = (scale_index - 1 + scale_Images.size) % scale_Images.size
            binding.scaleImage.setImageResource(scale_Images[scale_index])
            binding.tvScaleName.text = scale_Name[scale_index]
        }
        binding.btnNextScale.setOnClickListener {
            scale_index = (scale_index + 1) % scale_Images.size
            binding.scaleImage.setImageResource(scale_Images[scale_index])
            binding.tvScaleName.text = scale_Name[scale_index]
        }

        return binding.root
    }


    // 실제 메트로놈 본체 기능_2개
    // 1. 메트로놈 시작
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun startMetronome() {
        Metronome_StartTime = LocalDateTime.now()      // 로그용 시작 시각
        IsPlaying = true
        // 메트로놈 소리 증폭
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)
        binding.BPMPlus.isEnabled = false
        binding.BPMMinus.isEnabled = false
        binding.bpmSeekBar.isEnabled = false
        binding.WhatMode.isEnabled = false
        binding.ModeChromatic.isEnabled = false
        binding.ModeFree.isEnabled = false
        binding.btnTimerAdjust.isEnabled = false
        binding.btnPrevScale.isEnabled = false
        binding.btnNextScale.isEnabled = false
        binding.btnPrevScale.visibility = View.GONE
        binding.btnNextScale.visibility = View.GONE
        (requireActivity() as MainActivity).NavControl_Class.hide()
        (requireActivity() as MainActivity).SetControl_Class.hide()
        BeatIndex = 0
        scheduleNextBeat()

        // 크로매틱일 때만, 타이머 작동
        if (binding.WhatMode.checkedRadioButtonId == R.id.Mode_chromatic) {
            timerHandler.post(timerRunnable)
        }
        binding.PlayBTN.text = "Pause"
    }
    private fun scheduleNextBeat() {
        if (!IsPlaying) return
        flashCircle(BeatIndex)
        BeatIndex = (BeatIndex + 1) % 4
        handler.postDelayed({scheduleNextBeat()}, 60000L / BPM)
    }
    private fun flashCircle(index: Int) {
        val circles = listOf(
            binding.MetronomeCircle1, binding.MetronomeCircle2, binding.MetronomeCircle3, binding.MetronomeCircle4
        )
        circles.forEach { it.setBackgroundResource(R.drawable.metronome_circle_off) }
        circles[index].setBackgroundResource(R.drawable.metronome_circle_on)
        if (index == 0) {
            soundPool.play(soundBeat1Id, 1f, 1f, 0, 0, 1f)
        } else {
            soundPool.play(soundBeatOtherId, 1f, 1f, 0, 0, 1f)
        }
    }


    // 2. 메트로놈 정지
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun stopMetronome() {
        timerHandler.removeCallbacks(timerRunnable) // 타이머 일시정지
        Metronome_RemainTime = Metronome_GoalTime   // 타이머 값을 목표값으로 초기화
        val minutes = (Metronome_RemainTime / 1000) / 60
        val seconds = (Metronome_RemainTime / 1000) % 60
        binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
        binding.PlayBTN.text = "Play"
        handler.removeCallbacksAndMessages(null)
        listOf(
            binding.MetronomeCircle1, binding.MetronomeCircle2,
            binding.MetronomeCircle3, binding.MetronomeCircle4
        ).forEach { it.setBackgroundResource(R.drawable.metronome_circle_off) }


        // 자동 로그 기록
        if (binding.WhatMode.checkedRadioButtonId == R.id.Mode_chromatic) {
            Metronome_StartTime?.let { startTs ->
                val now = LocalDateTime.now()
                val actualMillis = Duration.between(startTs, now).toMillis()
                val elapsedSec = (actualMillis / 1000).toInt()
                if (elapsedSec >= 5) {
                    val ratings = arrayOf("1", "2", "3", "4", "5")
                    var selectedRating = 4
                    AlertDialog.Builder(requireContext())
                        .setTitle("연습 평가")
                        .setSingleChoiceItems(ratings, selectedRating) { _, which ->
                            selectedRating = which
                        }
                        .setPositiveButton("확인") { dialog, _ ->
                            val entry = Data_PracticeLog(
                                when_now   = startTs,
                                what_BPM   = BPM,
                                time_goal  = (Metronome_GoalTime / 1000).toInt(),
                                time_how   = elapsedSec,
                                what_scale = scale_Name[scale_index],
                                rating     = selectedRating + 1  // 배열 인덱스→점수
                            )
                            PracticeLogRepository.add_log(entry)
                            PracticeLogRepository.saveToPrefs(requireContext())
                            dialog.dismiss()
                        }
                        // 취소 시 0점
                        .setNegativeButton("취소") { dialog, _ ->
                            val cancelEntry = Data_PracticeLog(
                                when_now   = startTs,
                                what_BPM   = BPM,
                                time_goal  = (Metronome_GoalTime / 1000).toInt(),
                                time_how   = elapsedSec,
                                what_scale = scale_Name[scale_index],
                                rating     = 0
                            )
                            PracticeLogRepository.add_log(cancelEntry)
                            PracticeLogRepository.saveToPrefs(requireContext())
                            dialog.dismiss()
                        }
                        .show()
                }
                Metronome_StartTime = null
            }
        }
        binding.BPMPlus.isEnabled = true
        binding.BPMMinus.isEnabled = true
        binding.bpmSeekBar.isEnabled = true
        binding.WhatMode.isEnabled = true
        binding.ModeChromatic.isEnabled = true
        binding.ModeFree.isEnabled = true
        binding.btnTimerAdjust.isEnabled = true
        if (binding.WhatMode.checkedRadioButtonId == R.id.Mode_chromatic) {
            binding.btnPrevScale.isEnabled = true
            binding.btnNextScale.isEnabled = true
            binding.btnPrevScale.visibility = View.VISIBLE
            binding.btnNextScale.visibility = View.VISIBLE
        } else {
            binding.btnPrevScale.visibility = View.GONE
            binding.btnNextScale.visibility = View.GONE
        }
        (requireActivity() as MainActivity).NavControl_Class.show()
        (requireActivity() as MainActivity).SetControl_Class.show()
        IsPlaying = false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}