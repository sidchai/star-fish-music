<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div v-permission="'/sys-dict/get-list'" class="filter-container" style="margin: 10px 0 10px 0;">

      <el-select
        v-model="dictTypeId"
        placeholder="请选择字典名称"
        style="width:150px"
      >
        <el-option
          v-for="item in dictTypeList"
          :key="item.id"
          :label="item.dictName"
          :value="item.id"
        />
      </el-select>

      <el-input
        v-model="dictLabelQuery"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请输入字典标签"
        @keyup.enter.native="handleFind"
      />

      <el-button v-permission="'/sys-dict/getList'" class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button v-permission="'/sys-dict/add'" class="filter-item" type="primary" icon="el-icon-edit" @click="handleAdd">添加</el-button>
      <el-button v-permission="'/sys-dict/delete-batch'" class="filter-item" type="danger" icon="el-icon-delete" @click="handleDeleteBatch">删除选中</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" />

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="字典标签" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.dictLabel }}</span>
        </template>
      </el-table-column>

      <el-table-column label="字典键值" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.$index%5 === 0" type="warning">{{ scope.row.dictValue }}</el-tag>
          <el-tag v-if="scope.$index%5 === 1" type="success">{{ scope.row.dictValue }}</el-tag>
          <el-tag v-if="scope.$index%5 === 2" type="info">{{ scope.row.dictValue }}</el-tag>
          <el-tag v-if="scope.$index%5 === 3" type="danger">{{ scope.row.dictValue }}</el-tag>
          <el-tag v-if="scope.$index%5 === 4">{{ scope.row.dictValue }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="字典类型" width="200" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.sysDictType.dictType }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="是否默认" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isDefault === 1" type="success">是</el-tag>
          <el-tag v-if="scope.row.isDefault === 0" type="warning">否</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="回显样式" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.listClass === 'default'">{{ scope.row.listClass }}</el-tag>
          <el-tag v-if="scope.row.listClass === 'success'" type="success">{{ scope.row.listClass }}</el-tag>
          <el-tag v-if="scope.row.listClass === 'primary'" type="primary">{{ scope.row.listClass }}</el-tag>
          <el-tag v-if="scope.row.listClass === 'info'" type="info">{{ scope.row.listClass }}</el-tag>
          <el-tag v-if="scope.row.listClass === 'warning'" type="warning">{{ scope.row.listClass }}</el-tag>
          <el-tag v-if="scope.row.listClass === 'danger'" type="danger">{{ scope.row.listClass }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="排序" width="50" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.sort }}</span>
        </template>
      </el-table-column>

      <el-table-column label="备注" width="200" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.remake }}</span>
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
            <span>正常</span>
          </template>
          <template v-if="scope.row.status == 0">
            <span>已删除</span>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="240">
        <template slot-scope="scope">
          <el-button v-permission="'/sys-dict/edit'" type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button v-permission="'/sys-dict/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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

        <el-form-item label="字典标签" :label-width="formLabelWidth" prop="dictLabel">
          <el-input v-model="form.dictLabel" auto-complete="off" />
        </el-form-item>

        <el-form-item label="字典键值" :label-width="formLabelWidth" prop="dictValue">
          <el-input v-model="form.dictValue" auto-complete="off" />
        </el-form-item>

        <el-form-item label="字典类型" :label-width="formLabelWidth" prop="dictTypeId">
          <el-select
            v-model="form.dictTypeId"
            disabled
            placeholder="请选择字典类型"
            style="width:200px"
          >
            <el-option
              v-for="item in dictTypeList"
              :key="item.id"
              :label="item.dictType"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="回显样式" :label-width="formLabelWidth">
          <el-select v-model="form.listClass" clearable placeholder="推荐等级" style="width:140px">
            <el-option
              v-for="item in listClassType"
              :key="item.key"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
          <el-input v-model="form.sort" auto-complete="off" />
        </el-form-item>

        <el-form-item label="备注" :label-width="formLabelWidth">
          <el-input v-model="form.remake" auto-complete="off" />
        </el-form-item>

        <el-form-item label="系统默认" :label-width="formLabelWidth" prop="isDefault">
          <el-radio-group v-model="form.isDefault" size="small">
            <el-radio :label="1" border>是</el-radio>
            <el-radio :label="0" border>否</el-radio>
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
import { getSysDictTypeList } from '@/api/sysDict/sysDictType'
import { addSysDict, editSysDict, deleteBatchSysDict, deleteSysDict, getSysDictList } from '@/api/sysDict'

export default { // 字典数据
  name: 'SysDictData',
  data() {
    return {
      dictTypeId: null, // 从SysDictType传递过来的
      dictTypeMap: {}, // DictType对象集合
      dictLabelQuery: '', // 字典标签查询
      multipleSelection: [], // 用于存放多选框选中的数据
      dictTypeList: [],
      tableData: [],
      keyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0, // 总数量
      title: '增加字典数据',
      dialogFormVisible: false, // 控制弹出框
      formLabelWidth: '120px',
      isEditForm: false,
      form: {},
      listClassType: [
        { key: 1, label: 'default', value: 'default' },
        { key: 2, label: 'primary', value: 'primary' },
        { key: 3, label: 'success', value: 'success' },
        { key: 4, label: 'info', value: 'info' },
        { key: 5, label: 'warning', value: 'warning' },
        { key: 6, label: 'danger', value: 'danger' }
      ],
      rules: {
        dictLabel: [
          { required: true, message: '字典标签不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符' }
        ],
        dictValue: [
          { required: true, message: '字典键值不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符' }
        ],
        dictTypeId: [
          { required: true, message: '字典类型不能为空', trigger: 'blur' }
        ],
        isDefault: [
          { required: true, message: '系统默认不能为空', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '排序字段不能为空', trigger: 'blur' },
          { pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数' }
        ]
      }
    }
  },
  created() {
    // 传递过来的dictTypeId
    this.dictTypeId = this.$route.query.dictTypeId
    this.sysDictTypeList()
    this.sysDictDataList()
  },
  methods: {
    sysDictDataList() {
      const params = {}
      params.dictTypeId = this.dictTypeId
      params.dictLabel = this.dictLabelQuery
      params.currentPage = this.currentPage
      params.pageSize = this.pageSize
      getSysDictList(params).then(resp => {
        if (resp.code === 20000) {
          this.tableData = resp.data.dictListAll.records
          this.currentPage = resp.data.dictListAll.current
          this.pageSize = resp.data.dictListAll.size
          this.total = resp.data.dictListAll.total
        }
      })
    },
    sysDictTypeList() {
      const params = {}
      params.keyword = ''
      params.currentPage = 0
      params.pageSize = 100
      getSysDictTypeList(params).then(resp => {
        if (resp.code === 20000) {
          this.dictTypeList = resp.data.dictTypeListAll.records
        }
      })
    },
    // 这里可以设置一些初始值
    getFormObject() {
      const formObject = {
        isPublish: '1',
        sort: 0,
        isDefault: 0,
        listClass: ''
      }
      return formObject
    },
    handleFind() {
      this.sysDictDataList()
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = this.getFormObject()
      this.form.dictTypeId = this.dictTypeId
      this.isEditForm = false
    },
    handleEdit(row) {
      this.title = '编辑字典数据'
      this.dialogFormVisible = true
      this.isEditForm = true
      this.form = row
      this.form.dictTypeId = this.dictTypeId
    },
    handleDelete(row) {
      const that = this
      this.$confirm('此操作将把字典数据删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const sysDictData = {}
          sysDictData.id = row.id
          const deleteList = []
          deleteList.push(sysDictData)
          deleteSysDict(deleteList).then(resp => {
            console.log(resp)
            this.$message({
              type: 'success',
              message: resp.data
            })
            that.sysDictDataList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleDeleteBatch(row) {
      const that = this
      const data = that.multipleSelection
      if (that.multipleSelection.length <= 0) {
        this.$message({
          type: 'error',
          message: '请先选中需要删除的内容！'
        })
        return
      }
      this.$confirm('此操作将把选中字典数据删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const ids = [] // 多个选中的id
          data.forEach(item => {
            ids.push(item.id)
          })
          const params = new URLSearchParams()
          params.append('ids', ids)
          deleteBatchSysDict(params).then(resp => {
            this.$message({
              type: 'success',
              message: resp.data
            })
            that.sysDictDataList()
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
      this.sysDictDataList()
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          console.log('校验出错')
        } else {
          if (this.isEditForm) {
            editSysDict(this.form).then(resp => {
              console.log(resp)
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.data
                })
                this.dialogFormVisible = false
                this.sysDictDataList()
              } else {
                this.$message({
                  type: 'success',
                  message: resp.data
                })
              }
            })
          } else {
            addSysDict(this.form).then(resp => {
              if (resp.code === 20000) {
                this.$message({
                  type: 'success',
                  message: resp.data
                })
                this.dialogFormVisible = false
                this.sysDictDataList()
              } else {
                this.$message({
                  type: 'error',
                  message: resp.data
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
