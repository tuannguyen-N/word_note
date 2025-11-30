package com.example.wordnote.data

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "Note_App"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences
    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private const val CURRENT_COUNTRY_VOICE = "current_country_voice"
    var codeVoice: String?
        get() = preferences.getString(CURRENT_COUNTRY_VOICE, "us")
        set(value) = preferences.edit {
            it.putString(CURRENT_COUNTRY_VOICE, value)
        }

    private const val START_TIME_NOTIFICATION = "start_time"
    var startTimeNotification: Int
        get() = preferences.getInt(START_TIME_NOTIFICATION, 420) //7am
        set(value) = preferences.edit {
            it.putInt(START_TIME_NOTIFICATION, value)
        }

    private const val END_TIME_NOTIFICATION = "end_time"
    var endTimeNotification: Int
        get() = preferences.getInt(END_TIME_NOTIFICATION, 1260) //9.00pm
        set(value) = preferences.edit {
            it.putInt(END_TIME_NOTIFICATION, value)
        }

    private const val KEY_MAX_WORDS = "max_words"
    var maxWords: Int
        get() = preferences.getInt(KEY_MAX_WORDS, 10)
        set(value) = preferences.edit {
            it.putInt(KEY_MAX_WORDS, value)
        }

    private const val TIME_LEVEL_1 = "time_level_1"
    var timeLevel1: Long
        get() = preferences.getLong(TIME_LEVEL_1, 30 * 60 * 1000L) // 30'
        set(value) = preferences.edit {
            it.putLong(TIME_LEVEL_1, value)
        }

    private const val TIME_LEVEL_2 = "time_level_2"
    var timeLevel2: Long
        get() = preferences.getLong(TIME_LEVEL_2, 3 * 24 * 60 * 60 * 1000L) // 3days
        set(value) = preferences.edit {
            it.putLong(TIME_LEVEL_2, value)
        }

    private const val TIME_LEVEL_3 = "time_level_3"
    var timeLevel3: Long
        get() = preferences.getLong(TIME_LEVEL_3, 7 * 24 * 60 * 60 * 1000L) // a week
        set(value) = preferences.edit {
            it.putLong(TIME_LEVEL_3, value)
        }

    private const val TIME_LEVEL_4 = "time_level_3"
    var timeLevel4: Long
        get() = preferences.getLong(TIME_LEVEL_4, 30 * 24 * 60 * 60 * 1000L) // a month
        set(value) = preferences.edit {
            it.putLong(TIME_LEVEL_4, value)
        }

    private val CAN_SHOW_SWIPE_HINT = Pair("can_show_swipe_hint", true)
    var canShowSwipeHint: Boolean
        get() = preferences.getBoolean(
            CAN_SHOW_SWIPE_HINT.first,
            CAN_SHOW_SWIPE_HINT.second
        )
        set(value) = preferences.edit {
            it.putBoolean(CAN_SHOW_SWIPE_HINT.first, value)
        }

    private val NEED_OPEN_SETTINGS_FOR_VIDEO_PERMISSION =
        Pair("need_open_settings_for_video_permission", false)
    var needOpenSettingForVideoPermission: Boolean
        get() = preferences.getBoolean(
            NEED_OPEN_SETTINGS_FOR_VIDEO_PERMISSION.first,
            NEED_OPEN_SETTINGS_FOR_VIDEO_PERMISSION.second
        )
        set(value) = preferences.edit {
            it.putBoolean(NEED_OPEN_SETTINGS_FOR_VIDEO_PERMISSION.first, value)
        }

    private val NEED_OPEN_SETTINGS_FOR_READ_AUDIO_PERMISSION =
        Pair("need_open_settings_for_audio_permission", false)
    var needOpenSettingForReadAudioPermission: Boolean
        get() = preferences.getBoolean(
            NEED_OPEN_SETTINGS_FOR_READ_AUDIO_PERMISSION.first,
            NEED_OPEN_SETTINGS_FOR_READ_AUDIO_PERMISSION.second
        )
        set(value) = preferences.edit {
            it.putBoolean(NEED_OPEN_SETTINGS_FOR_READ_AUDIO_PERMISSION.first, value)
        }
    private val NEED_OPEN_SETTINGS_FOR_RECORD_AUDIO_PERMISSION =
        Pair("need_open_settings_for_record_audio_permission", false)
    var needOpenSettingForRecordAudioPermission: Boolean
        get() = preferences.getBoolean(
            NEED_OPEN_SETTINGS_FOR_RECORD_AUDIO_PERMISSION.first,
            NEED_OPEN_SETTINGS_FOR_RECORD_AUDIO_PERMISSION.second
        )
        set(value) = preferences.edit {
            it.putBoolean(NEED_OPEN_SETTINGS_FOR_RECORD_AUDIO_PERMISSION.first, value)
        }

    private val NEED_OPEN_SETTINGS_FOR_NOTIFICATION_PERMISSION =
        Pair("need_open_settings_for_notification_permission", false)
    var needOpenSettingForNotificationPermission: Boolean
        get() = preferences.getBoolean(
            NEED_OPEN_SETTINGS_FOR_NOTIFICATION_PERMISSION.first,
            NEED_OPEN_SETTINGS_FOR_NOTIFICATION_PERMISSION.second
        )
        set(value) = preferences.edit {
            it.putBoolean(NEED_OPEN_SETTINGS_FOR_NOTIFICATION_PERMISSION.first, value)
        }

    private val CAN_POST_NOTIFICATIONS = Pair("can_post_notifications", false)
    var canPostNotifications: Boolean
        get() = preferences.getBoolean(
            CAN_POST_NOTIFICATIONS.first,
            CAN_POST_NOTIFICATIONS.second
        )
        set(value) = preferences.edit {
            it.putBoolean(CAN_POST_NOTIFICATIONS.first, value)
        }

    private val CAN_SPEAKING_VOICE_NOTIFICATION = Pair("can_speaking_voice_notification", true)
    var canSpeakingVoiceNotification: Boolean
        get() = preferences.getBoolean(
            CAN_SPEAKING_VOICE_NOTIFICATION.first,
            CAN_SPEAKING_VOICE_NOTIFICATION.second
        )
        set(value) = preferences.edit {
            it.putBoolean(CAN_SPEAKING_VOICE_NOTIFICATION.first, value)
        }
}