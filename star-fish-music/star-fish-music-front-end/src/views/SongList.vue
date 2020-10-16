<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="song-list">
    <ul class="song-list-header">
      <li
        v-for="(item, index) in songStyle"
        :key="index"
        :class="{active: item.name === activeName}"
        @click="handleChangeView(item.name)">
        {{item.name}}
      </li>
    </ul>
    <div class="song-content">
      <content-list :contentList="data"></content-list>
      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          background
          layout="total, prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="albumDatas.length">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import ContentList from '../components/ContentList'
import { mapGetters } from 'vuex'
import { songStyle } from '@/assets/data/songList'
import { getAllSongList, getSongListByStyle } from '@/api/songList'

export default {
  name: 'song-list',
  components: {
    ContentList
  },
  data () {
    return {
      songStyle: [], // 歌单导航栏类别
      activeName: '全部歌单',
      pageSize: 15, // 页数
      currentPage: 1, // 当前页
      albumDatas: [], // 歌单
      loading: true
    }
  },
  computed: {
    ...mapGetters([
      'songsList'
    ]),
    // 计算当前表格中的数据
    data () {
      return this.albumDatas.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  mounted () {
    this.songStyle = songStyle
    this.handleChangeView('全部歌单')
  },
  methods: {
    // 获取当前页
    handleCurrentChange (val) {
      this.currentPage = val
    },
    // 获取歌单
    handleChangeView (name) {
      this.activeName = name
      this.albumDatas = []
      if (name === '全部歌单') {
        this.GetSongList()
      } else {
        this.GetSongListByStyle(name)
      }
    },
    // 获取全部歌单
    GetSongList () {
      getAllSongList()
        .then(resp => {
          this.loading = false
          this.currentPage = 1
          this.albumDatas = resp.data.songListInfo
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 通过类别获取歌单
    GetSongListByStyle (style) {
      getSongListByStyle(style)
        .then(resp => {
          this.currentPage = 1
          this.albumDatas = resp.data.songListByStyle
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/song-list.scss';
</style>
