<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>c${cameraId}维修直播</title>

</head>

<body>
<div id="a1"></div>

<script type="text/javascript" src="../play/ckplayer/ckplayer.js" charset="utf-8"></script>
<script type="text/javascript">
    var width = document.body.offsetWidth;
    var height = (width * 9) / 16;
    var flashvars={
        f: '../play/ckplayer/m3u8.swf',         //使用swf向播放器发送视频地址进行播放
        a: 'http://${shopIp}:8080/hls/c${cameraId}.m3u8',      //m3u8文件
        c: 0,       //调用 ckplayer.js 配置播放器
        p: 1,       //自动播放视频
        s: 4,       //flash插件形式发送视频流地址给播放器进行播放
        lv: 1    //注意，如果是直播，需设置lv:1
    };
    var video=['http://${shopIp}:8080/hls/c${cameraId}.m3u8'];
    //  CKobject.embedHTML5('ckplayer/ckplayer.swf','a1','ckplayer_a1','600','400',false,flashvars,video);
    var support=['all'];
    var params={bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always',wmode:'transparent'};
    CKobject.embed('../play/ckplayer/ckplayer.swf','a1','ckplayer_a1', width, height,false,flashvars,video);

</script>
</body>
</html>