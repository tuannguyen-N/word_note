package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.databinding.BottomSheetEditTimeBinding
import nl.joery.timerangepicker.TimeRangePicker

class EditTimeBottomSheet(
    private val onEditTime: (TimeRangePicker.Time, TimeRangePicker.Time) -> Unit,
    private val onDoneEditTime: (Int, Int) -> Unit
) : BaseDialog<BottomSheetEditTimeBinding>(BottomSheetEditTimeBinding::inflate) {

    private val MINUTES_IN_DAY = 24 * 60
    private val minDurationMinutes = 4 * 60

    private var lastStart: Int? = null
    private var lastEnd: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomSheetAnimation)
        }
        setUpView()
        setOnClick()
        onListenTimeChange()
    }

    private fun setUpView() = with(binding.timeRangePicker) {
        startTimeMinutes = AppPreferences.startTimeNotification.toInt()
        endTimeMinutes   = AppPreferences.endTimeNotification.toInt()
        binding.tvStartAt.text = formatTime(startTime.hour, startTime.minute)
        binding.tvEndAt.text = formatTime(endTime.hour, endTime.minute)
    }

    private fun onListenTimeChange() {

        binding.timeRangePicker.setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {

            override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                val startMin = startTime.totalMinutes
                var end = binding.timeRangePicker.endTime
                val endMin = end.totalMinutes

                // Update UI start
                binding.tvStartAt.text = formatTime(startTime.hour, startTime.minute)

                // Ensure minimum duration
                val duration = durationMinutes(startMin, endMin)
                if (duration < minDurationMinutes) {
                    val corrected = (startMin + minDurationMinutes) % MINUTES_IN_DAY
                    binding.timeRangePicker.endTimeMinutes = corrected
                    end = binding.timeRangePicker.endTime
                    binding.tvEndAt.text = formatTime(end.hour, end.minute)
                }

                triggerEditCallback(startMin, end.totalMinutes)
            }

            override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                val endMin = endTime.totalMinutes
                binding.tvEndAt.text = formatTime(endTime.hour, endTime.minute)

                triggerEditCallback(
                    binding.timeRangePicker.startTime.totalMinutes,
                    endMin
                )
            }

            override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {}
        })

        // When user finishes dragging → call final callback
        binding.timeRangePicker.setOnDragChangeListener(object : TimeRangePicker.OnDragChangeListener {
            override fun onDragStart(thumb: TimeRangePicker.Thumb) = true

            override fun onDragStop(thumb: TimeRangePicker.Thumb) {
                val start = binding.timeRangePicker.startTime.totalMinutes
                val end = binding.timeRangePicker.endTime.totalMinutes
                onDoneEditTime(start, end)
            }
        })
    }

    /** Prevents callback spam */
    private fun triggerEditCallback(start: Int, end: Int) {
        if (start != lastStart || end != lastEnd) {
            lastStart = start
            lastEnd = end
            onEditTime(
                TimeRangePicker.Time(start / 60, start % 60),
                TimeRangePicker.Time(end / 60, end % 60)
            )
        }
    }

    private fun formatTime(hour: Int, minute: Int) = String.format("%02d:%02d", hour, minute)

    private fun durationMinutes(start: Int, end: Int) =
        (end - start + MINUTES_IN_DAY) % MINUTES_IN_DAY

    private fun setOnClick() {
        binding.btnClose.setOnClickListener { dismiss() }
    }
}
