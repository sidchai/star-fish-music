<template>
  <div>
    <el-upload
      class="avatar-uploader"
      :action="action"
      :show-file-list="false"
      :on-success="onSuccess"
      :before-upload="beforeUpload"
      :on-progress="onProgress"
      :on-error="onError">
      <img v-if="src" :src="src" class="avatar">
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      <slot></slot>
    </el-upload>
  </div>
</template>

<script>
export default {
  props: {
    action: {
      type: String
    },
    src: {
      type: String
    },
    onSuccess: {
      type: Function
    },
    beforeUpload: {
      type: Function
    },
    onProgress: {
      type: Function
    },
    onError: {
      type: Function
    },
    showFileList: {
      type: Boolean,
      default: true
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
