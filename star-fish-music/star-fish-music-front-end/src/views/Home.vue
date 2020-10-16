<template>
  <div class="home">
    <!--轮播图-->
    <swiper/>
    <!--热门歌单/歌手-->
    <div
      v-loading="loading"
      element-loading-text="拼命加载中"
      element-loading-spinner="el-icon-loading"
      class="section"
      v-for="(item, index) in songsList"
      :key="index">
      <div class="section-title">{{item.name}}</div>
      <content-list :contentList="item.list"></content-list>
    </div>
  </div>
</template>

<script>
import Swiper from '../components/Swiper'
import ContentList from '../components/ContentList'
import { getAllSinger } from '@/api/singer'
import { getAllSongList } from '@/api/songList'

export default {
  name: 'home',
  components: {
    Swiper,
    ContentList
  },
  data () {
    return {
      songsList: [
        {name: '热门歌单', list: []},
        {name: '热门歌手', list: []}
      ],
      loading: true
    }
  },
  created () {
    // 获取歌单列表
    this.GetSongList()
    // 获取歌手列表
    this.GetSinger()
  },
  methods: {
    GetSongList () {
      getAllSongList().then(resp => {
        this.loading = false
        this.songsList[0].list = resp.data.songListInfo.slice(0, 10)
      }).catch(err => {
        console.log(err)
      })
    },
    GetSinger () {
      getAllSinger().then(resp => {
        this.loading = false
        this.songsList[1].list = resp.data.singerInfo.slice(0,10)
      }).catch(err => {
          console.log(err)
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/home.scss';
</style>
