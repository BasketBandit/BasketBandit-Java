package basketbandit.core.modules.audio.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import java.util.HashMap;

public class MusicManagerHandler {

    private static HashMap<String, GuildAudioManager> managers;
    private static AudioPlayerManager playerManager;

    /**
     * Handles full application runtime handlers
     * managers instead of leaving it to the main class.
     */
    public MusicManagerHandler() {
        managers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();

        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.setFrameBufferDuration(200);
    }

    /**
     * Returns a guild's GuildAudioManager or null if there isn't one.
     * @return trackScheduler or NULL.
     */
    public static GuildAudioManager getGuildMusicManager(String guildID) {
        return managers.get(guildID);
    }

    /**
     * Returns full MusicManager HashMap.
     */
    public static HashMap<String, GuildAudioManager> getGuildMusicManagers() {
        return managers;
    }

    /**
     * Adds tot the GuildAudioManager HashMap.
     */
    public static void addGuildMusicManager(String guildID, GuildAudioManager manager) {
        managers.put(guildID, manager);
    }

    public static AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}