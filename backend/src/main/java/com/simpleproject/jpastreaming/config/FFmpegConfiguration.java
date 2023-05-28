package com.simpleproject.jpastreaming.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegConfiguration {
    @Bean
    public FFmpeg ffMpeg() throws IOException{
        return new FFmpeg("D:/ffmpeg-6.0-essentials_build/ffmpeg-6.0-essentials_build/bin/ffmpeg.exe");
    }

    @Bean
    public FFprobe ffProbe() throws IOException{
        return new FFprobe("D:/ffmpeg-6.0-essentials_build/ffmpeg-6.0-essentials_build/bin/ffprobe.exe");
    }

}
