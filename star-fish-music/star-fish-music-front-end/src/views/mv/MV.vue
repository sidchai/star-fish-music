<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="mv">
    <m-v-list :mvList="data"></m-v-list>
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
import MVList from './component/MVList'
import { getAllMV } from '@/api/mv'
export default {
  name: 'MV',
  components: {
    MVList
  },
  data () {
    return {
      pageSize: 15, // 页数
      currentPage: 1, // 当前页
      albumDatas: [], //MV信息
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
    this.GetAllMV()
  },
  methods: {
    // 获取当前页
    handleCurrentChange (val) {
      this.currentPage = val
    },
    // 获取所有mv
    GetAllMV () {
      getAllMV().then(resp => {
        this.loading = false
        this.currentPage = 1
        this.albumDatas = resp.data.mvInfo
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../../assets/css/mv';
</style>
