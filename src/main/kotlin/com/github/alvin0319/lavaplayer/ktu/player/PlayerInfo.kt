package com.github.alvin0319.lavaplayer.ktu.player

import com.github.alvin0319.lavaplayer.ktu.LavaPlayerFactory
import com.github.alvin0319.lavaplayer.ktu.scheduler.TrackEventCallbackHandler
import com.github.alvin0319.lavaplayer.ktu.scheduler.TrackScheduler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import java.util.LinkedList

class PlayerInfo(
    private val jda: JDA,
    val guild: Guild
) {
    var currentPlayingTrack: AudioTrack? = null

    var joinedAudioChannel: AudioChannel? = null
        private set

    var audioPlayer: AudioPlayer? = null
        private set

    val trackQueue: LinkedList<AudioTrack> = LinkedList()

    var repeat: Boolean = false

    val trackEventCallbackHandler = TrackEventCallbackHandler()

    var trackScheduler: AudioEventAdapter = TrackScheduler(jda, this, trackEventCallbackHandler)

    /**
     * Let the bot join the [AudioChannel]
     * @param audioChannel the [AudioChannel] to join
     * @param forceMoveChannel if true, the bot will move itself to the given audio channel, otherwise it will throw an IllegalStateException
     * @throws IllegalStateException if the bot is already in the given audio channel
     */
    @JvmOverloads
    @Throws(IllegalStateException::class)
    fun joinAudioChannel(audioChannel: AudioChannel, forceMoveChannel: Boolean = false) {
        if (joinedAudioChannel != null && !forceMoveChannel) {
            throw IllegalStateException("Cannot join audio channel")
        }
        if (joinedAudioChannel != null && audioPlayer != null) {
            guild.moveVoiceMember(guild.selfMember, audioChannel)
        } else {
            audioChannel.guild.audioManager.openAudioConnection(audioChannel)
            audioPlayer = LavaPlayerFactory.audioPlayerManager.createPlayer()
        }
        joinedAudioChannel = audioChannel
    }

    /**
     * Let the bot play the given [AudioTrack]
     * @param track the [AudioTrack] to play
     * @throws IllegalStateException if the bot is not in the audio channel, or the bot is already playing a track
     */
    @Throws(IllegalStateException::class)
    fun playTrack(track: AudioTrack, interruptPlaying: Boolean = false) {
        if (!isChannelJoined()) {
            throw IllegalStateException("Cannot play track while not in audio channel")
        }
        if (currentPlayingTrack != null && !interruptPlaying) {
            throw IllegalStateException("Cannot play track while another track is playing")
        }
        audioPlayer!!.playTrack(track)
    }

    /**
     * Moves the bot to the given audio channel.
     * This does not actually move the bot, it just changes the audio channel the bot is in.
     * Do not call this manually.
     * @param newAudioChannel the audio channel to move to
     */
    fun moveAudioChannel(newAudioChannel: AudioChannel) {
        this.joinedAudioChannel = newAudioChannel
    }

    fun isChannelJoined(): Boolean = joinedAudioChannel != null
}
