package com.simpleproject.jpastreaming.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final FFmpeg ffMpeg;

    private final FFprobe ffProbe;


    @GetMapping("/video")       //mp4
    public ResponseEntity<ResourceRegion> playVideo(@RequestHeader HttpHeaders headers) throws IOException{     //service로 따로 빼는 리팩토링 필요
        log.info("VideoController.playVideo");
        String commonPath="file:///D:/test3.mp4";       //경로
        UrlResource video=new UrlResource(new URL(commonPath));
        ResourceRegion region= resourceRegion(video, headers);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException {
        final long chunkSize=1000000L;
        long contentLength=video.contentLength();
        Optional<HttpRange> optional=headers.getRange().stream().findFirst();
        HttpRange httpRange;
        if (optional.isPresent()){
            httpRange=optional.get();
            long start=httpRange.getRangeStart(contentLength);
            long end=httpRange.getRangeEnd(contentLength);
            long rangeLength=Long.min(chunkSize, end-start+1);
            return new ResourceRegion(video, start, rangeLength);
        }
        else{
            long rangeLength=Long.min(chunkSize, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }

    @GetMapping("/video/conversion")
    public ResponseEntity<?> mp4Tom3u8Format(){
        String videoDirectory = "D:/mp4_m3u8_convert_test/1";
        File folder = new File(videoDirectory);
        if (!folder.exists()){
            try{
                folder.mkdir();
            }
            catch(Exception e){
                e.getStackTrace();
            }
        }
        File result = new File(videoDirectory+"/result.m3u8");
        FFmpegBuilder builder = new FFmpegBuilder();
        builder.setInput("D:/mp4_m3u8_convert_test/test3.mp4")  //mp4 저장위치
                .addOutput(result.getAbsolutePath())
                .setFormat("hls")
                .addExtraArgs("-hls_time", "10") // 분할 시간
                .addExtraArgs("-hls_list_size", "0") // 플레이 리스트 길이, 0: 무제한
                .addExtraArgs("-hls_segment_filename", folder.getAbsolutePath() + "/%03d.ts")
                .addExtraArgs("-codec:", "copy")
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffMpeg, ffProbe);
        executor.createJob(builder).run();
        try {
            Files.delete(Paths.get( "D:/mp4_m3u8_convert_test/test3.mp4"));
        } catch (IOException e) {
           e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/video/m3u8")              //m3u8 format
    public ResponseEntity<Resource> videoHlsM3U8() {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        String fileFullPath = "D:/mp4_m3u8_convert_test/1/result.m3u8";
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "test3" + ".m3u8");
        headers.setContentType(MediaType.parseMediaType("application/x-mpegURL"));
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }


    @GetMapping("/video/{tsName}.ts")
    public ResponseEntity<Resource> videoHlsTs(@PathVariable String tsName) {
        log.debug("************** class = {}, function = {}", this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        String fileFullPath = "D:/mp4_m3u8_convert_test/1/" + tsName + ".ts";
        Resource resource = new FileSystemResource(fileFullPath);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tsName + ".ts");
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

}
