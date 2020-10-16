<template>
  <div class="user-activity">
    <el-form
      ref="ruleForm"
      :model="ruleForm"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item
        prop="email"
        label="邮箱"
        :rules="[
          { type: 'email', message: '请输入正确的邮箱', trigger: ['blur', 'change']}
        ]"
        style="width: 400px;"
      >
        <el-input v-model="ruleForm.email" />
      </el-form-item>
      <el-form-item prop="qqNumber" label="QQ" style="width: 400px;">
        <el-input v-model="ruleForm.qqNumber" />
      </el-form-item>
      <el-form-item prop="weChatNumber" label="微信" style="width: 400px;">
        <el-input v-model="ruleForm.wechatNumber" />
      </el-form-item>
      <el-form-item prop="github" label="GitHub" style="width: 400px;">
        <el-input v-model="ruleForm.github" />
      </el-form-item>
      <el-form-item prop="gitee" label="Gitee" style="width: 400px;">
        <el-input v-model="ruleForm.gitee" />
      </el-form-item>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm('ruleForm')">保 存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getWebConfig, editWebConfig } from '@/api/webConfig'

export default {
  data() {
    return {
      ruleForm: {},
      // 表单校验规则
      rules: {
        qqNumber: [
          { pattern: /^[1-9][0-9]{5,10}$/, message: '请输入正确的QQ号码' }
        ],
        gitee: [
          { pattern: /^((https|http|ftp|rtsp|mms)?:\/\/gitee.com)[^\s]+/, message: '请输入正确的Gitee地址' }
        ],
        github: [
          { pattern: /^((https|http|ftp|rtsp|mms)?:\/\/github.com)[^\s]+/, message: '请输入正确的Github地址' }
        ]
      }
    }
  },
  created() {
    this.GetWebConfig()
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
    // 获取网站基础信息
    GetWebConfig() {
      getWebConfig().then(resp => {
        this.ruleForm = resp.data.item
      }).catch(error => {
        console.log(error)
      })
    }
  }
}
</script>
