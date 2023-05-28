<template>
  <video id="myvid" width="640" height="400" controls type="video/mp4"></video>
</template>

<script>
import axios from "axios";

export default {
  name: "App",
  components: {},
  data() {
    return {
      url: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    };
  },
  async mounted() {
    await this.getVideo("http://localhost:8080/video");
  },
  methods: {
    getVideo: async function (filePath) {
      let myvid = document.getElementById("myvid");
      const response = await axios.request({
        responseType: "blob",
        url: filePath,
        method: "get",
        headers: {
          "Content-Type": "video/mp4",
          Token: "your_Token_HERE",
        },
      });
      const videoBlob = await response.data;
      const buf = await videoBlob.arrayBuffer();
      this.url = URL.createObjectURL(new Blob([buf]));
      console.log("SHOWING::" + myvid.src);
      myvid.load();
      myvid.onloadeddata = function () {
        myvid.play();
      };
    },
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
