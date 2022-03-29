package com.github.alvin0319.lavaplayer.ktu.scheduler

import com.github.alvin0319.lavaplayer.ktu.player.PlayerInfo
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.JDA

class TrackScheduler(
    private val jda: JDA,
    private val playerInfo: PlayerInfo,
    private val trackEventCallbackHandler: TrackEventCallbackHandler
) : AudioEventAdapter() {

    private var trackIndex = 0

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        trackEventCallbackHandler.trackStart(player, track)
        playerInfo.currentPlayingTrack = track
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        trackEventCallbackHandler.trackStuck(player, track, thresholdMs)
    }

    override fun onTrackStuck(
        player: AudioPlayer,
        track: AudioTrack,
        thresholdMs: Long,
        stackTrace: Array<out StackTraceElement>
    ) {
        trackEventCallbackHandler.trackStuck(player, track, thresholdMs)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        trackEventCallbackHandler.trackEnd(player, track, endReason)
        if (endReason.mayStartNext || endReason == AudioTrackEndReason.REPLACED) {
            if (playerInfo.trackQueue.isNotEmpty()) {
                if (playerInfo.repeat) {
                    trackIndex++
                    if (trackIndex >= playerInfo.trackQueue.size) {
                        trackIndex = 0
                    }
                    playerInfo.playTrack(playerInfo.trackQueue[trackIndex].makeClone())
                } else {
                    playerInfo.playTrack(playerInfo.trackQueue.poll().makeClone())
                }
                return
            }
        }
        playerInfo.currentPlayingTrack = null
    }

    override fun onPlayerPause(player: AudioPlayer) {
        trackEventCallbackHandler.trackPause(player)
    }

    override fun onPlayerResume(player: AudioPlayer) {
        trackEventCallbackHandler.trackResume(player)
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        trackEventCallbackHandler.trackException(player, track, exception)
    }
}
