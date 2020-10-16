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
            placeholder="请输入mv标题"
          />
        </el-col>
        <el-col :xs="24" :sm="17" :md="17" :lg="18">
          <el-button v-permission="'/mv/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加</el-button>
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

      <el-table-column label="标题" width="300" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column label="歌手名" width="300" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.singerName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="300" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            style="display: block"
            active-color="#ff4949"
            inactive-color="#13ce66"
            active-text="下架"
            :active-value="0"
            inactive-text="上架"
            :inactive-value="1"
            @change="statusChange(scope.row)"
          />
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="300">
        <template slot-scope="scope">
          <el-button v-permission="'/mv/edit'" type="primary" size="small" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-permission="'/mv/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除
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

            <el-form-item label="标题名" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="歌手名" :label-width="formLabelWidth" prop="singerName">
              <el-select v-model="form.singerName" placeholder="请选择">
                <el-option
                  v-for="(item, index) in singerOptions"
                  :key="index"
                  :label="item.singerName"
                  :value="item.singerName"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :xs="24" :md="24" :lg="24">
            <el-form-item label="歌曲名" :label-width="formLabelWidth" prop="songName">
              <el-select v-model="form.songId" placeholder="请选择">
                <el-option
                  v-for="item in songOptions"
                  :key="item.id"
                  :label="item.songName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="24">
            <!-- 上传视频 -->
            <el-form-item label="上传视频" v-show="isShowUpload">
              <el-upload
                ref="upload"
                :auto-upload="false"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-exceed="handleUploadExceed"
                :before-remove="handleBeforeRemove"
                :on-remove="handleOnRemove"
                :file-list="fileList"
                :limit="1"
                :action="url">
                <el-button slot="trigger" size="small" type="primary">选择视频</el-button>
                <el-button
                  :disabled="uploadBtnDisabled"
                  style="margin-left: 10px;"
                  size="small"
                  type="success"
                  @click="submitUpload()">上传</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelOper">取 消</el-button>
        <el-button :disabled="uploadBtnDisabled" type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMvList,
  addMv,
  editMv,
  deleteMv,
  changeMvStatus,
  removeByVodId,
  changeVideo
} from '@/api/musicVideo'
import { getAllSinger } from '@/api/singer'
import { getSongList } from '@/api/song'
export default {
  name: 'MV',
  filters: {
    statusFilter(status) {
      if (status === 1) {
        status = 'success'
      } else {
        status = 'danger'
      }
      const statusMap = {
        success: 'success',
        danger: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      loading: true,
      keyword: '', // 搜索关键字
      tableData: [], // 表格数据
      tempData: [], // 用于存放搜索的临时信息
      currentPage: 1, // 当前页数
      pageSize: 5, // 每页显示的数目
      title: '', // 对话框标题
      dialogFormVisible: false, // 控制弹出框
      isShowUpload: false, // 是否显示上传组件
      multipleSelection: [], // 存储多选框选中的行数
      formLabelWidth: '100px', // 表单标签宽度
      fileList: [], // 上传文件列表
      singerOptions: [], // 存放歌手信息
      songOptions: [], // 存放歌曲信息
      form: {}, // 表单数据
      uploadBtnDisabled: false,
      url: process.env.VUE_APP_URL + '/music/vod/media/upload', // 上传地址
      // 表单校验规则
      rules: { // 表单校验规则
        singerName: [
          { required: true, message: '歌手名不能为空', trigger: 'blur' }
        ],
        songId: [
          { required: true, message: '歌曲名不能为空', trigger: 'blur' }
        ],
        title: [
          { required: true, message: '标题不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.GetMvList()
    this.GetAllSinger()
    this.GetSongList()
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
        this.tableData = this.tempData
      } else {
        this.tableData = []
        for (const item of this.tempData) {
          console.log(item)
          if (item.title.toLowerCase().includes(this.keyword.toLowerCase())) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  methods: {
    // 上传多于一个视频
    handleUploadExceed() {
      this.$message.warning('想要重新上传视频，请先删除已上传的视频')
    },
    // 上传视频提交
    submitUpload() {
      this.uploadBtnDisabled = true // 禁用按钮
      this.$refs.upload.submit() // 提交上传请求
    },
    // 视频上传成功的回调
    handleUploadSuccess(response, file) {
      this.uploadBtnDisabled = false
      const data = this.form
      if (response.success) {
        this.form.sourceId = response.data.sourceId
        this.form.originalName = file.name
        editMv(data)
      } else {
        this.$message.error('上传失败(非20000)')
      }
    },
    // 失败回调
    handleUploadError() {
      this.uploadBtnDisabled = false
      this.$message.error('上传失败(http)')
    },
    // 删除视频文件确认
    handleBeforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },

    // 执行视频文件的删除
    handleOnRemove(file, fileList) {
      const data = {
        id: this.form.id,
        sourceId: this.form.sourceId,
        originalName: this.form.originalName
      }
      if (!data.sourceId) {
        return
      }
      removeByVodId(data.sourceId).then(response => {
        data.sourceId = ''
        data.originalName = ''
        // 删除视频，同时更新数据库中信息
        changeVideo(data)
        this.$message.success(response.message)
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
      this.fileList = [] // 清空文件列表
    },
    // 获得多选框选中的行数
    handleSelectionChange(val) {
      this.multipleSelection = val // 存入选中的行数
    },
    // 关闭对话框
    handleClose() {
      this.dialogFormVisible = false
    },
    // 改变歌曲状态
    statusChange(row) {
      /*
      *   change
      *     0: 下架
      *     1: 上架
      * */
      // 得到当前歌曲id以及状态值
      const song = {
        id: row.id,
        status: row.status
      }
      // 改变数据库中状态值
      changeMvStatus(song).then(resp => {
        if (resp.code === 20000) {
          if (resp.message === '上架成功') {
            this.$message({
              type: 'success',
              message: resp.message
            })
          } else {
            this.$message({
              type: 'error',
              message: resp.message
            })
          }
        }
      })
    },
    // 获取MV全部信息
    GetMvList() {
      getMvList().then(resp => {
        this.loading = false
        this.tableData = resp.data.mvInfo
        this.tempData = resp.data.mvInfo
      })
    },
    // 获取所有歌手信息
    GetAllSinger() {
      getAllSinger().then(resp => {
        console.log(resp.data)
        this.singerOptions = resp.data.singerInfo
      })
    },
    // 获取所有歌曲信息
    GetSongList() {
      getSongList().then(resp => {
        this.songOptions = resp.data.songInfo
      })
    },
    // 添加
    handleAdd(row) {
      this.dialogFormVisible = true // 打开对话框
      this.title = '增加标签' // 设置对话框标题
      this.form = {} // 重置表单中的数据，防止回显
      this.isEditForm = false // 声明不是修改操作
      this.isShowUpload = false // 添加功能不显示上传组件
    },
    // 编辑
    handleEdit(row) {
      this.title = '编辑标签'
      this.dialogFormVisible = true // 打开对话框
      this.isEditForm = true // 声明是修改操作
      this.form = row // 把当前行数据存入form表单信息中
      if (row.sourceId) {
        this.fileList = [{ 'name': row.originalName }]
      }
      console.log(row)
      this.isShowUpload = true // 修改功能显示上传组件
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该MV信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMv(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          // 重新渲染表格
          this.GetMvList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) { // 校验通过
          const data = this.form
          if (this.isEditForm) { // 修改操作
            editMv(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
              }
            })
          } else { // 添加操作
            console.log(data)
            addMv(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
                this.GetMvList() // 重新渲染表格
              }
            })
          }
        } else {
          console.log('校验错误!!!')
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
  .pagination {
    display: flex;
    justify-content: center;
  }
</style>
