<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" v-permission="'/category-menu/add'">添加菜单</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%">

      <el-table-column type="expand">
        <template slot-scope="scope">
          <el-form label-position="left" inline class="demo-table-expand">

            <el-table :data="scope.row.childCategoryMenu" :show-header="showHeader" style="width: 100%">
              <el-table-column label width="60" align="center">
                <template slot-scope="scope_child">
                  <span>{{scope_child.$index + 1}}</span>
                </template>
              </el-table-column>

              <el-table-column label width="150" align="center">
                <template slot-scope="scope_child">
                  <span>{{ scope_child.row.menuName }}</span>
                </template>
              </el-table-column>

              <el-table-column label width="100" align="center">
                <template slot-scope="scope_child">
                  <el-tag v-for="item in menuLevelDictList" :key="item.id" v-show="scope_child.row.menuLevel == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
                </template>
              </el-table-column>

              <el-table-column label width="200" align="center">
                <template slot-scope="scope_child">
                  <span>{{ scope_child.row.introduce }}</span>
                </template>
              </el-table-column>

              <el-table-column label width="100" align="center">
                <template slot-scope="scope_child">
                  <span>{{ scope_child.row.icon }}</span>
                </template>
              </el-table-column>

              <el-table-column label width="200" align="center">
                <template slot-scope="scope_child">
                  <el-tag>{{ scope_child.row.url }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column width="100" align="center">
                <template slot-scope="scope">
                  <el-tag v-for="item in yesNoDictList" :key="item.id" v-show="scope.row.isShow == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
                </template>
              </el-table-column>

              <el-table-column label width="160" align="center">
                <template slot-scope="scope_child">
                  <span>{{ scope_child.row.createTime }}</span>
                </template>
              </el-table-column>

              <el-table-column label width="100" align="center">
                <template slot-scope="scope_child">
                  <template v-if="scope_child.row.status == 1">
                    <el-tag type="success">正常</el-tag>
                  </template>
                  <template v-if="scope_child.row.status == 0">
                    <el-tag type="error">已删除</el-tag>
                  </template>
                </template>
              </el-table-column>

              <el-table-column fixed="right" min-width="230">
                <template slot-scope="scope_child">
                  <el-button @click="handleStick(scope_child.row)" type="warning" size="small" v-permission="'/category-menu/stick'">置顶</el-button>
                  <el-button @click="handleEdit(scope_child.row)" type="primary" size="small" v-permission="'/category-menu/edit'">编辑</el-button>
                  <el-button @click="handleDelete(scope_child.row)" type="danger" size="small" v-permission="'/category-menu/delete'">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </template>
      </el-table-column>
      <!-- 一级菜单 -->
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{scope.$index + 1}}</span>
        </template>
      </el-table-column>

      <el-table-column label="菜单名称" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.menuName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="菜单级别" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item in menuLevelDictList" :key="item.id" v-show="scope.row.menuLevel == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="菜单简介" width="200" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.introduce }}</span>
        </template>
      </el-table-column>

      <el-table-column label="图标" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.icon }}</span>
        </template>
      </el-table-column>

      <el-table-column label="路由" width="200" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.url }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="是否显示" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item in yesNoDictList" :key="item.id" v-show="scope.row.isShow == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
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
            <el-tag type="error">已删除</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="230">
        <template slot-scope="scope">
          <el-button @click="handleStick(scope.row)" type="warning" size="small" v-permission="'/category-menu/stick'">置顶</el-button>
          <el-button @click="handleEdit(scope.row)" type="primary" size="small" v-permission="'/category-menu/edit'">编辑</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="small" v-permission="'/category-menu/delete'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="菜单名称" :label-width="formLabelWidth" prop="menuName">
          <el-input v-model="form.menuName" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="菜单等级" :label-width="formLabelWidth" prop="menuLevel">
          <el-select v-model="form.menuLevel" placeholder="请选择">
            <el-option
              v-for="item in menuLevelDictList"
              :key="item.id"
              :label="item.dictLabel"
              v-show="item.dictValue != 3"
              :value="parseInt(item.dictValue)"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="form.menuLevel == 2"
          label="父菜单名"
          :label-width="formLabelWidth"
          prop="parentId"
        >
          <el-select
            v-model="form.parentId"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请输入父菜单名"
            :remote-method="remoteMethod"
            :loading="loading"
          >
            <el-option
              v-for="item in menuOptions"
              :key="item.id"
              :label="item.menuName"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="菜单介绍" :label-width="formLabelWidth" prop="introduce">
          <el-input v-model="form.introduce" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="图标" :label-width="formLabelWidth" prop="icon">
          <el-input v-model="form.icon" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="路由" :label-width="formLabelWidth" prop="url">
          <el-input v-model="form.url" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="是否显示" :label-width="formLabelWidth" prop="isShow">
          <el-radio-group v-model="form.isShow" size="small">
            <el-radio v-for="item in yesNoDictList" :key="item.id" :label="parseInt(item.dictValue)" border>{{item.dictLabel}}</el-radio>
          </el-radio-group>
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
import { getMenuList, getAllMenu, addMenu, editMenu, deleteMenu, stickMenu } from '@/api/categoryMenu'
import { getListByDictTypeList } from '@/api/sysDict'
export default {
  name: 'CategoryMenu',
  data() {
    return {
      loading: true,
      showHeader: false, // 是否显示表头
      tableData: [],
      keyword: '',
      menuLevel: '',
      currentPage: 1,
      pageSize: 10,
      total: 0, // 总数量
      title: '',
      dialogFormVisible: false, // 控制弹出框
      formLabelWidth: '120px',
      isEditForm: false,
      menuLevelDictList: [], // 菜单等级字典
      yesNoDictList: [], // 是否字典
      yesNoDefault: null,
      form: {
        id: null,
        menuName: '',
        introduce: '',
        icon: '',
        url: '',
        sort: ''
      },
      menuOptions: [], // 一级菜单候选项
      rules: {
        menuName: [
          { required: true, message: '菜单名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符' }
        ],
        menuLevel: [
          { required: true, message: '菜单等级不能为空', trigger: 'blur' }
        ],
        parentId: [
          { required: true, message: '父菜单名不能为空', trigger: 'blur' }
        ],
        introduce: [
          { required: true, message: '菜单简介不能为空', trigger: 'blur' }
        ],
        icon: [
          { required: true, message: '图标不能为空', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '路由不能为空', trigger: 'blur' }
        ],
        isShow: [
          { required: true, message: '显示字段不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getDictList()
    this.menuList()
  },
  methods: {
    menuList() {
      getAllMenu().then(resp => {
        console.log('getAllMenu', resp)
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.menuList
          this.menuOptions = resp.data.menuList
        }
      })
    },
    /**
     * 字典查询
     */
    getDictList() {
      const dictTypeList = ['sys_menu_level', 'sys_yes_no']

      getListByDictTypeList(dictTypeList).then(resp => {
        if (resp.code === 20000) {
          console.log(resp)
          const dictMap = resp.data.dictTypeList

          this.menuLevelDictList = dictMap.sys_menu_level.list

          this.yesNoDictList = dictMap.sys_yes_no.list

          if (dictMap.sys_yes_no.defaultValue) {
            this.yesNoDefault = parseInt(dictMap.sys_yes_no.defaultValue)
          }
        }
      })
    },
    getFormObject() {
      const formObject = {
        id: null,
        menuName: '',
        introduce: '',
        icon: '',
        url: '',
        sort: '',
        menuType: 0, // 菜单类型  菜单
        isShow: this.yesNoDefault
      }
      return formObject
    },
    handleFind() {
      this.menuList()
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.title = '增加分类'
      this.form = this.getFormObject()
      this.isEditForm = false
    },
    handleEdit(row) {
      this.dialogFormVisible = true
      this.isEditForm = true
      this.title = '修改分类'
      // eslint-disable-next-line no-unused-vars
      const parentId = row.parentId
      this.form = row
    },
    handleStick(row) {
      this.$confirm('此操作将会把该标签放到首位, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const params = {}
          params.id = row.id
          stickMenu(params).then(resp => {
            if (resp.code === 20000) {
              this.menuList()
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
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消置顶'
          })
        })
    },
    handleDelete(row) {
      const that = this
      this.$confirm('此操作将把分类删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const id = row.id
          deleteMenu(id).then(resp => {
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
            that.menuList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 分类远程搜索函数
    remoteMethod(query) {
      if (query !== '') {
        // 这里只搜索一级菜单出来
        const params = new URLSearchParams()
        params.append('keyword', query)
        params.append('menuLevel', 1)
        params.append('pageSize', 100)
        getMenuList(params).then(resp => {
          if (resp.code === 20000) {
            this.menuOptions = resp.data.data.records
          }
        })
      } else {
        this.menuOptions = []
      }
    },

    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          console.log('校验失败')
        } else {
          if (this.isEditForm) {
            editMenu(this.form).then(resp => {
              console.log(resp)
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false
                this.menuList()
              } else {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
              }
            })
          } else {
            addMenu(this.form).then(resp => {
              console.log(resp)
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.message
                })
                this.dialogFormVisible = false
                this.menuList()
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

</style>
