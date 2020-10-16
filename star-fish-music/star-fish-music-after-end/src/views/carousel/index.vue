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
            placeholder="请输入轮播图标题"
          />
        </el-col>
        <el-col :xs="24" :sm="17" :md="17" :lg="18">
          <el-button v-permission="'/carousel/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加</el-button>
          <el-button v-permission="'/carousel/delete-all'" class="filter-item" type="danger" icon="el-icon-delete" @click="batchDelete">批量删除</el-button>
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

      <el-table-column label="轮播图图片" width="180" align="center">
        <template slot-scope="scope">
          <div style="width: 180px; height: 180px; overflow: hidden">
            <img
              v-if="scope.row.pic"
              :src="scope.row.pic"
              style="width: 100%"
            >
          </div>
        </template>
      </el-table-column>

      <el-table-column label="轮播图标题" width="300" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
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
          <el-button v-permission="'/carousel/edit'" type="primary" size="small" @click="handleEdit(scope.row)">编辑
          </el-button>
          <el-button v-permission="'/carousel/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除
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
        <el-form-item label="轮播图图片" :label-width="formLabelWidth">
          <upload
            v-show="isShowUpload"
            :action="avatarUrl"
            :src="form.pic"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-error="handleAvatarError"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">

            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" />
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
  getCarouselAll,
  addCarousel,
  editCarousel,
  deleteBatchCarousel,
  deleteCarousel,
  changeCarouselStatus,
  addOrChangeCarouselAvatar
} from '@/api/carousel'

export default {
  name: 'Carousel',
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
      tempDate: [], // 用于存放搜索的临时信息
      currentPage: 1, // 当前页数
      pageSize: 5, // 每页显示的数目
      title: '', // 对话框标题
      dialogFormVisible: false, // 控制弹出框
      isEditForm: false, // 是否是修改选项
      isShowUpload: false, // 是否显示上传组件
      form: {}, // 表单数据
      multipleSelection: [], // 存储多选框选中的行数
      formLabelWidth: '100px', // 表单标签宽度
      avatarUrl: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/carouselPic', // 上传地址
      rules: { // 表单校验规则
        title: [
          { required: true, message: '轮播图标题不能为空', trigger: 'blur' }
        ]
      }
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
      if (this.keyword === '') {
        this.tableData = this.tempDate
      } else {
        this.tableData = []
        for (const item of this.tempDate) {
          if (item.title.toLowerCase().includes(this.keyword.toLowerCase())) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  created() {
    // 初始化数据
    this.GetCarouselAll()
  },
  methods: {
    // 得到所有轮播图信息
    GetCarouselAll() {
      getCarouselAll().then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.carouselInfo
          this.tempDate = resp.data.carouselInfo
        }
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
      this.isShowUpload = true // 修改功能显示上传组件
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该歌单信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCarousel(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          // 重新渲染表格
          this.GetCarouselAll()
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
        deleteBatchCarousel(params).then(resp => {
          if (resp.code === 20000) { // 请求成功
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.GetCarouselAll() // 重新渲染表格
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
      changeCarouselStatus(song).then(resp => {
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
          data.adminId = this.$store.getters.adminId
          if (this.isEditForm) { // 修改操作
            editCarousel(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
              }
            })
          } else { // 添加操作
            addCarousel(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
                this.GetCarouselAll() // 重新渲染表格
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
    // 文件上传成功
    handleAvatarSuccess(resp) {
      if (resp.success) {
        this.form.pic = resp.data.url
        const data = {
          id: this.form.id,
          pic: this.form.pic
        }
        // 修改或添加数据库中信息
        addOrChangeCarouselAvatar(data).then(resp => {
        })
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    // 文件上传前的钩子函数，做一些判断
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 或 Gif 格式!')
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!')
      }
      return isJPG && isLt5M
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
    width: 180px;
    height: 180px;
  }
  .pagination {
    display: flex;
    justify-content: center;
  }
</style>
