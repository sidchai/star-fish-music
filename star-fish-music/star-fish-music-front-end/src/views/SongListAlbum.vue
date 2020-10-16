<template>
  <div class="song-list-album">
    <div class="album-slide">
      <div class="album-img">
        <img :src=songLists.pic alt="">
      </div>
      <div class="album-info">
        <h2>简介：</h2>
        <span>
          {{songLists.introduction}}
        </span>
      </div>
    </div>
    <div class="album-content">
      <div class="album-title">
        <p>{{songLists.title}}</p>
      </div>
      <!--歌曲-->
      <div class="songs-body">
        <album-content
          v-loading="loading"
          element-loading-text="拼命加载中"
          element-loading-spinner="el-icon-loading"
          :songList="listOfSongs">
          <template slot="title">歌单</template>
        </album-content>
        <comment :playId="songListId" :type="1"></comment>
      </div>
    </div>
  </div>
</template>

<script>
import { mixin } from '@/mixins'
import { mapGetters } from 'vuex'
import AlbumContent from '@/components/AlbumContent'
import Comment from '@/components/Comment'
import { getSongByListId, getSongListById } from '@/api/songList'

export default {
  name: 'song-list-album',
  components: {
    AlbumContent,
    Comment
  },
  data () {
    return {
      songLists: [],
      singers: {},
      count: 0, // 点赞数
      songListId: '', // 歌单ID
      value3: 0,
      value5: 0,
      loading: true
    }
  },
  computed: {
    ...mapGetters([
      'loginIn', // 登录标识
      'tempList', // 单个歌单信息
      'listOfSongs', // 存放的音乐
      'userId', // 用户ID
      'avator' // 用户头像
    ])
  },
  created () {
    this.songListId = this.tempList.id // 给歌单ID赋值
    this.singers = this.tempList
    this.GetSongId() // 获取歌单里面的歌曲ID
    this.GetSongList()
  },
  mixins: [mixin],
  methods: {
    // 收集歌单里面的歌曲
    GetSongId () {
      getSongListById(this.songListId)
        .then(resp => {
          this.loading = false
          this.songLists = resp.data.songListById
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 获取单里的歌曲
    GetSongList () {
      getSongByListId(this.songListId)
        .then(resp => {
          this.$store.commit('setListOfSongs', resp.data.songListInfo)
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/song-list-album.scss';
</style>
