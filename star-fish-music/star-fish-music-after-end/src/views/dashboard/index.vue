<template>
  <div class="dashboard-editor-container">
    <github-corner class="github-corner" />

    <panel-group />

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :md="12" :lg="6">
        <div class="chart-wrapper">
          <span class="chart-font-style">用户性别</span>
          <pie-chart
            v-if="showPieChart"
            @clickPie="clickSingerRegion"
            :value="userCountByGender"
            :tag-name="groupingGenderArray"
          />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="6">
        <div class="chart-wrapper">
          <span class="chart-font-style">歌手类型</span>
          <bar-chart
            v-if="showBarChart"
            @clickBar="clickSingerClassify"
            :value="singerCountByClassify"
            :tag-name="groupingTypesArray"
          />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="6">
        <div class="chart-wrapper">
          <span class="chart-font-style">歌手地区</span>
          <pie-chart
            v-if="showPieChart"
            @clickPie="clickSingerRegion"
            :value="singerCountByRegion"
            :tag-name="groupingAreaArray"
            :type="1"
          />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="6">
        <div class="chart-wrapper">
          <span class="chart-font-style">歌单风格</span>
          <bar-chart
            v-if="showBarChart"
            @clickBar="clickSongListStyle"
            :value="songListCountByStyle"
            :tag-name="groupingStylesArray"
            :type="1"
          />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
        <transaction-table />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">
        <todo-list />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">
        <box-card />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import GithubCorner from '@/components/GithubCorner'
import PanelGroup from './components/PanelGroup'
import BarChart from './components/BarChart'
import PieChart from './components/PieChart'
import TransactionTable from './components/TransactionTable'
import TodoList from './components/TodoList'
import BoxCard from './components/BoxCard'
import { getAllUser } from '@/api/user'

export default {
  name: 'Dashboard',
  components: {
    GithubCorner,
    PanelGroup,
    BarChart,
    PieChart,
    TransactionTable,
    TodoList,
    BoxCard
  },
  data() {
    return {
      showPieChart: true, // 是否显示圆形分格统计图表
      showBarChart: true, // 是否显示柱形图表
      userCountByGender: [
        { value: 1, name: '男' },
        { value: 1, name: '女' }
      ], // 根据性别查询用户信息
      groupingGenderArray: ['男', '女'], // 性别数组
      singerCountByClassify: [21, 10, 3, 2, 6], // 根据类型查询歌手信息
      groupingTypesArray: ['全部', '华语', '欧美', '日本', '韩国'], // 类型数组
      singerCountByRegion: [
        { value: 50, name: '中国' },
        { value: 15, name: '美国' },
        { value: 30, name: '韩国' },
        { value: 5, name: '加拿大' },
        { value: 5, name: '日本' },
        { value: 2, name: '新加坡' },
        { value: 1, name: '越南' }
      ], // 根据地区查询歌手信息
      groupingAreaArray: ['中国', '韩国', '美国', '加拿大', '日本', '新加坡', '越南'], // 地区数组
      songListCountByStyle: [2, 4, 1, 2, 1], // 根据风格查询歌单信息
      groupingStylesArray: ['华语', '日韩', '流行', '欧美', '轻音乐'] // 风格数组
    }
  },
  methods: {
    clickSingerRegion(index) {
      let tag = this.singerCountByRegion[index] // 得到当前点击的地区名
      /* this.$router.push({
        path: '/',
        query: {
          tag: tag
        }
      })*/
    },
    clickSingerClassify(index) {
      let tag = this.singerCountByClassify[index]
      /* this.$router.push({
        path: '/',
        query: {
          tag: tag
        }
      })*/
    },
    clickSongListStyle(index) {
      let tag = this.songListCountByStyle[index]
      /* this.$router.push({
        path: '/',
        query: {
          tag: tag
        }
      })*/
    },
    GetAllUser() {
      getAllUser().then(resp => {
        const data = resp.data.userInfo
        this.userCountByGender[0].value = this.setSex('1', data)
        this.userCountByGender[1].value = this.setSex('0', data)
      })
    },
    setSex(gender, arr) {
      let count = 0
      for (const item of arr) {
        if (gender === item.gender) {
          count++
        }
      }
      return count
    }
  },
  created() {
    this.GetAllUser()
  }
}
</script>

<style lang="scss" scoped>
  .dashboard-editor-container {
    padding: 32px;
    background-color: rgb(240, 242, 245);
    position: relative;

    .github-corner {
      position: absolute;
      top: 0px;
      border: 0;
      right: 0;
    }

    .chart-wrapper {
      background: #fff;
      padding: 16px 16px 0;
      margin-bottom: 32px;
      text-align: center;

      .chart-font-style {
        color: #2EC7C9;
      }
    }
  }

  @media (max-width:1024px) {
    .chart-wrapper {
      padding: 8px;
    }
  }
</style>
