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
            placeholder="请输入管理员名">
            <i
              class="el-icon-edit el-input__icon"
              slot="suffix">
            </i>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="17" :md="17" :lg="18">
          <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd" v-permission="'/admin/add'">添加</el-button>
          <el-button class="filter-item" type="danger" icon="el-icon-delete" @click="batchDelete" v-permission="'/admin/delete-all'">批量删除</el-button>
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

      <el-table-column label="拥有角色" width="150" align="center">
        <template slot-scope="{row}">
          <el-tag v-if="row.role" :type="row.role.roleName | roleNameFilter">{{ row.role.roleName }}</el-tag>
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

      <el-table-column label="登录次数" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.loginCount }}</span>
        </template>
      </el-table-column>

      <el-table-column label="地区" width="100" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.region }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="登录IP" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginIp }}</span>
        </template>
      </el-table-column>

      <el-table-column label="最后登录时间" width="160" align="center">
        <template slot-scope="scope">
          <span> {{ scope.row.lastLoginTime }} </span>
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

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button type="warning" size="small" @click="handleRest(scope.row)" v-permission="'/admin/rest-pwd'">重置密码
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(scope.row)" v-permission="'/admin/edit'">编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)" v-permission="'/admin/delete'">删除
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
            v-show="isShowUpload"
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
            <el-form-item label="角色名" :label-width="formLabelWidth" prop="roleId">
              <el-select v-model="form.roleId" placeholder="请选择">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.id"
                  :label="item.roleName"
                  :value="item.id"
                />
              </el-select>
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
import { adminList,
  deleteAdmin,
  restPwd,
  deleteAdmins,
  editAdmin,
  addAdmin,
  changeStatus,
  addOrChangeAdminAvatar
} from '@/api/admin'
import { getRoleList } from '@/api/role'
import { getListByDictType } from '@/api/sysDict'
import { addRecycle } from '@/api/recycle'
import Upload from '@/components/upload'

export default {
  name: 'AdminListTable',
  components: {
    Upload
  },
  filters: {
    roleNameFilter(roleName) {
      if (roleName === '超级管理员') {
        roleName = 'danger'
      } else if (roleName === '一般管理员') {
        roleName = 'success'
      }
      return roleName
    }
  },
  data() {
    return {
      tableData: [], // 用于显示管理员信息
      tempData: [], // 用于存放搜索的临时信息
      loading: true, // 搜索框加载状态
      keyword: '',
      currentPage: 1, // 当前页
      pageSize: 5, // 每页显示条数
      total: 0, // 总数量
      title: '增加管理员',
      dialogFormVisible: false, // 控制弹出框
      isEditForm: false, // 是否为修改功能
      isShowUpload: false, // 是否显示上传组件
      genderDefaultValue: '', // 设置性别默认值
      form: {},
      multipleSelection: [], // 存储多选框选中的行数信息
      formLabelWidth: '100px', // 表单标签宽度
      roleOptions: [], // 角色选择框数据
      genderList: [], // 性别字典
      url: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/adminPic', // 上传地址
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
          { required: true, message: '邮箱不能为空', trigger: 'blur' },
          { pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱' }
        ],
        phone: [
          { pattern: /0?(13|14|15|18)[0-9]{9}/, message: '请输入正确的手机号码' }
        ],
        qqNumber: [
          { pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码' }
        ],
        roleId: [
          { required: true, message: '角色名不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() { // 初始化数据
    this.getAdminListPage()
    this.roleList()
    this.getDictList()
  },
  methods: {
    getAdminListPage() { // 获取管理员分页信息
      this.tableData = []
      this.tempData = []
      adminList().then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.adminList
          this.tempData = resp.data.adminList
          this.currentPage = 1
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
    getFormObject() {
      const formObject = {
        id: null,
        gender: this.genderDefaultValue
      }
      return formObject
    },
    handleAdd() { // 添加
      this.dialogFormVisible = true
      this.from = this.getFormObject()
      this.title = '增加管理员'
      this.isEditForm = false
      this.isShowUpload = false // 添加功能不显示上传组件
    },
    batchDelete() { // 批量删除管理员
      const data = this.multipleSelection
      console.log(data)
      if (data.length <= 0) {
        this.$message({
          showClose: true,
          message: '您还未选中，请选中后再删除',
          type: 'error'
        })
        return false
      }
      this.$confirm('此操作将删除选中管理员信息, 是否继续?', '提示', {
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
        deleteAdmins(params).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.getAdminListPage() // 重新渲染表格
        }).catch(error => {
          console.log('deleteAdmins', error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleRest(row) { // 重置密码
      this.$confirm('此操作会重置该管理员密码, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        restPwd(row.id).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
        }).catch(error => {
          console.log('restPwd', error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消重置'
        })
      })
    },
    handleEdit(row) { // 修改操作
      this.title = '编辑管理员'
      this.dialogFormVisible = true
      this.isEditForm = true
      this.form = row
      this.isShowUpload = true // 修改功能显示上传组件
    },
    handleDelete(row) { // 删除操作
      this.$confirm('此操作会将该管理员删除,删除后可通过回收站查看, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$prompt('请输入删除原因', '原因', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          const recycle = {
            adminId: row.id,
            cause: value
          }
          // 删除管理员信息
          deleteAdmin(row.id).then(resp => {
            if (resp.code === 20000) {
              this.$message({
                type: 'success',
                message: resp.message
              })
            }
            this.getAdminListPage() // 重新渲染表格
          }).catch(error => {
            console.log('deleteAdmin', error)
          })
          // 把删除的管理员信息加入到回收站中
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
        addOrChangeAdminAvatar(data)
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
    statusChange(row) { // 改变管理员状态
      /*
      *   change
      *     0: 已封禁
      *     1: 正常
      * */
      // 得到当前用户id以及状态值
      const admin = {
        id: row.id,
        status: row.status,
        roleId: row.roleId
      }
      // 改变数据库中状态值
      changeStatus(admin).then(resp => {
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
    handleSelectionChange(val) { // 存入选中的行数
      this.multipleSelection = val
    },
    handleClose() {
      this.dialogFormVisible = false
      this.form = this.getFormObject()
    },
    submitForm() { // 提交表单
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.isEditForm) { // 修改操作
            const data = this.form
            console.log(data)
            /* if (data.status) { // 把状态值进行转换传回后端
              data.status = 0
            } else {
              data.status = 1
            }*/
            editAdmin(data).then(resp => {
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
          } else { // 添加操作
            addAdmin(this.form).then(resp => {
              if (resp.code === 20000) { // 请求成功
                this.$message({
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false
                this.getAdminListPage()
              }
            }).catch(error => {
              console.log('addAdmin', error)
            })
          }
        } else {
          console.log('校验错误!!!')
          return false
        }
      })
    },
    cancelOper() {
      this.dialogFormVisible = false
      this.form = this.getFormObject()
    },
    roleList() { // 得到角色列表
      const currentPage = 1
      const pageSize = 5
      getRoleList(currentPage, pageSize).then(resp => {
        this.roleOptions = resp.data.roleList.records
      })
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
  computed: {
    data() { // 计算表格数据
      return this.tableData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
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
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.imgBody {
  width: 100px;
  height: 100px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}
.uploadImgBody {
  margin-left: 5px;
  width: 100px;
  height: 100px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
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
