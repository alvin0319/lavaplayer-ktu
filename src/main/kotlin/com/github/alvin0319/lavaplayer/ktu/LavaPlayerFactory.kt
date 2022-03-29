package com.github.alvin0319.lavaplayer.ktu

import com.github.alvin0319.lavaplayer.ktu.player.PlayerInfo
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

object LavaPlayerFactory {
    @JvmStatic
    val audioPlayerManager = DefaultAudioPlayerManager()

    @JvmStatic
    val audioPlayers: MutableMap<JDA, MutableMap<Guild, PlayerInfo>> = mutableMapOf()

    @JvmStatic
    val requiredIntents: List<GatewayIntent> = listOf(
        GatewayIntent.GUILD_VOICE_STATES
    )

    @JvmStatic
    val requiredFlags: List<CacheFlag> = listOf(
        CacheFlag.VOICE_STATE,
    )

    @JvmStatic
    val requiredMemberPolicies: List<MemberCachePolicy> = listOf(
        MemberCachePolicy.VOICE
    )

    @JvmStatic
    val requiredPermission: List<Permission> = listOf(
        Permission.VOICE_CONNECT,
        Permission.VOICE_SPEAK,
        Permission.VOICE_DEAF_OTHERS,
        Permission.PRIORITY_SPEAKER
    )

    @JvmStatic
    fun createPlayer(jda: JDA, guild: Guild): PlayerInfo {
        requiredIntents.forEach { intent ->
            if (!jda.gatewayIntents.contains(intent)) {
                throw IllegalStateException("JDA does not have the required intent: $intent")
            }
        }
        requiredFlags.forEach { flag ->
            if (!jda.cacheFlags.contains(flag)) {
                throw IllegalStateException("JDA does not have the required flag: $flag")
            }
        }
        if (!guild.selfMember.hasPermission(requiredPermission)) {
            throw IllegalStateException("JDA does not have the required permissions: ${requiredPermission.joinToString { "${it.name}, " }}")
        }
        return if (audioPlayers.getOrPut(jda, ::mutableMapOf).containsKey(guild)) {
            audioPlayers.getValue(jda).getValue(guild)
        } else {
            val playerInfo = PlayerInfo(jda, guild)
            audioPlayers.getValue(jda)[guild] = playerInfo
            playerInfo
        }
    }

    fun getPlayer(jda: JDA, guild: Guild): PlayerInfo? {
        return audioPlayers.getOrPut(jda, ::mutableMapOf)[guild]
    }
}
