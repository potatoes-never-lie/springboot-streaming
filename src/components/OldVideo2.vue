<template>
  <div>
    <video ref="player" id="player" class="video-js">
      <source src="http://localhost:8080/video" type="video/mp4" />
      <slot>
        <p class="vjs-no-js">
          To view this video please enable JavaScript, and consider upgrading to
          a web browser that
          <a href="https://videojs.com/html5-video-support/" target="_blank"
            >supports HTML5 video</a
          >
        </p>
      </slot>
    </video>
  </div>
</template>

<script>
import videojs from "video.js";
import "video.js/dist/video-js.css";

export default {
  name: "VideoPlayer",
  data() {
    return {
      player: videojs.Player,
    };
  },

  mounted() {
    videojs.Vhs.xhr.beforeRequest = (options) => {
      options.headers = {
        ...options.headers,
        Authorization: `Bearer OHH`,
      };
    };

    var options = (videojs.PlayerOptions = {
      fluid: true,
      preload: "auto",
      controls: true,
      controlBar: {
        pictureInPictureToggle: false,
      },
    });

    this.player = videojs(this.$refs.player, options);
  },
  beforeUnmounted() {
    if (this.player) {
      this.player.dispose();
    }
  },
};
</script>
