package com.github.alvin0319.lavaplayer.ktu.scheduler

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

data class TrackEventCallbackHandler @JvmOverloads constructor(
    var trackStart: (AudioPlayer, AudioTrack) -> Unit = { _, _ -> },
    var trackEnd: (AudioPlayer, AudioTrack, AudioTrackEndReason) -> Unit = { _, _, _ -> },
    var trackPause: (AudioPlayer) -> Unit = { _ -> },
    var trackResume: (AudioPlayer) -> Unit = { _ -> },
    var trackStuck: (AudioPlayer, AudioTrack, Long) -> Unit = { _, _, _ -> },
    var trackException: (AudioPlayer, AudioTrack, FriendlyException) -> Unit = { _, _, _ -> }
)
