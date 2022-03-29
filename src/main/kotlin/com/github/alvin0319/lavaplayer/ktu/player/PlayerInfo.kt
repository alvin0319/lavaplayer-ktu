package com.github.alvin0319.lavaplayer.ktu.player

import com.github.alvin0319.lavaplayer.ktu.LavaPlayerFactory
import com.github.alvin0319.lavaplayer.ktu.scheduler.TrackEventCallbackHandler
import com.github.alvin0319.lavaplayer.ktu.scheduler.TrackScheduler
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import java.util.LinkedList

class PlayerInfo(
    val jda: JDA,
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

    var trackLoadHandler: (List<AudioTrack>, Boolean) -> Unit = { tracks, isFromPlaylist ->
        if (isFromPlaylist) {
            if (trackQueue.isNotEmpty()) {
                trackQueue.addAll(tracks)
            } else {
                playTrack(tracks.first())
                trackQueue.addAll(tracks.subList(1, tracks.size))
            }
        } else {
            if (trackQueue.isNotEmpty()) {
                playTrack(tracks.first())
            } else {
                trackQueue.add(tracks[0])
            }
        }
    }

    /**
     * Join the given [AudioChannel]
     * @param audioChannel the [AudioChannel] to join
     * @param forceMoveChannel if true, the bot will move itself to the given audio channel, otherwise it will throw an [IllegalStateException]
     * @throws IllegalStateException if the bot is already connected to the audio channel
     */
    @JvmOverloads
    @Throws(IllegalStateException::class)
    fun joinAudioChannel(audioChannel: AudioChannel, forceMoveChannel: Boolean = false) {
        if (joinedAudioChannel != null && !forceMoveChannel) {
            throw IllegalStateException("Cannot join audio channel")
        }
        if (guild.selfMember.voiceState?.isGuildDeafened == false) {
            // prevent bandwidth usage
            guild.deafen(guild.selfMember, true).queue(null) {}
        }
        if (joinedAudioChannel != null && audioPlayer != null) {
            guild.moveVoiceMember(guild.selfMember, audioChannel)
        } else {
            audioChannel.guild.audioManager.openAudioConnection(audioChannel)
            audioPlayer = LavaPlayerFactory.audioPlayerManager.createPlayer().apply {
                addListener(trackScheduler)
            }
        }
        joinedAudioChannel = audioChannel
    }

    /**
     * Starts playing the given [AudioTrack]
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
    fun internalMoveAudioChannel(newAudioChannel: AudioChannel) {
        this.joinedAudioChannel = newAudioChannel
    }

    fun moveAudioChannel(newAudioChannel: AudioChannel) {
        if (joinedAudioChannel == newAudioChannel) {
            throw IllegalArgumentException("Cannot move to the same audio channel")
        }
        if (joinedAudioChannel == null) {
            throw IllegalStateException("Cannot move audio channel while not connected to any audio channel")
        }
        guild.moveVoiceMember(guild.selfMember, newAudioChannel).queue(null) {}
    }

    fun isChannelJoined(): Boolean = joinedAudioChannel != null && audioPlayer != null

    /**
     * Returns whether the bot is currently connected to an audio channel.
     */
    fun isConnected(): Boolean = isChannelJoined() && guild.selfMember.voiceState?.inAudioChannel() == true

    /**
     * Immediately stops the current playing track and disconnects from the [AudioChannel]
     */
    fun disconnect() {
        joinedAudioChannel?.guild?.audioManager?.closeAudioConnection()
        joinedAudioChannel = null
        audioPlayer?.destroy()
        audioPlayer = null
        currentPlayingTrack = null
    }

    fun isPlaying(): Boolean = audioPlayer != null && audioPlayer!!.playingTrack != null && currentPlayingTrack != null

    /**
     * @param paused whether to pause track or not.
     */
    fun setPaused(paused: Boolean) {
        audioPlayer?.isPaused = paused
    }

    /**
     * @return whether the track is currently paused.
     */
    fun isPaused(): Boolean = audioPlayer?.isPaused ?: false

    /**
     * Sets the volume of the track
     * @param volume the volume to set the track to.
     */
    fun setVolume(volume: Int) {
        audioPlayer?.volume = volume
    }

    /**
     * @return the current volume of the track.
     */
    fun getVolume(): Int = audioPlayer?.volume ?: 0

    /**
     * Searches for a track with the given query and plays it.
     * @param query the query to search for. If query is URL, it will be just used as a URL. otherwise, it will be searched and load all of them.
     */
    fun search(query: String) {
        // check if query contains https or http
        val regex = Regex("(https?://.*)|(www\\..*)")
        LavaPlayerFactory.audioPlayerManager.loadItem(
            if (regex.matches(query)) query else "ytsearch:$query",
            object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    trackLoadHandler(listOf(track), false)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    trackLoadHandler(playlist.tracks, true)
                }

                override fun noMatches() {
                    trackLoadHandler(listOf(), false)
                }

                override fun loadFailed(exception: FriendlyException?) {
                    trackLoadHandler(listOf(), false)
                }
            }
        )
    }
}
