# dm-service-video
视频直播  采用ffmpeg 第三方流媒体服务器 跟nginx 代理模式 进行的摄像头直播过程 本直播并没有对视频大小 流量优化 

1. 环境准备
安装wget: yum install wget -y
安装git: yum install git -y
安装gcc: yum install gcc -y
安装

2. 安装nginx
nginx使用源码方式安装，同时安装nginx-rtmp-module, http_flv_module,http_mp4_module, http_gzip_static_module, http_ssl_module

2.1 安装依赖
yum -y install pcre pcre-devel zlib-devel openssl openssl-devel

2.2 下载nginx-rtmp-module
wget https://github.com/arut/nginx-rtmp-module/archive/v1.1.7.tar.gz

tar -xzvf v1.1.7.tar.gz

2.3 下载nginx源码
wget http://nginx.org/download/nginx-1.9.3.tar.gz

tar -xzvf nginx-1.9.3.tar.gz

2.4 编译安装
cd nginx-1.9.3

./configure --add-module=../nginx-rtmp-module-1.1.7 --with-http_ssl_module --with-http_mp4_module --with-http_flv_module --with-http_gzip_static_module

make

make install

3. 安装ffmpeg
3.1 安装ffmpeg依赖
yum -y install autoconf automake cmake freetype-devel gcc gcc-c++ git libtool make mercurial nasm pkgconfig zlib-devel

3.1.1 安装yasm
YASM是 libx264和ffmpeg要用到得一个汇编工具

git clone --depth 1 git://github.com/yasm/yasm.git

cd yasm

autoreconf -fiv

./configure

make

make install

make distclean

3.1.2 安装Libx264解码包

libx264 是H.264编解码器, 编译时需指定 --enable-gpl --enable-libx264

git clone --depth 1 git://git.videolan.org/x264

cd x264

./configure --prefix="$HOME/ffmpeg_build" --bindir="$HOME/bin" --enable-static

make

make install

make distclean

3.2 安装ffmpeg
git clone --depth 1 git://source.ffmpeg.org/ffmpeg

cd ffmpeg

PKG_CONFIG_PATH="$HOME/ffmpeg_build/lib/pkgconfig" ./configure --prefix="$HOME/ffmpeg_build" --extra-cflags="-I$HOME/ffmpeg_build/include" --extra-ldflags="-L$HOME/ffmpeg_build/lib" --bindir="$HOME/bin" --pkg-config-flags="--static" --enable-gpl --enable-libx264

make

make install

4. 配置nginx
 rtmp {

    server {

        listen 1935;

        application video {

            live on;

        }

        application hls {

            live on;

            hls on;

            hls_path /tmp/hls;

        }

     }  

 }

然后,针对hls,还需要在http里面增加一个location配置

 location /hls {

     types {

         application/vnd.apple.mpegurl m3u8;

         video/mp2t ts;

     }

     root /tmp;

     add_header Cache-Control no-cache;

 }
