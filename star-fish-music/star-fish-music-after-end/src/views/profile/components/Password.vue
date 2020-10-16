<template>
  <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
    <el-form-item label="原密码" prop="oldPass">
      <el-input type="password" v-model="ruleForm.oldPass" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="pass">
      <el-input :disabled="isVerifyPwd" type="password" v-model="ruleForm.pass" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item label="确认密码" prop="checkPass">
      <el-input :disabled="isVerifyPwd" type="password" v-model="ruleForm.checkPass" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button :disabled="isVerifyPwd" type="primary" @click="submitForm('ruleForm')">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { verifyPwd, editPassword } from '@/api/admin'
export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    const validateOldPass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入原密码'))
      } else {
        const data = {
          id: this.user.adminId,
          username: this.user.name,
          password: value
        }
        verifyPwd(data).then(resp => {
          if (resp.message === '原密码不正确') {
            console.log(111)
            this.isVerifyPwd = true
            this.$message({
              showClose: true,
              message: resp.message,
              type: 'error'
            })
          } else {
            this.isVerifyPwd = false
          }
        })
        if (this.ruleForm.checkPass !== '') {
          this.$refs.ruleForm.validateField('checkPass')
        }
        callback()
      }
    }
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6 || value.length > 16) {
        callback(new Error('密码不长度在6-16位之间'))
      } else {
        if (this.ruleForm.checkPass !== '') {
          this.$refs.ruleForm.validateField('checkPass')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.ruleForm.pass) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      isVerifyPwd: false,
      ruleForm: {
        pass: '',
        checkPass: '',
        oldPass: ''
      },
      // 定义表单校验规则
      rules: {
        pass: [
          { validator: validatePass, trigger: 'blur' }
        ],
        checkPass: [
          { validator: validatePass2, trigger: 'blur' }
        ],
        oldPass: [
          { validator: validateOldPass, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const data = this.ruleForm
          const admin = {
            id: this.user.adminId,
            username: this.user.name,
            password: data.pass
          }
          // 修改密码
          editPassword(admin).then(resp => {
            if (resp.code === 20000) {
              this.$message({
                showClose: true,
                message: resp.message,
                type: 'success'
              })
              this.ruleForm = {}
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>
