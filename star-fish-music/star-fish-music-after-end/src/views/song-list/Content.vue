<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="7" :md="7" :lg="6">
          <el-input
            v-model="keyword"
            prefix-icon="el-icon-search"
            clearable
            placeholder="请输入歌曲名"
          />
        </el-col>
        <el-col :xs="24" :sm="17" :md="17" :lg="18">
          <el-button v-permission="'/song-list-content/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加</el-button>
          <el-button v-permission="'/song-list-content/delete-all'" class="filter-item" type="danger" icon="el-icon-delete" @click="batchDelete">批量删除</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table :data="data" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" />

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="歌曲名" width="300" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.songName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="歌曲专辑" width="650" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.introduction }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="500" align="center">
        <template slot-scope="scope">
          <el-tag type="waring">{{ scope.row.createTime }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button v-permission="'/song-list-content/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="tableData.length"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" :before-close="handleClose">
      <el-form ref="form" :model="form" :rules="rules" :status-icon="true">

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item
              label="歌曲名"
              :label-width="formLabelWidth"
              prop="songId"
            >
              <el-cascader
                v-model="form.songId"
                :options="options"
                placeholder="请选择"
                :props="{ expandTrigger: 'hover' }"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelOper">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSongListSongInfo,
  addSongListSongInfo,
  deleteSongListSongInfo,
  deleteBatchSongListSongInfo
} from '@/api/songListSong'
import { getSingerBySong } from '@/api/singer'
export default {
  name: 'Content',
  data() {
    return {
      loading: true,
      songListId: 0, // 接收传递过来的 歌单id
      keyword: '', // 搜索关键字
      tableData: [], // 表格数据
      tempDate: [], // 用于存放搜索的临时信息
      currentPage: 1, // 当前页数
      pageSize: 5, // 每页显示的数目
      title: '', // 对话框标题
      dialogFormVisible: false, // 控制弹出框
      form: {}, // 表单数据
      multipleSelection: [], // 存储多选框选中的行数
      formLabelWidth: '100px', // 表单标签宽度
      rules: { // 表单校验规则
        songId: [
          { required: true, message: '歌曲名不能为空', trigger: 'blur' }
        ]
      },
      options: [] // 级联选择器数据
    }
  },
  computed: {
    // 计算当前表格中的数据
    data() {
      return this.tableData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  // 监听输入框 keyword的变动
  watch: {
    keyword(newVal, oldVal) {
      console.log(newVal)
      console.log(oldVal)
      if (this.keyword === '') {
        this.tableData = this.tempDate
      } else {
        this.tableData = []
        for (const item of this.tempDate) {
          console.log(item)
          if (item.songName.toLowerCase().includes(this.keyword.toLowerCase())) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  methods: {
    // 获取所有歌曲 & 歌单 中间表信息
    GetSongListSongInfo() {
      getSongListSongInfo(this.songListId).then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.songListSongInfo.songs
          this.tempDate = resp.data.songListSongInfo.songs
        }
      })
    },
    // 所有歌手信息列表以及包含的歌曲信息
    GetSingerBySong() {
      getSingerBySong().then(resp => {
        const data = resp.data.singerSongInfo
        const options = []
        for (let i = 0; i < data.length; i++) {
          const singers = {} // 歌手信息
          singers.label = data[i].singerName
          singers.value = data[i].id
          const songData = data[i].songs
          const songList = [] // 所有歌曲信息
          for (let j = 0; j < songData.length; j++) {
            const song = {}
            song.label = songData[j].songName
            song.value = songData[j].id
            songList.push(song)
          }
          singers.children = songList
          options.push(singers)
        }
        this.options = options
      })
    },
    // 添加
    handleAdd(row) {
      this.dialogFormVisible = true // 打开对话框
      this.title = '增加标签' // 设置对话框标题
      this.form = {} // 重置表单中的数据，防止回显
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该歌曲信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSongListSongInfo(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          // 重新渲染表格
          this.GetSongListSongInfo()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 批量删除
    batchDelete() {
      // 得到存入选中数据的数组
      const data = this.multipleSelection
      // 如果没有选中数据就提示
      if (data.length <= 0) {
        this.$message({
          showClose: true,
          message: '您还未选中，请选中后再删除',
          type: 'error'
        })
        return false
      }
      // 开始删除提示
      this.$confirm('此操作将删除选中歌曲信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = [] // 多个选中的id
        data.forEach(item => {
          ids.push(item.id)
        })
        const params = new URLSearchParams()
        params.append('ids', ids)
        deleteBatchSongListSongInfo(params).then(resp => {
          if (resp.code === 20000) { // 请求成功
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.GetSongListSongInfo() // 重新渲染表格
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 分页点击切换
    handleCurrentChange(val) {
      this.currentPage = val
    },
    // 关闭对话框
    cancelOper() {
      this.dialogFormVisible = false
      this.form = {} // 重置表单中的数据，防止回显
    },
    // 获得多选框选中的行数
    handleSelectionChange(val) {
      this.multipleSelection = val // 存入选中的行数
    },
    // 关闭对话框
    handleClose() {
      this.dialogFormVisible = false
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) { // 校验通过
          const data = this.form
          const songId = data.songId
          data.songListId = this.songListId
          if (songId.length > 0) {
            // 选取最后一个元素
            data.songId = songId[songId.length - 1]
          } else {
            this.$message({
              type: 'error',
              message: '请选中歌曲名'
            })
            return
          }
          addSongListSongInfo(data).then(resp => {
            if (resp.code === 20000) { // 请求成功
              this.$message({ // 提示信息
                type: 'success',
                message: resp.message
              })
              this.dialogFormVisible = false // 关闭对话框
              this.GetSongListSongInfo() // 重新渲染表格
            }
          })
        } else {
          console.log('校验错误!!!')
          return false
        }
      })
    }
  },
  created() {
    // 初始化数据
    this.songListId = this.$route.query.id
    this.GetSongListSongInfo()
    this.GetSingerBySong()
  }
}
</script>

<style scoped>
  .pagination {
    display: flex;
    justify-content: center;
  }
</style>
