<template>
  <div>
    <video-player />
  </div>
</template>

<script>
import VideoPlayer from "./VideoPlayer.vue";
import "video.js/dist/video-js.css";

export default {
  name: "VideoExample",
  components: {
    VideoPlayer,
  },
  data() {
    return {};
  },
};
</script>
