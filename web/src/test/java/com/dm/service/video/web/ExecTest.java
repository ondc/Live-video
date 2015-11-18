package com.dm.service.video.web;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by chenlichao on 15/6/18.
 */
public class ExecTest {


    public static void main(String[] args) throws IOException, InterruptedException {

        String cmd = "ffmpeg -re -i /tmp/test.mp4 -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 640x360 -q 10 rtmp://centos7:1935/video/test11";

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(cmd.split(" "));
        processBuilder.directory(new File("/tmp"));
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("/tmp/exec.log")));

        Process process = processBuilder.start();


        Thread.sleep(60000);
    }
}
