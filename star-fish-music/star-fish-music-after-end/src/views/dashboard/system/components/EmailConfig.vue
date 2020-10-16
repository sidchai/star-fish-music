<template>
  <div class="user-activity">
    <el-form
      ref="ruleForm"
      :model="ruleForm"
      label-width="120px">
      <el-form-item
        prop="emailNumber"
        label="邮箱"
        :rules="[
          { type: 'email', message: '请输入正确的邮箱', trigger: ['blur', 'change']}
        ]"
        style="width: 400px;">
        <el-input v-model="ruleForm.emailNumber"></el-input>
      </el-form-item>
      <el-form-item prop="emailPassword" label="密码" style="width: 400px;">
        <el-input type="password" v-model="ruleForm.emailPassword"></el-input>
      </el-form-item>
      <el-form-item prop="smtpHost" label="SMTP地址" style="width: 400px;">
        <el-input v-model="ruleForm.smtpHost"></el-input>
      </el-form-item>
      <el-form-item prop="smtpPort" label="SMTP端口号" style="width: 400px;">
        <el-input v-model="ruleForm.smtpPort"></el-input>
      </el-form-item>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm('ruleForm')">保 存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { editSystemConfig, getSystemConfig } from '@/api/systemConfig'

export default {
  data() {
    return {
      ruleForm: {}
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
    }
  },
  created() {
    getSystemConfig().then(resp => {
      this.ruleForm = resp.data.item
    }).catch(error => {
      console.log(error)
    })
  }
}
</script>
