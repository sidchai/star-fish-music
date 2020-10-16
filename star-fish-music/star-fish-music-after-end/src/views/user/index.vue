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
            clearable
            class="inline-input"
            style="width: 200px;"
            placeholder="请输入用户名">
            <i
              class="el-icon-edit el-input__icon"
              slot="suffix">
            </i>
          </el-input>
        </el-col>
      </el-row>
    </div>

    <el-table :data="data" style="width: 100%">

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="头像" width="120" align="center">
        <template slot-scope="scope">
          <img
            v-if="scope.row.avatar"
            :src="scope.row.avatar"
            style="width: 100px;height: 100px;"
          >
        </template>
      </el-table-column>

      <el-table-column label="用户名" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.username }}</span>
        </template>
      </el-table-column>

      <el-table-column label="性别" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.gender==1" type="success">男</el-tag>
          <el-tag v-if="scope.row.gender==0" type="danger">女</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="生日" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.birthday }}</span>
        </template>
      </el-table-column>

      <el-table-column label="个人介绍" width="150" align="center">
        <template slot-scope="{row}">
          <p style="overflow: hidden"> {{ row.introduce }}</p>
        </template>
      </el-table-column>

      <el-table-column label="地区" width="100" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.region }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="登录次数" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.loginCount }}</span>
        </template>
      </el-table-column>

      <el-table-column label="登录IP" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginIp }}</span>
        </template>
      </el-table-column>

      <el-table-column label="浏览器" width="160" align="center">
        <template slot-scope="scope">
          <span> {{ scope.row.browser }} </span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="200" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            style="display: block"
            active-color="#ff4949"
            inactive-color="#13ce66"
            active-text="封禁"
            :active-value="0"
            inactive-text="正常"
            :inactive-value="1"
            @change="statusChange(scope.row)"
          />
        </template>
      </el-table-column>

      <el-table-column label="评论状态" width="200" align="center">
        <template slot-scope="scope">
          <template v-if="scope.row.commentStatus == 1">
            <el-tag type="success">正常</el-tag>
          </template>
          <template v-if="scope.row.commentStatus == 0">
            <el-tag type="error">禁言</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)" v-permission="'/user/edit'">编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)" v-permission="'/user/delete'">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
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
        <el-form-item label="头像" :label-width="formLabelWidth">
          <upload
            :action="url"
            :src="form.avatar"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :on-error="handleAvatarError">
          </upload>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">

            <el-form-item label="用户名" :label-width="formLabelWidth" prop="username">
              <el-input v-model="form.username" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="昵称" :label-width="formLabelWidth">
              <el-input v-model="form.nickName" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="个人介绍" :label-width="formLabelWidth">
              <p style="overflow: hidden"> {{ form.introduce }}</p>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
              <el-radio
                v-for="gender in genderList"
                :key="gender.id"
                v-model="form.gender"
                :label="gender.dictValue"
                border
                size="medium"
              >{{gender.dictLabel}}
              </el-radio>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="手机号" :label-width="formLabelWidth" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="QQ号码" :label-width="formLabelWidth" prop="qqNumber">
              <el-input v-model="form.qqNumber" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="24" :lg="12">
            <el-form-item label="职业" :label-width="formLabelWidth">
              <el-input v-model="form.occupation" />
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
import Upload from '@/components/upload'
import { getListByDictType } from '@/api/sysDict'
import {
  getAllUser,
  deleteUser,
  changeStatus,
  addOrChangeUserAvatar,
  editUser,
  changeCommentStatus
} from '@/api/user'
import { addRecycle } from '@/api/recycle'

export default {
  name: 'User',
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
      tableData: [], // 用于显示用户信息
      tempData: [], // 用于存放搜索的临时信息
      loading: true, // 搜索框加载状态
      keyword: '',
      currentPage: 1, // 当前页
      pageSize: 5, // 每页显示条数
      title: '', // 对话框标题
      form: {}, // 表格数据
      dialogFormVisible: false, // 控制弹出框
      genderDefaultValue: '', // 设置性别默认值
      formLabelWidth: '100px', // 表单标签宽度
      genderList: [], // 性别字典
      url: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/userPic', // 上传地址
      rules: { // 表单校验规则
        username: [
          { required: true, message: '用户名不能为空', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在3到20个字符' }
        ],
        dictValue: [
          { required: true, message: '字典键值不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符' }
        ],
        gender: [
          { required: true, message: '性别不能为空', trigger: 'blur' }
        ],
        email: [
          { pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱' }
        ],
        phone: [
          { pattern: /0?(13|14|15|18)[0-9]{9}/, message: '请输入正确的手机号码' }
        ],
        qqNumber: [
          { pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码' }
        ]
      }
    }
  },
  computed: {
    data() { // 计算表格数据
      return this.tableData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  watch: {
    keyword() { // 进行搜索功能
      if (this.keyword === '') {
        this.tableData = this.tempData
      } else {
        this.tableData = []
        for (const item of this.tempData) {
          if (item.username.includes(this.keyword)) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  created() { // 初始化数据
    this.GetUserList()
    this.getDictList()
  },
  methods: {
    GetUserList() { // 获取所有用户信息
      this.tableData = []
      this.tempData = []
      getAllUser().then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.userInfo
          this.tempData = resp.data.userInfo
        }
      })
    },
    getDictList() { // 字典查询
      const dictType = 'sys_user_sex'
      getListByDictType(dictType).then(resp => {
        if (resp.code === 20000) {
          const data = resp.data.dictList
          this.genderList = data
          this.genderDefaultValue = data[0].dictValue
        }
      })
    },
    handleDelete(row) { // 删除操作
      this.$confirm('此操作会将该用户删除,删除后可通过回收站查看, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$prompt('请输入删除原因', '原因', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          const recycle = {
            userId: row.id,
            cause: value
          }
          // 删除用户信息
          deleteUser(row.id).then(resp => {
            if (resp.code === 20000) {
              this.$message({
                type: 'success',
                message: resp.message
              })
            }
            this.GetUserList() // 重新渲染表格
          }).catch(error => {
            console.log('deleteAdmin', error)
          })
          // 把删除的用户信息加入到回收站中
          addRecycle(recycle).then(resp => {
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消输入'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleCurrentChange(val) { // 分页
      this.currentPage = val
    },
    // 修改
    handleEdit(row) {
      this.dialogFormVisible = true // 打开对话框
      this.title = '修改用户信息'
      this.form = row
    },
    // 文件上传失败(http)
    handleAvatarError() {
      this.$message.error('上传失败!（http失败）')
    },
    // 文件上传成功
    handleAvatarSuccess(resp) {
      if (resp.success) {
        this.form.avatar = resp.data.url
        const data = {
          id: this.form.id,
          avatar: this.form.avatar
        }
        // 修改数据库中信息
        addOrChangeUserAvatar(data)
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
    statusChange(row) { // 改变用户状态
      /*
      *   change
      *     0: 已封禁
      *     1: 正常
      * */
      // 得到当前用户id以及状态值
      const user = {
        id: row.id,
        status: row.status
      }
      // 改变数据库中状态值
      changeStatus(user).then(resp => {
        if (resp.code === 20000) {
          if (resp.message === '解禁成功') {
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
    // 改变评论状态
    ChangeCommentStatus(row) {
      /*
      *   change
      *     0: 禁言
      *     1: 正常
      * */
      // 得到当前用户id以及状态值
      const user = {
        id: row.id,
        commentStatus: row.commentStatus
      }
      changeCommentStatus(user).then(resp => {
        if (resp.code === 20000) {
          if (resp.message === '解禁成功') {
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
    // 提交表单
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const data = this.form
          editUser(data).then(resp => {
            if (resp.code === 20000) { // 请求成功
              this.$message({
                type: 'success',
                message: resp.message
              })
              this.dialogFormVisible = false
            }
          }).catch(error => {
            console.log('editAdmin', error)
          })
        } else {
          console.log('校验错误!!!')
          return false
        }
      })
    },
    handleClose() {
      this.dialogFormVisible = false
      this.form = {}
    },
    // 关闭对话框
    cancelOper() {
      this.dialogFormVisible = false
      this.form = this.getFormObject()
    }
  }
}
</script>

<style scoped>

</style>
