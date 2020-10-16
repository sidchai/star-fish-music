<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input
        v-model="keyword"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请输入角色名称"
      />
      <el-button v-permission="'/role/get-list'" class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button v-permission="'/role/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加角色</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%">
      <el-table-column type="selection" />
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="角色名称" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.roleName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="角色介绍" width="300" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.introduce }}</span>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <template v-if="scope.row.status == 1">
            <el-tag type="success">正常</el-tag>
          </template>
          <template v-if="scope.row.status == 0">
            <el-tag type="danger">封禁</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="150">
        <template slot-scope="scope">
          <el-button v-permission="'/role/edit'" type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button v-permission="'/role/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
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
    <el-dialog :title="title" :visible.sync="dialogFormVisible">
      <el-form ref="form" :model="form" :rules="rules">
        <el-form-item label="角色名称" :label-width="formLabelWidth" prop="roleName">
          <el-input v-model="form.roleName" auto-complete="off" />
        </el-form-item>

        <el-form-item label="角色介绍" :label-width="formLabelWidth">
          <el-input v-model="form.introduce" auto-complete="off" />
        </el-form-item>

        <el-form-item label="访问菜单" :label-width="formLabelWidth">
          <el-tree
            ref="tree"
            :data="categoryMenuList"
            show-checkbox
            node-key="id"
            :props="defaultProps"
            :default-checked-keys="form.categoryMenuIds"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRoleList, addRole, editRole, deleteRole, getRoleListByKeyword } from '@/api/role'

import { getAllMenu } from '@/api/categoryMenu'
export default {
  name: 'Role',
  data() {
    return {
      loading: true,
      tableData: [],
      keyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0, // 总数量
      title: '增加角色',
      dialogFormVisible: false, // 控制弹出框
      formLabelWidth: '120px',
      isEditForm: false,
      form: {
        id: null,
        roleName: '',
        introduce: '',
        categoryMenuIds: []
      },
      // 分类菜单列表
      categoryMenuList: [],
      // tree配置项
      defaultProps: {
        children: 'childCategoryMenu',
        label: 'menuName'
      },
      rules: {
        roleName: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符' }
        ]
      }
    }
  },
  watch: {
    'form': {
      handler(newVal, oldVal) {
        console.log('value changed ', newVal, oldVal)
      },
      deep: true
    }
  },
  created() {
    this.allMenuList()
    this.roleList()
  },
  methods: {
    allMenuList() {
      getAllMenu().then(resp => {
        console.log(resp)
        if (resp.code === 20000) {
          const data = resp.data.menuList
          this.categoryMenuList = data
        }
      })
    },
    // 查询角色
    handleFind() {
      this.GetRoleListByKeyword()
    },
    roleList() {
      const currentPage = this.currentPage
      const pageSize = this.pageSize

      getRoleList(currentPage, pageSize).then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          const data = resp.data.roleList.records
          // 初始化菜单ID
          /* for (let a = 0; a < data.length; a++) {
            if (data[a].categoryMenuIds) {
              data[a].categoryMenuIds = eval('(' + data[a].categoryMenuIds + ')')
              console.log(data[a].categoryMenuIds)
            } else {
              data[a].categoryMenuIds = []
            }
          }*/
          this.tableData = data
          this.currentPage = resp.data.roleList.current
          this.pageSize = resp.data.roleList.size
          this.total = resp.data.roleList.total
        }
      })
    },
    GetRoleListByKeyword() {
      const currentPage = this.currentPage
      const pageSize = this.pageSize
      const keyword = this.keyword
      getRoleListByKeyword(currentPage, pageSize, keyword).then(resp => {
        if (resp.code === 20000) {
          const data = resp.data.roleList.records
          // 初始化菜单ID
          for (let a = 0; a < data.length; a++) {
            if (data[a].categoryMenuIds) {
              console.log(data[a].categoryMenuIds)
            } else {
              data[a].categoryMenuIds = []
            }
            console.log(data[a])
          }
          this.tableData = data
          this.currentPage = resp.data.roleList.current
          this.pageSize = resp.data.roleList.size
          this.total = resp.data.roleList.total
        }
      })
    },
    getFormObject() {
      const formObject = {
        uid: null,
        roleName: null,
        introduce: null,
        categoryMenuIds: []
      }
      return formObject
    },

    handleAdd() {
      this.dialogFormVisible = true
      this.title = '增加角色'
      this.form = this.getFormObject()
      setTimeout(() => {
        this.$refs.tree.setCheckedKeys(this.form.categoryMenuIds)
      }, 100)
      this.isEditForm = false
    },

    handleEdit(row) {
      this.dialogFormVisible = true
      this.isEditForm = true
      this.title = '修改角色'
      this.form = row
      setTimeout(() => {
        this.$refs.tree.setCheckedKeys(this.form.categoryMenuIds)
      }, 100)
    },

    handleDelete(row) {
      const that = this
      this.$confirm('此操作将把该角色删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteRole(row.id).then(resp => {
            if (resp.code === 20000) {
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
            that.roleList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.roleList()
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          console.log('校验出错')
        } else {
          // 得到选中树的ID
          const categoryMenuIds = this.$refs.tree.getCheckedKeys()
          console.log('全选ID', categoryMenuIds)

          this.form.categoryMenuIds = JSON.stringify(categoryMenuIds)
          if (this.isEditForm) {
            console.log('form', this.form)
            editRole(this.form).then(resp => {
              console.log(resp)
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false
                this.roleList()
              } else {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
              }
            })
          } else {
            addRole(this.form).then(resp => {
              console.log(resp)
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false
                this.roleList()
              } else {
                this.$message({
                  type: 'error',
                  message: resp.message
                })
              }
            })
          }
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
