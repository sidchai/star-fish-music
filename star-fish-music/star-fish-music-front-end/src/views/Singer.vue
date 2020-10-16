<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="singer">
    <ul class="singer-header">
      <li
        v-for="(item, index) in singerStyle"
        :key="index"
        :class="{active: item.name === activeName}"
        @click="handleChangeView(item)">
        {{item.name}}
      </li>
    </ul>
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
</template>

<script>
import ContentList from '../components/ContentList'
import { singerStyle } from '@/assets/data/singer'
import { getAllSinger, getSingerByClassify } from '@/api/singer'

export default {
  name: 'singer',
  components: {
    ContentList
  },
  data () {
    return {
      singerStyle: [], // 歌手导航栏类别
      activeName: '全部',
      pageSize: 15, // 页数
      currentPage: 1, // 当前页
      albumDatas: [], //歌手信息
      loading: true
    }
  },
  computed: {
    // 计算当前表格中的数据
    data () {
      return this.albumDatas.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  created () {
    this.singerStyle = singerStyle
    this.GetAllSinger()
  },
  methods: {
    // 获取当前页
    handleCurrentChange (val) {
      this.currentPage = val
    },
    handleChangeView (item) {
      this.activeName = item.name
      this.albumDatas = []
      if (item.name === '全部') {
        this.GetAllSinger()
      } else {
        this.GetSingerClassify(item.type)
      }
    },
    // 获取所有歌手
    GetAllSinger () {
      getAllSinger().then(resp => {
        this.loading = false
        this.currentPage = 1
        this.albumDatas = resp.data.singerInfo
      }).catch(err => {
        console.log(err)
      })
    },
    // 通过类别对歌手分类
    GetSingerClassify (classify) {
      getSingerByClassify(classify).then(resp => {
          this.currentPage = 1
          this.albumDatas = resp.data.classifySingerInfo
        }).catch(err => {
          console.log(err)
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/singer.scss';
</style>
