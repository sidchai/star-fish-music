<template>
  <div
    v-loading="loading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input v-model="queryParams.content" clearable class="filter-item" style="width: 200px;" placeholder="请输入评论名" />
      <el-input v-model="queryParams.userName" clearable class="filter-item" style="width: 150px;" placeholder="请输入用户名" />

      <el-select v-model="queryParams.type" clearable placeholder="评论类型" style="width:110px">
        <el-option
          v-for="item in commentTypeDictList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        />
      </el-select>

      <el-select v-model="queryParams.source" clearable placeholder="评论来源" style="width:110px">
        <el-option
          v-for="item in commentSourceDictList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        />
      </el-select>

      <el-button v-permission="'/comment/get-list'" class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button v-permission="'/comment/delete-batch'" class="filter-item" type="danger" icon="el-icon-delete" @click="handleDeleteBatch">删除选中</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">

      <el-table-column type="selection" />

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="头像" width="160" align="center">
        <template slot-scope="scope">
          <img
            v-if="scope.row.user"
            :src="BASE_IMAGE_URL + scope.row.user.photoUrl"
            onerror="onerror=null;src='https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'"
            style="width: 100px;height: 100px;"
          >
          <img
            v-else
            src="https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            style="width: 100px;height: 100px;"
          >
        </template>
      </el-table-column>

      <el-table-column label="评论人" width="100" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.user">{{ scope.row.user.nickName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="来源" width="150" align="center">
        <template slot-scope="scope">
          <template>
            <el-tag type="warning" style="cursor: pointer;" @click.native="goPage(scope.row.source, scope.row.blog)">{{ scope.row.sourceName }}</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="内容" width="250" align="center">
        <template slot-scope="scope">
          <!--            <el-popover-->
          <!--              v-if="scope.row.content"-->
          <!--              placement="top-start"-->
          <!--              width="400"-->
          <!--              trigger="hover"-->
          <!--              :content="scope.row.content">-->
          <!--              <el-button slot="reference">{{subComment(scope.row.content, 10)}}</el-button>-->
          <!--            </el-popover>-->
          <span v-html="$xss(scope.row.content, options)" />
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="150">
        <template slot-scope="scope">
          <el-button v-permission="'/comment/delete'" type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
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
import { getCommentList, deleteComment, deleteBatchComment } from '@/api/comment'
import { getListByDictTypeList } from '@/api/sysDict'
export default {
  name: 'Comment',
  data() {
    return {
      loading: true,
      // xss白名单配置
      options: {
        whiteList: {
          a: ['href', 'title', 'target', 'style'],
          span: ['class', 'style']
        }
      },
      queryParams: {
        content: null, // 评论内容
        userName: null, // 用户名
        source: null// 来源
      }, // 查询参数
      multipleSelection: [], // 多选，用于批量删除
      BLOG_WEB_URL: process.env.BLOG_WEB_URL,
      BASE_IMAGE_URL: process.env.BASE_IMAGE_URL,
      tableData: [],
      keyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0, // 总数量
      title: '增加友链',
      dialogFormVisible: false, // 控制弹出框
      formLabelWidth: '120px',
      isEditForm: false,
      commentTypeDictList: [], // 评论类型字典
      commentSourceDictList: [] // 评论来源字典
    }
  },
  created() {
    // 获取字典
    this.getDictList()
    // 获取评论
    this.commentList()
  },
  methods: {
    // 跳转到该博客详情
    onClick(row) {
      console.log('点击跳转', row)
      window.open(this.BLOG_WEB_URL + '/#/info?blogUid=' + row.uid)
    },
    // 跳转到前端页面
    goPage: function(type, blog) {
      switch (type) {
        case 'MESSAGE_BOARD': { window.open(this.BLOG_WEB_URL + '/#/messageBoard') } break
        case 'ABOUT': { window.open(this.BLOG_WEB_URL + '/#/about') } break
        case 'BLOG_INFO': { window.open(this.BLOG_WEB_URL + '/#/info?blogUid=' + blog.uid) } break
      }
    },
    /**
     * 字典查询
     */
    getDictList: function() {
      var dictTypeList = ['sys_comment_type', 'sys_comment_source']

      getListByDictTypeList(dictTypeList).then(resp => {
        if (resp.code === 'success') {
          var dictMap = resp.data
          this.commentTypeDictList = dictMap.sys_comment_type.list
          this.commentSourceDictList = dictMap.sys_comment_source.list
        }
      })
    },
    commentList: function() {
      const params = {}
      params.keyword = this.queryParams.content
      if (this.queryParams.source == null || this.queryParams.source === undefined || this.queryParams.source === '') {
        params.source = 'all'
      } else {
        params.source = this.queryParams.source
      }
      params.userName = this.queryParams.userName
      params.type = this.queryParams.type
      params.currentPage = this.currentPage
      params.pageSize = this.pageSize

      getCommentList(params).then(resp => {
        if (resp.code === 20000) {
          this.loading = false
          this.tableData = resp.data.records
          this.currentPage = resp.data.current
          this.pageSize = resp.data.size
          this.total = resp.data.total
        }
      })
    },
    subComment(str, index) {
      if (str == null || str === undefined) {
        return ''
      }
      if (str.length < index) {
        return str
      } else {
        return str.substring(0, index) + '...'
      }
    },
    handleFind: function() {
      this.commentList()
    },
    handleReply: function(row) {
      console.log('点击了回复')
    },
    handleDelete: function(row) {
      const that = this;
      const params = {}
      params.uid = row.uid
      deleteComment(params).then(resp => {
        console.log(resp)
        this.$message({
          type: 'success',
          message: resp.data
        })
        that.commentList()
      })
    },
    handleDeleteBatch: function() {
      var that = this
      if (that.multipleSelection.length <= 0) {
        this.$message({
          type: 'error',
          message: '请先选中需要删除的内容！'
        })
        return
      }
      this.$confirm('此操作将把选中的评论删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deleteBatchComment(that.multipleSelection).then(resp => {
            console.log(resp)
            this.$message({
              type: 'success',
              message: resp.data
            })
            that.commentList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleCurrentChange: function(val) {
      console.log('点击了换页')
      this.currentPage = val
      this.commentList()
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val
    }

  }
}
</script>

<style scoped>

</style>
