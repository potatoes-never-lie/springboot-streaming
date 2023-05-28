<template>
  <div>
    <video
      id="myvid"
      ref="player"
      width="640"
      height="400"
      class="video-js"
      controls
    ></video>
  </div>
</template>

<script>
import videojs from "video.js";
import "video.js/dist/video-js.css";

export default {
  name: "VideoPlayer",
  data() {
    return {
      player: null,
      options: {
        autoplay: false,
        controls: true,
        sources: [
          {
            src: "http://localhost:8080/video/m3u8",
            type: "application/x-mpegURL",
          },
        ],
      },
    };
  },
  created() {
    videojs.Hls.xhr.beforeRequest = function (options) {
      console.log("OPTIONS :::" + options);
      options.headers = {
        ...options.headers,
        Authorization: "This is Auth Header You're Looking for ...",
      };
      return options;
    };
  },

  mounted() {
    this.player = videojs(
      this.$refs.player,
      this.options,
      function onPlayerReady() {
        console.log("onPlayerReady", this);
      },
    );
  },
  beforeUnmounted() {
    if (this.player) {
      this.player.dispose();
    }
  },
};
</script>
