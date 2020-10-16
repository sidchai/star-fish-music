<template>
  <div class="mv-album">
    <!-- 播放器 -->
    <div class="prism-player" id="J_prismPlayer"></div>
  </div>
</template>

<script>
  import { getPlayAuth } from '@/api/mv'
  export default {
    name: 'MVAlbum',
    data() {
      return {
        sourceId: '',
        playAuth: null
      }
    },
    mounted() {
      this.$store.commit('setIsPlay', false);
      this.sourceId = this.$route.params.id // 给视频id赋值
      getPlayAuth(this.sourceId).then(resp => {
        this.sourceId = this.sourceId
        this.playAuth = resp.data.playAuth
        console.log(this.playAuth)
        new Aliplayer({
          id: 'J_prismPlayer',
          width: '100%',
          vid : this.sourceId,
          playauth: this.playAuth
        }, function (player) {
          console.log('播放器创建好了。')
        });
      })
    }
  }
</script>

<style lang="scss" scoped>
  @import "../../assets/css/mv-album";
</style>
