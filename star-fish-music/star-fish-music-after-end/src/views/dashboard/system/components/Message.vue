<template>
  <div class="user-activity">
    <el-form
      ref="ruleForm"
      :model="ruleForm"
      label-width="80px">
      <el-form-item prop="logo" label="Logo">
        <upload
          :action="url"
          :src="ruleForm.logo"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          :on-error="handleAvatarError">
        </upload>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="12" :lg="8">
          <el-form-item prop="siteName" label="网站名称">
            <el-input v-model="ruleForm.siteName"></el-input>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="16">
          <el-form-item label="标题" prop="title">
            <el-input v-model="ruleForm.title"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="12" :lg="8">
          <el-form-item prop="keyword" label="关键字">
            <el-input v-model="ruleForm.keyword"></el-input>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="16">
          <el-form-item label="网站描述" prop="introduce">
            <el-input type="textarea" v-model="ruleForm.introduce"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="12" :lg="8">
          <el-form-item prop="author" label="作者">
            <el-input v-model="ruleForm.author"></el-input>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="12" :lg="16">
          <el-form-item label="类型" prop="type">
            <el-input v-model="ruleForm.type"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" :md="12" :lg="8">
          <el-form-item prop="aliPay" label="支付宝">
            <upload :action="url" :src="ruleForm.aliPay" :on-success="handleAliPaySuccess" :before-upload="beforeAvatarUpload">
            </upload>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="8" :md="12" :lg="16">
          <el-form-item label="微信" prop="weChatPlay">
            <upload :action="url" :src="ruleForm.weChatPay" :on-success="handleWeChatPaySuccess" :before-upload="beforeAvatarUpload">
            </upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm('ruleForm')">保 存</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import { getWebConfig, editWebConfig } from '@/api/webConfig'
import Upload from '@/components/upload'
export default {
  components: {
    Upload
  },
  data() {
    return {
      ruleForm: {},
      url: process.env.VUE_APP_URL + 'music/oss/file/upload?model=img/systemPic' // 上传地址  process.env.VUE_APP_URL => http://localhost:8088/
    }
  },
  methods: {
    submitForm(formName) {
      const data = this.ruleForm
      this.$refs[formName].validate((valid) => {
        if (valid) {
          editWebConfig(data).then(resp => {
            if (resp.code === 20000) { // 请求成功
              this.$message({
                showClose: true,
                message: resp.message,
                type: 'success'
              })
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 文件上传失败(http)
    handleAvatarError() {
      this.$message.error('上传失败!（http失败）')
    },
    // 文件上传成功
    handleAvatarSuccess(resp, file) {
      if (resp.success) {
        this.ruleForm.logo = resp.data.url
        const data = this.ruleForm
        editWebConfig(data).then(resp => {
        })
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    // 文件上传成功
    handleAliPaySuccess(resp, file) {
      if (resp.success) {
        this.ruleForm.aliPay = resp.data.url
        const data = this.ruleForm
        editWebConfig(data).then(resp => {
        })
      } else {
        this.$message.error('上传失败!（非20000）')
      }
    },
    // 文件上传成功
    handleWeChatPaySuccess(resp, file) {
      if (resp.success) {
        this.ruleForm.weChatPay = resp.data.url
        const data = this.ruleForm
        editWebConfig(data).then(resp => {
        })
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
        this.$message.error('上传logo图片大小不能超过 3MB!')
      }
      return isJPG && isLt3M
    },
    // 获取网站基础信息
    GetWebConfig() {
      getWebConfig().then(resp => {
        this.ruleForm = resp.data.item
      }).catch(error => {
        console.log(error)
      })
    }
  },
  created() {
    this.GetWebConfig()
  }
}
</script>
<style>
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
  .inputClass {
    position: absolute;
  }
  img {
    width: 100px;
    height: 100px;
  }
</style>
