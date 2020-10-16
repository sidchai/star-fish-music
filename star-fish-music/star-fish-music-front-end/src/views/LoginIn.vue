<template>
<div class="login-in">
  <login-logo/>
  <div class="login">
    <div class="login-head">
      <span>帐号登录</span>
    </div>
    <el-form :model="loginForm" status-icon :rules="rules" ref="loginForm" class="demo-ruleForm" >
      <el-form-item prop="username">
        <el-input placeholder="请输入用户名或手机号" v-model="loginForm.username"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="请输入密码" v-model="loginForm.password" @keyup.enter.native="handleleLoginIn"></el-input>
      </el-form-item>
      <div class="login-btn">
        <el-button @click="goSignUp">注册</el-button>
        <el-button type="primary" @click="handleleLoginIn">登录</el-button>
      </div>
    </el-form>
  </div>
</div>
</template>

<script>
import { mixin } from '@/mixins'
import LoginLogo from '@/components/LoginLogo'
import { loginIn } from '@/api/user'

export default {
  name: 'login-in',
  components: {
    LoginLogo
  },
  mixins: [mixin],
  data () {
    let validateName = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('用户名或手机号不能为空'))
      } else {
        callback()
      }
    }
    let validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    return {
      loginForm: { // 登录用户名密码
        username: '',
        password: ''
      },
      rules: {
        username: [
          { validator: validateName, message: '请输入用户名或手机号', trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.changeIndex('登录')
  },
  methods: {
    changeIndex (value) {
      this.$store.commit('setActiveName', value)
    },
    handleleLoginIn () {
      let params = this.loginForm
      const _this = this
      loginIn(params).then(resp => {
          // console.log('-----------获取登录信息---------------')
          if (resp.code === 20000) {
            _this.$message({
              message: resp.message,
              type: 'success'
            })
            _this.setUserMsg(resp.data.userInfo)
            _this.$store.commit('setLoginIn', true)
            setTimeout(function () {
              _this.changeIndex('首页')
              _this.$router.push({path: '/'})

            }, 2000)
          } else {
            _this.notify(resp.message, 'error')
          }
        })
        .catch(failResponse => {})
    },
    setUserMsg (item) {
      this.$store.commit('setUserId', item.id)
      this.$store.commit('setUsername', item.username)
      this.$store.commit('setAvatar', item.avatar)
    },
    goSignUp () {
      this.$router.push({path: '/sign-up'})
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/login-in.scss';
</style>
