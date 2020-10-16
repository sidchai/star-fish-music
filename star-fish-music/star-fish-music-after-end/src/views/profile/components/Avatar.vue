<template>
  <div class="block">
    <el-upload
      class="avatar-uploader"
      action="https://jsonplaceholder.typicode.com/posts/"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeAvatarUpload">
      <img v-if="url" :src="url" class="avatar">
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
    </el-upload>
    <el-calendar v-model="value">
    </el-calendar>
  </div>
</template>

<script>

import {addOrChangeAdminAvatar} from "@/api/admin";

export default {
  props: {
    url: {
      type: String
    },
    adminId: {
      type: String
    }
  },
  data() {
    return {
      value: new Date()
    }
  },
  methods: {
    handleAvatarSuccess(resp) {
      if (resp.success) {
        const data = {
          id: this.adminId,
          avatar: resp.data.url
        }
        // 修改或添加数据库中信息
        addOrChangeAdminAvatar(data)
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
