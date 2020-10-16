<template>
  <div class="info">
    <p class="title">编辑个人资料</p>
    <hr/>
    <div class="personal">
      <el-form :model="form" class="demo-ruleForm" label-width="80px">
        <el-form-item prop="username" label="昵称">
          <el-input v-model="form.username" placeholder="昵称"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio
              v-for="gender in genderList"
              :key="gender.id"
              v-model="form.gender"
              :label="gender.dictValue"
              border
              size="medium"
            >{{gender.dictLabel}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="phone" label="手机">
          <el-input  placeholder="手机" v-model="form.phone" ></el-input>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item prop="birthday" label="生日">
          <el-date-picker type="date" placeholder="选择日期" v-model="form.birthday" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item prop="introduce" label="个性签名">
          <el-input  type="textarea" placeholder="个性签名" v-model="form.introduce" ></el-input>
        </el-form-item>
        <el-form-item prop="region" label="地区">
          <el-select v-model="form.region" placeholder="地区" style="width:100%">
            <el-option
              v-for="item in cities"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="qqNumber" label="QQ号">
          <el-input placeholder="请输入您的qq号" v-model="form.qqNumber" ></el-input>
        </el-form-item>
        <el-form-item label="微信号">
          <el-input  placeholder="请输入您的微信号" v-model="form.wechatNumber" ></el-input>
        </el-form-item>
        <el-form-item label="职业">
          <el-input  placeholder="请输入您的职业" v-model="form.occupation" ></el-input>
        </el-form-item>
      </el-form>
    <div class="btn">
      <el-button type="success" @click="editUserInfo()">保存</el-button>
      <el-button type="info" @click="goback">取消</el-button>
    </div>
    </div>
</div>
</template>

<script>
import { mapGetters } from 'vuex'
import { cities } from '@/assets/data/form'
import { getUserInfoById, editUserInfo, getListByDictType } from '@/api/user'

export default {
  name: 'Info',
  data: function () {
    return {
      form: { // 注册
        username: '',
        password: '',
        gender: '',
        phone: '',
        email: '',
        birth: '',
        introduction: '',
        region: ''
      },
      cities: [],
      genderDefaultValue: '', // 设置性别默认值
      genderList: [], // 性别字典
    }
  },
  computed: {
    ...mapGetters([
      'userId'
    ])
  },
  created () {
    this.cities = cities
  },
  mounted () {
    this.GetUserInfoById()
    this.GetDictList()
  },
  methods: {
    GetUserInfoById () {
      getUserInfoById(this.userId).then(resp => {
        this.form = resp.data.userInfoById
      }).catch(err => {
        console.log(err)
      })
    },
    GetDictList() { // 字典查询
      const dictType = 'sys_user_sex'
      getListByDictType(dictType).then(resp => {
        if (resp.code === 20000) {
          const data = resp.data.dictList
          this.genderList = data
          this.genderDefaultValue = data[0].dictValue
        }
      })
    },
    goback () {
      this.$router.go(-1)
    },
    editUserInfo () {
      const _this = this
      const params = this.form
      editUserInfo(params).then(resp => {
        if (resp.code === 20000) {
          this.$store.commit('setUsername', this.form.username)
          this.$notify.success({
            title: resp.message,
            showClose: true
          })
          setTimeout(function () {
            _this.$router.go(-1)
          }, 2000)
        } else {
          this.$notify.error({
            title: resp.message,
            showClose: true
          })
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/info.scss';
</style>
