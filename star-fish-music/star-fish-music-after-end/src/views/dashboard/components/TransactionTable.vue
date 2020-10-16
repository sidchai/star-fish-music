<template>
  <div>
    <el-table :data="list" style="width: 100%;padding-top: 15px;">
      <el-table-column label="用户名" min-width="200">
        <template slot-scope="scope">
          {{ scope.row.username }}
        </template>
      </el-table-column>
      <el-table-column label="角色名" width="495" align="center">
        <template slot-scope="scope">
          <el-tag>
            {{ scope.row.role.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="{row}">
          <template v-if="row.status === 1">
            <el-tag :type="row.status | statusFilter">
              <span>正常</span>
            </el-tag>
          </template>
          <template v-if="row.status === 0">
            <el-tag :type="row.status | statusFilter">
              <span>封禁</span>
            </el-tag>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="block">
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getAdminListPage } from '@/api/admin'

export default {
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
      list: [],
      currentPage: 1,
      pageSize: 8,
      total: 0
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      getAdminListPage(this.currentPage, this.pageSize).then(resp => {
        this.list = resp.data.adminPageList.records
        this.currentPage = resp.data.current
        this.pageSize = resp.data.size
        this.total = resp.data.adminPageList.total
      })
    },
    handleCurrentChange(val) {
      console.log(val)
      this.currentPage = val
    }
  }
}
</script>
