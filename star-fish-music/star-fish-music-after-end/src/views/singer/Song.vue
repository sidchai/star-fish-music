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
          <el-button v-permission="'/song/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加</el-button>
          <el-button v-permission="'/song/delete-all'" class="filter-item" type="danger" icon="el-icon-delete" @click="batchDelete">批量删除</el-button>
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

      <el-table-column label="专辑头像" width="110" align="center">
        <template slot-scope="scope">
          <div style="width: 100px; height: 100px; overflow: hidden">
            <img
              lazy
              v-if="scope.row.pic"
              :src="scope.row.pic"
              style="width: 100%"
            >
          </div>
          <div class="play" @click="setSongUrl(scope.row.songUrl, scope.row.songName)">
            <div v-if="toggle !== scope.row.songName">
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-play"></use>
              </svg>
            </div>
            <div v-else>
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-pause"></use>
              </svg>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="歌曲名" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.songName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="专辑名" width="200" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.introduction }}</span>
        </template>
      </el-table-column>

      <el-table-column label="歌词" width="650" align="center">
        <template slot-scope="scope">
          <ul style="height: 100px; overflow: scroll">
            <li v-for="(item, index) in parseLyric(scope.row.lyric)" :key="index">
              {{ item }}
            </li>
          </ul>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="200" align="center">
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

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button v-permission="'/song/edit'" type="primary" size="small" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-permission="'/song/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除
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
        <el-form-item label="专辑头像" :label-width="formLabelWidth">
          <upload
            v-show="isShowUpload"
            v-loading="loading"
            element-loading-text="拼命上传中"
            :action="avatarUrl"
            :src="form.pic"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-progress="onProgressAvatar"
            :on-error="handleAvatarError"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">

            <el-form-item label="歌曲名" :label-width="formLabelWidth" prop="songName">
              <el-input v-model="form.songName" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="专辑" :label-width="formLabelWidth" prop="introduction">
              <el-input v-model="form.introduction" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item
              label="歌手名"
              :label-width="formLabelWidth"
              prop="singerId"
            >
              <el-cascader
                v-model="form.singerId"
                :options="options"
                placeholder="请选择"
                :props="{ expandTrigger: 'hover' }"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="歌词" :label-width="formLabelWidth">
              <el-input v-model="form.lyric" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="24">
            <el-form-item :label-width="formLabelWidth">
              <el-upload
                v-show="isShowUpload"
                v-loading="loadingSong"
                element-loading-text="拼命上传中"
                class="upload-demo change"
                :action="songUrl"
                :show-file-list="false"
                :on-success="handleSongSuccess"
                :on-error="handleAvatarError"
                :on-progress="onProgressSong"
                :before-upload="beforeSongUpload"
              >
                <el-button type="primary">更新歌曲<i class="el-icon-upload el-icon--right"></i></el-button>
              </el-upload>
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
import Upload from '@/components/upload/index'
import {
  getSongList,
  addSong,
  editSong,
  deleteBatchSong,
  deleteSong,
  changeSongStatus,
  addOrChangeSongAvatar,
  addOrChangeSongUrl
} from '@/api/song'
import { getAllSinger } from '@/api/singer'
import { getClassifyBySinger } from '@/api/classify'
import { mapGetters } from 'vuex'
import '@/assets/js/iconfont'
export default {
  name: 'Song',
  components: {
    Upload
  },
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
      singerOptions: [], // 歌手选中框数据
      currentPage: 1, // 当前页数
      pageSize: 5, // 每页显示的数目
      total: 0, // 数据总数
      title: '', // 对话框标题
      dialogFormVisible: false, // 控制弹出框
      isEditForm: false, // 是否是修改选项
      isShowUpload: false, // 是否显示上传组件
      toggle: false, // 控制播放图标状态
      form: {}, // 表单数据
      multipleSelection: [], // 存储多选框选中的行数
      formLabelWidth: '100px', // 表单标签宽度
      avatarUrl: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/introductionPic', // 上传地址
      songUrl: process.env.VUE_APP_URL + 'music/oss/file/upload?model=song', // 歌曲上传地址
      rules: { // 表单校验规则
        singerId: [
          { required: true, message: '歌手名不能为空', trigger: 'blur' }
        ],
        songName: [
          { required: true, message: '歌曲名不能为空', trigger: 'blur' }
        ],
        introduction: [
          { required: true, message: '专辑不能为空', trigger: 'blur' }
        ]
      },
      options: [], // 级联选择器数据
      loadingSong: false
    }
  },
  computed: {
    ...mapGetters([
      'isPlay', // 播放状态
      'url' // 歌曲url
    ]),
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
          if (item.songName.toLowerCase().includes(this.keyword.toLowerCase())) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  created() {
    // 初始化数据
    this.GetSongList()
    this.singerList()
    this.GetClassifyBySinger()
  },
  methods: {
    // 得到所有歌曲信息
    GetSongList() {
      getSongList().then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.songInfo
          this.tempData = resp.data.songInfo
        }
      })
    },
    // 得到所有歌手信息
    singerList() {
      getAllSinger().then(resp => {
        if (resp.code === 2000) {
          this.singerOptions = resp.data.singerInfo
        }
      })
    },
    // 得到所有歌手分类和歌手信息
    GetClassifyBySinger() {
      getClassifyBySinger().then(resp => {
        if (resp.code === 20000) {
          const data = resp.data.classifySingerInfo
          const options = []
          for (let i = 0; i < data.length; i++) {
            const classify = {} // 歌手类型信息
            classify.label = data[i].name
            classify.value = data[i].id
            const singerData = data[i].singers
            const singerList = [] // 所有歌手信息
            for (let j = 0; j < singerData.length; j++) {
              const singer = {}
              singer.label = singerData[j].singerName
              singer.value = singerData[j].id
              singerList.push(singer)
            }
            classify.children = singerList
            options.push(classify)
          }
          this.options = options
        }
      })
    },
    // 设置歌曲
    setSongUrl(url, songName) {
      console.log(url)
      if (url === null) {
        return this.$message.error('因合作方要求，该资源占时下架>_<')
      }
      if (this.toggle !== false) {
        this.toggle = true
      }
      this.toggle = songName
      this.$store.commit('SET_URL', url)
      if (this.isPlay) {
        this.$store.commit('SET_IS_PLAY', false)
      } else {
        this.$store.commit('SET_IS_PLAY', true)
      }
    },
    // 添加
    handleAdd(row) {
      this.dialogFormVisible = true // 打开对话框
      this.title = '增加歌曲' // 设置对话框标题
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
      this.isShowUpload = true // 修改功能显示上传组件
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该歌曲信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSong(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          // 重新渲染表格
          this.GetSongList()
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
        deleteBatchSong(params).then(resp => {
          if (resp.code === 20000) { // 请求成功
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.GetSongList() // 重新渲染表格
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
      changeSongStatus(song).then(resp => {
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
          const singerId = data.singerId
          if (singerId.length > 0) {
            // 选取最后一个元素
            data.singerId = singerId[singerId.length - 1]
          } else {
            this.$message({
              type: 'error',
              message: '请选中歌手名'
            })
            return
          }
          if (this.isEditForm) { // 修改操作
            editSong(data).then(resp => {
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
            addSong(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
                this.GetSongList() // 重新渲染表格
              }
            })
          }
        } else {
          console.log('校验错误!!!')
          return false
        }
      })
    },
    // 文件上传失败(http)
    handleAvatarError() {
      this.$message.error('上传失败!（http失败）')
    },
    // 文件上传时的钩子
    onProgressAvatar() {
      this.loading = true
    },
    // 文件上传成功
    handleAvatarSuccess(resp) {
      if (resp.success) {
        this.loading = false
        this.form.pic = resp.data.url
        const data = {
          id: this.form.id,
          pic: this.form.pic
        }
        // 修改或添加数据库中信息
        addOrChangeSongAvatar(data).then(resp => {
        })
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    // 文件上传前的钩子函数，做一些判断
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt3M = file.size / 1024 / 1024 < 3

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt3M) {
        this.$message.error('上传图片大小不能超过 3MB!')
      }
      return isJPG && isLt3M
    },
    // 文件上传时的钩子
    onProgressSong() {
      this.loadingSong = true
    },
    // 文件上传成功
    handleSongSuccess(resp) {
      if (resp.success) {
        this.loadingSong = false
        this.form.songUrl = resp.data.url
        const data = {
          id: this.form.id,
          songUrl: this.form.songUrl
        }
        // 修改或添加数据库中信息
        addOrChangeSongUrl(data).then(resp => {
          if (resp.code === 2000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
        })
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    beforeSongUpload(file) {
      const isCorrect = file.type === 'audio/flac' || file.type === 'audio/mpeg' || file.type === 'audio/ogg'
      const isLt50M = file.size / 1024 / 1024 < 50

      if (!isCorrect) {
        this.$message.error('上传歌曲文件只能是 mp3 或 flac 或 ogg 格式!')
      }
      if (!isLt50M) {
        this.$message.error('上传歌曲文件大小不能超过 50MB!')
      }
      return isCorrect && isLt50M
    },
    // 解析歌词
    parseLyric(text) {
      const lines = text.split('\n')
      const pattern = /\[\d{2}:\d{2}.(\d{3}|\d{2})\]/g
      const result = []

      // 对于歌词格式不对的特殊处理
      if (!(/\[.+\]/.test(text))) {
        return [text]
      }
      for (const item of lines) {
        if (pattern.test(item)) {
          const value = item.replace(pattern, '') // 存歌词
          result.push(value)
        }
      }
      return result
    }
  }
}
</script>

<style scoped>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    margin-bottom: 10px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409eff;
  }
  .uploadImgBody :hover {
    border: dashed 1px #00ccff;
  }
  img {
    width: 100px;
    height: 100px;
  }
  .play {
    position: absolute;
    z-index: 100;
    width: 100px;
    height: 100px;
    top: 26px;
    left: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
  .icon {
    width: 2em;
    height: 2em;
    color: white;
    fill: currentColor;
    overflow: hidden;
  }
  .pagination {
    display: flex;
    justify-content: center;
  }
  li {
    list-style: none;
  }
</style>
