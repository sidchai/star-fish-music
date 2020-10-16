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
          <el-autocomplete
            v-model="keyword"
            clearable
            class="inline-input"
            value-key="singer_name"
            :fetch-suggestions="querySearch"
            :trigger-on-focus="false"
            @select="handleSelect"
            placeholder="请输入歌手名">
            <i
              class="el-icon-edit el-input__icon"
              slot="suffix">
            </i>
          </el-autocomplete>
        </el-col>
        <el-col :xs="24" :sm="17" :md="17" :lg="18">
          <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd" v-permission="'/singer/add'">添加</el-button>
          <el-button class="filter-item" type="danger" icon="el-icon-delete" @click="batchDelete" v-permission="'/singer/delete-all'">批量删除</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" />

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="歌手头像" width="120" align="center">
        <template slot-scope="scope">
          <img
            v-if="scope.row.pic"
            :src="scope.row.pic"
            style="width: 100px;height: 100px;"
          >
        </template>
      </el-table-column>

      <el-table-column label="歌手名" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.singerName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="出道时间" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.debutTime }}</span>
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

      <el-table-column label="地区" width="150" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.region }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="歌手介绍" width="600" align="center">
        <template slot-scope="scope">
          <p style="height: 100px; overflow: scroll">{{ scope.row.introduction }}</p>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)" v-permission="'/singer/edit'">编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)" v-permission="'/singer/delete'">删除
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
        :total="total"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" :before-close="handleClose">
      <el-form ref="form" :model="form" :rules="rules" :status-icon="true">
        <el-form-item label="歌手头像" :label-width="formLabelWidth">
          <upload
            v-show="isShowUpload"
            :action="url"
            :src="form.pic"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-error="handleAvatarError"
          >
          </upload>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">

            <el-form-item label="歌手名" :label-width="formLabelWidth" prop="singerName">
              <el-input v-model="form.singerName" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item
              label="排名"
              :label-width="formLabelWidth"
            >
              <el-input v-model="form.rank" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="出道时间" :label-width="formLabelWidth" prop="debutTime">
              <el-date-picker type="date" placeholder="选择出道时间" v-model="form.debutTime" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="地区" :label-width="formLabelWidth" prop="region">
              <el-input v-model="form.region"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="歌手介绍" :label-width="formLabelWidth">
              <el-input type="textarea" v-model="form.introduction"></el-input>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="分类名" :label-width="formLabelWidth" prop="classifyId">
              <el-select v-model="form.classifyId" placeholder="请选择">
                <el-option
                  v-for="item in classifyOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
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
  getNameListByKeyword,
  getSingerPage,
  getSingerPageByKeyword,
  addSinger,
  editSinger,
  deleteSinger,
  deleteSingers,
  changeSingerStatus,
  addOrChangeSingerPic
} from '@/api/singer'
import { getClassifyBySinger } from '@/api/classify'
import Upload from '@/components/upload'

export default {
  name: 'Singer',
  data() {
    return {
      loading: true,
      keyword: '', // 搜索关键字
      tableData: [], // 表格数据
      classifyOptions: [], // 存放歌手类型名信息
      currentPage: 1, // 当前页数
      pageSize: 5, // 每页显示的数目
      total: 0, // 数据总数
      title: '', // 对话框标题
      dialogFormVisible: false, // 控制弹出框
      isEditForm: false, // 是否是修改选项
      isShowUpload: false, // 是否显示上传组件
      form: {}, // 表单数据
      multipleSelection: [], // 存储多选框选中的行数
      formLabelWidth: '100px', // 表单标签宽度
      url: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/singerListPic', // 上传地址
      rules: { // 表单校验规则
        singerName: [
          { required: true, message: '歌手名不能为空', trigger: 'blur' }
        ],
        debutTime: [
          { required: true, message: '出道时间不能为空', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '地区不能为空', trigger: 'blur' }
        ],
        classifyId: [
          { required: true, message: '分类名不能为空', trigger: 'blur' }
        ]
      }
    }
  },
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
  methods: {
    // 获取所有歌手信息
    GetSingerList() {
      getSingerPage(this.currentPage, this.pageSize).then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.singerPageList.records
          this.currentPage = resp.data.singerPageList.current
          this.pageSize = resp.data.singerPageList.pages
          this.total = resp.data.singerPageList.total
        }
      })
    },
    // 获取歌手类型
    GetClassifyBySinger() {
      getClassifyBySinger().then(resp => {
        this.classifyOptions = resp.data.classifySingerInfo
      })
    },
    // 输入建议
    querySearch(queryString, cb) {
      getNameListByKeyword(queryString).then(resp => {
        console.log(resp.data.singerNameList)
        // 调用 callback 返回建议列表的数据
        cb(resp.data.singerNameList)
      })
    },
    // 检测输入框
    handleSelect(item) {
      getSingerPageByKeyword(this.currentPage, this.pageSize, item.singer_name).then(resp => {
        if (resp.code === 20000) {
          this.tableData = resp.data.singerPageListKeyword.records
          this.currentPage = resp.data.singerPageListKeyword.current
          this.pageSize = resp.data.singerPageListKeyword.pages
          this.total = resp.data.singerPageListKeyword.total
        }
      })
    },
    // 添加
    handleAdd(row) {
      this.dialogFormVisible = true // 打开对话框
      this.title = '增加歌手' // 设置对话框标题
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
      this.$confirm('此操作将删除该歌手信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteSinger(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          // 重新渲染表格
          this.GetSingerList()
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
      this.$confirm('此操作将删除选中歌手信息, 是否继续?', '提示', {
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
        deleteSingers(params).then(resp => {
          if (resp.code === 20000) { // 请求成功
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.GetSingerList() // 重新渲染表格
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
      this.GetSingerList()
    },
    // 改变歌手状态
    statusChange(row) {
      /*
      *   change
      *     0: 下架
      *     1: 上架
      * */
      // 得到当前用户id以及状态值
      const singer = {
        id: row.id,
        status: row.status
      }
      // 改变数据库中状态值
      changeSingerStatus(singer).then(resp => {
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
          if (this.isEditForm) { // 修改操作
            editSinger(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
              }
            })
          } else { // 添加操作
            addSinger(data).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({ // 提示信息
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false // 关闭对话框
                this.GetSingerList() // 重新渲染表格
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
        const data = this.form
        // 修改或添加数据库中信息
        addOrChangeSingerPic(data).then(resp => {
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
    }
  },
  created() {
    // 初始化数据
    this.GetSingerList()
    this.GetClassifyBySinger()
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
  .pagination {
    display: flex;
    justify-content: center;
  }
</style>
