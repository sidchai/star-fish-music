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
            <template slot-scope="{ item }">
              <div class="name">{{ item.username }}</div>
            </template>
          </el-input>
        </el-col>
      </el-row>
    </div>

    <el-table :data="data" style="width: 1150px" @selection-change="handleSelectionChange">
      <el-table-column type="selection" />

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="头像" width="120" align="center">
        <template slot-scope="scope">
          <img
            v-if="scope.row.admin.avatar"
            :src="scope.row.admin.avatar"
            style="width: 100px;height: 100px;"
          >
        </template>
      </el-table-column>

      <el-table-column label="用户名" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.admin.username }}</span>
        </template>
      </el-table-column>

      <el-table-column label="拥有角色" width="150" align="center">
        <template slot-scope="{row}">
          <el-tag v-if="row.admin.role" :type="row.admin.role.roleName | roleNameFilter">{{ row.admin.role.roleName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="原因" width="200" align="center">
        <template slot-scope="scope">
          <p> {{ scope.row.cause }} </p>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="200" align="center">
        <el-tag type="danger">已删除</el-tag>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button type="warning" size="small" @click="handleRestore(scope.row)" v-permission="'/admin-recycle/restore'">恢复
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
  </div>
</template>

<script>
import { getRecycleList, restoreMessage } from '@/api/recycle'

export default {
  name: 'AdminRecycle',
  data() {
    return {
      loading: true, // 搜索框加载状态
      tableData: [], // 用于显示管理员信息
      tempData: [], // 用于存放搜索的临时信息
      currentPage: 1, // 当前页
      pageSize: 5, // 每页显示条数
      total: 0, // 总数量
      multipleSelection: [], // 存储多选框选中的行数
      genderList: [], // 性别字典
      keyword: '' // 搜索关键字
    }
  },
  methods: {
    getRecycleList() {
      this.tableData = []
      this.tempData = []
      getRecycleList().then(resp => {
        this.loading = false
        this.tableData = resp.data.recycleList
        this.tempData = resp.data.recycleList
        this.currentPage = 1
      })
    },
    handleRestore(row) { // 恢复管理员
      this.$confirm('此操作将恢复该管理员功能, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        restoreMessage(row).then(resp => {
          if (resp.code === 20000) {
            this.$message({
              type: 'success',
              message: resp.message
            })
          }
          this.getRecycleList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消恢复'
        })
      })
    },
    handleCurrentChange(val) { // 分页
      this.currentPage = val
    },
    handleSelectionChange(val) { // 存入选中的行数
      this.multipleSelection = val
    }
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
  created() {
    this.getRecycleList()
  },
  watch: {
    keyword() { // 进行搜索功能
      if (this.keyword === '') {
        this.tableData = this.tempData
      } else {
        this.tableData = []
        for (const item of this.tempData) {
          if (item.admin.username.includes(this.keyword)) {
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
  .pagination {
    display: flex;
    justify-content: center;
  }
</style>
