package com.solace.demo.iot.common.movieParser;

import com.solace.demo.iot.common.movieParser.domain.MovieMetadata;
import com.solace.demo.iot.common.movieParser.domain.Audio;
import com.solace.demo.iot.common.movieParser.domain.General;
import com.solace.demo.iot.common.movieParser.domain.Video;
import com.solace.demo.iot.common.movieParser.jna.MediaInfo;

import java.io.File;

public class MovieMetadataParser {
    
    /**
     * Singleton pattern, avoid synchronized using static declaration.
     */
    public enum MovieMetadataParserHolder {
        INSTANCE;
        MovieMetadataParser PARSER = new MovieMetadataParser();
    }
    
    /**
     * Singleton pattern, holder.
     *
     * @return
     */
    public static MovieMetadataParser getInstance() {
        return MovieMetadataParserHolder.INSTANCE.PARSER;
    }

    /**
     * Default Parser.
     */
    private MovieMetadataParser() {
    }
    
    /**
     * Read Metadata of file
     */
    public MovieMetadata parseFile(String path) {
        File file         = new File(path);
        MediaInfo info    = new MediaInfo();
        MovieMetadata mm = new MovieMetadata(path);
        try {
            info.open(file);
            for (General gKey : General.values()) {
                String val = info.get(MediaInfo.StreamKind.General, 0, gKey.getKey(),  MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                if (val != null && !val.isEmpty()) {
                    mm.getGeneralKeys().put(gKey, val);
                }
            }
            for (Audio aKey : Audio.values()) {
                String val = info.get(MediaInfo.StreamKind.Audio, 0, aKey.getKey(),  MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                if (val != null && !val.isEmpty()) {
                    mm.getAudioKeys().put(aKey, val);
                }
            }
            for (Video vKey : Video.values()) {
                String val = info.get(MediaInfo.StreamKind.Video, 0, vKey.getKey(),  MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
                if (val != null && !val.isEmpty()) {
                    mm.getVideoKeys().put(vKey, val);
                }
            }
        } finally {            
            info.close();   
        }
        return mm;
    }

}
