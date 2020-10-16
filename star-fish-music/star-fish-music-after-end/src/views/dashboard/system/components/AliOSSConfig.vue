<template>
  <div class="user-activity">
    <el-form
      ref="ruleForm"
      :model="ruleForm"
      :rules="rules"
      label-width="120px">
      <el-form-item prop="localFileBaseUrl" label="本地文件域名" style="width: 400px;">
        <el-input v-model="ruleForm.localUrl"></el-input>
      </el-form-item>
      <el-form-item prop="aliFileBaseUrl" label="阿里云文件域名" style="width: 400px;">
        <el-input v-model="ruleForm.aliEndpoint"></el-input>
      </el-form-item>
      <el-form-item prop="aliAccessKey" label="阿里云账号" style="width: 400px;">
        <el-input v-model="ruleForm.aliAccessKey"></el-input>
      </el-form-item>
      <el-form-item prop="aliSecretKey" label="阿里云密码" style="width: 400px;">
        <el-input type="password" v-model="ruleForm.aliSecretKey"></el-input>
      </el-form-item>
      <el-form-item prop="aliBucket" label="阿里云上传空间" style="width: 400px;">
        <el-input v-model="ruleForm.aliBucket"></el-input>
      </el-form-item>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm('ruleForm')">保 存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getSystemConfig, editSystemConfig } from '@/api/systemConfig'

export default {
  data() {
    return {
      ruleForm: {},
      // 表单校验规则
      rules: {
        localFileBaseUrl: [
          { pattern: /^((https|http|ftp|rtsp|mms)?:\/\/gitee.com)[^\s]+/, message: '请输入正确的本地域名' }
        ],
        qiNiuFileBaseUrl: [
          { pattern: /^((https|http|ftp|rtsp|mms)?:\/\/github.com)[^\s]+/, message: '请输入正确的七牛域名' }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) { // 提交表单
      const data = this.ruleForm
      this.$refs[formName].validate((valid) => {
        if (valid) {
          editSystemConfig(data).then(resp => {
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
    // 获取网站系统信息
    GetSystemConfig() {
      getSystemConfig().then(resp => {
        this.ruleForm = resp.data.item
      }).catch(error => {
        console.log(error)
      })
    }
  },
  created() { // 页面加载前调用
    this.GetSystemConfig()
  }
}
</script>
