<template>
  <div class="singer-album">
    <div class="album-slide">
      <div class="singer-img">
        <img :src="singer.pic" alt="">
      </div>
      <ul class="info">
        <li>出道时间：{{singer.debutTime}}</li>
        <li>地区：{{singer.region}}</li>
      </ul>
    </div>
    <div class="album-content">
      <div class="intro">
        <h2>{{singer.singerName}}</h2>
        <span style="text-overflow:ellipsis">{{singer.introduction}}</span>
      </div>
      <div class="content">
        <album-content
          v-loading="loading"
          element-loading-text="拼命加载中"
          element-loading-spinner="el-icon-loading"
          :songList="listOfSongs">
          <template slot="title">歌单</template>
        </album-content>
      </div>
    </div>
  </div>
</template>

<script>
import { mixin } from '@/mixins'
import { mapGetters } from 'vuex'
import AlbumContent from '../components/AlbumContent'
import { getSongBySingerId } from '@/api/singer'

export default {
  name: 'singer-album',
  components: {
    AlbumContent
  },
  mixins: [mixin],
  data () {
    return {
      singerId: '',
      singer: {},
      loading: true
    }
  },
  computed: {
    ...mapGetters([
      'tempList',
      'listOfSongs'
    ])
  },
  mounted () {
    this.singerId = this.$route.params.id // 给歌单ID赋值
    this.singer = this.tempList
    this.getSongList()
  },
  methods: {
    getSongList () {
      getSongBySingerId(this.singerId).then(resp => {
        this.loading = false
        this.$store.commit('setListOfSongs', resp.data.singerSongInfo)
      }).catch(err => {
          console.log(err)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/singer-album.scss';
</style>
