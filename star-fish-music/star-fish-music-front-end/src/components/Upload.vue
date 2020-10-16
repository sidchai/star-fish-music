<template>
  <div class="upload">
    <p class="title">修改头像</p>
    <hr/>
    <div class="section">
      <el-upload
        drag
        :action="avatarUrl"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>修改头像</em></div>
        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过5M</div>
      </el-upload>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { mixin } from '../mixins'
import { addOrChangeUserAvatar } from '@/api/user'

export default {
  name: 'Upload',
  mixins: [mixin],
  data () {
    return {
      avatarUrl: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/userPic'
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  methods: {
    handleAvatarSuccess (res) {
      if (res.success) {
        this.$store.commit('setAvatar', res.data.url)
        const data = {
          id: this.userId,
          avatar: res.data.url
        }
        this.$message({
          message: '修改成功',
          type: 'success'
        })
        addOrChangeUserAvatar(data)
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    beforeAvatarUpload (file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      const isLt3M = file.size / 1024 / 1024 < 5

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 或 GIF 格式!')
      }
      if (!isLt3M) {
        this.$message.error('上传图片大小不能超过 5MB!')
      }
      return isJPG && isLt3M
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/upload.scss';
</style>
