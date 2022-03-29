package com.github.alvin0319.lavaplayer.ktu.scheduler

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

data class TrackEventCallbackHandler @JvmOverloads constructor(
    var trackStart: (AudioPlayer, AudioTrack) -> Unit = { _, _ -> }, // Called when a track starts playing
    var trackEnd: (AudioPlayer, AudioTrack, AudioTrackEndReason) -> Unit = { _, _, _ -> }, // Called when a whole track in queue ends
    var trackPause: (AudioPlayer) -> Unit = { _ -> }, // Called when a track is paused
    var trackResume: (AudioPlayer) -> Unit = { _ -> }, // Called when a track is resumed
    var trackStuck: (AudioPlayer, AudioTrack, Long) -> Unit = { _, _, _ -> }, // Called when a track is stuck
    var trackException: (AudioPlayer, AudioTrack, FriendlyException) -> Unit = { _, _, _ -> } // Called when an exception occurs
)
