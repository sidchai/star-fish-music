<template>
<div class="signUp-page">
  <loginLogo/>
  <div class="signUp">
    <div class="signUp-head">
      <span>用户注册</span>
    </div>
    <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="70px" class="demo-ruleForm">
      <el-form-item prop="username" label="用户名">
        <el-input v-model="registerForm.username" prefix-icon="el-icon-user" placeholder="用户名"></el-input>
      </el-form-item>
      <el-form-item prop="phone" label="手机" >
        <el-input placeholder="手机" prefix-icon="el-icon-mobile" v-model="registerForm.phone"></el-input>
      </el-form-item>
      <el-form-item prop="code" label="验证码" >
        <el-input placeholder="验证码" prefix-icon="el-icon-chat-line-round" v-model="registerForm.code"/>
        <a href="javasript:;" type="button" style="position:absolute;right: 10px;top: 2px;" @click="getCodeFun"> <span style="color: #000"> {{ codeText }} </span> </a>
      </el-form-item>
      <el-form-item prop="password" label="密码">
        <el-input type="password" prefix-icon="el-icon-lock" placeholder="密码" v-model="registerForm.password"></el-input>
      </el-form-item>
      <div class="login-btn">
        <el-button type="primary" @click="SignUp">注册</el-button>
      </div>
    </el-form>
    <div class="more-sign">
      <h6>社交帐号直接注册</h6>
      <ul>
        <li><a id="weixin" class="weixin" href="http://localhost:9110/api/ucenter/wx/login"><i class="iconfont icon-weixin"/></a></li>
        <li><a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a></li>
      </ul>
    </div>
  </div>
</div>
</template>

<script>
import loginLogo from '../components/LoginLogo'
import { mixin } from '@/mixins'
import '@/assets/iconfont/iconfont.css'
import { rules, cities } from '@/assets/data/form'
import { sendMessage, register } from '@/api/register'

export default {
  name: 'SignUp-page',
  components: {
    loginLogo
  },
  mixins: [mixin],
  data () {
    return {
      registerForm: { // 注册
        username: '', // 用户名
        phone: '', // 手机号
        password: '', // 密码
        code: '' // 验证码
      },
      rules: {},
      sending: false, // 是否发送验证码
      second: 60, // 倒计时间
      codeText: '获取验证码'
    }
  },
  created () {
    this.rules = rules
    this.cities = cities
  },
  methods: {
    // 获取验证码
    getCodeFun() {
      if(this.registerForm.phone === '') {
        this.$message({
          type: 'error',
          message: '请先输入手机号'
        })
        return false
      }
      if(this.sending) return // 如果已点击则退出，防止多次重复提交
      this.sending = true // 用户已点击
      sendMessage(this.registerForm.phone).then(resp => {
        // 倒计时
        this.timeDown()
        // 提示发送成功
        this.$message.success(resp.message)
      })
    },
    // 倒计时
    timeDown() {
      this.codeText = this.second
      // 定义计时器
      const timer = setInterval(() => {
        this.codeText--
        if (this.codeText < 1) {
          clearInterval(timer)
          this.codeText = '获取验证码'
          this.sending = false
          this.second = 60
        }
        // console.log(new Date())
      }, 1000)
    },
    SignUp () {
      const params = this.registerForm
      this.$refs.registerForm.validate((valid) => {
        if(valid) {
          register(params).then(resp => {
            // 提示注册成功
            this.$message.success(resp.message)
            this.$router.push({ path: 'login-in' })
          })
        } else {
          console.log('校验失败')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/sign-up.scss';
</style>
