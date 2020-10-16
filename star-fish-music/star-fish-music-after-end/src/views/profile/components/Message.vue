<template>
  <div class="user-activity">
    <el-form ref="ruleForm" :model="ruleForm"  :rules="rules" label-width="80px" >
      <el-form-item prop="nickName" label="昵称">
        <el-input v-model="ruleForm.nickName"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio v-for="(gender, index) in genderList" :key="index" :label="gender" v-model="ruleForm.gender"></el-radio>
      </el-form-item>
      <el-form-item prop="birthday" label="生日">
        <el-date-picker type="date" v-model="ruleForm.birthday" style="width: 100%;"></el-date-picker>
      </el-form-item>
      <el-form-item label="个人介绍" prop="introduce">
        <el-input type="textarea" v-model="ruleForm.introduce"></el-input>
      </el-form-item>
      <el-form-item prop="region" label="地区">
        <el-select v-model="ruleForm.region">
          <el-option v-for="item in cities" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="phone" label="手机号">
        <el-input v-model="ruleForm.phone"></el-input>
      </el-form-item>
      <el-form-item
        prop="email"
        label="邮箱"
        :rules="[
          { type: 'email', message: '请输入正确的邮箱', trigger: ['blur', 'change']}
        ]">
        <el-input v-model="ruleForm.email"></el-input>
      </el-form-item>
      <el-form-item prop="qq" label="QQ">
        <el-input v-model="ruleForm.qqNumber"></el-input>
      </el-form-item>
      <el-form-item prop="weChat" label="微信">
        <el-input v-model="ruleForm.wechatNumber"></el-input>
      </el-form-item>
      <el-form-item prop="github" label="GitHub">
        <el-input v-model="ruleForm.github"></el-input>
      </el-form-item>
      <el-form-item prop="gitee" label="Gitee">
        <el-input v-model="ruleForm.gitee"></el-input>
      </el-form-item>
      <el-form-item size="large">
        <el-button type="primary" @click="submitForm('ruleForm')">修 改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getLoginAdminInfo } from '@/api/admin'
export default {
  data() {
    const validatePhone = (rule, value, callback) => {
      // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
      if (!this.isCellPhone(value)) {
        callback(new Error(`请输入正确的手机号`))
      } else {
        callback()
      }
    }
    return {
      genderList: ['男', '女'],
      ruleForm: {}, // 当前表单信息
      cities: [{
        value: '北京',
        label: '北京'
      }, {
        value: '天津',
        label: '天津'
      }, {
        value: '河北',
        label: '河北'
      }, {
        value: '山西',
        label: '山西'
      }, {
        value: '内蒙古',
        label: '内蒙古'
      }, {
        value: '辽宁',
        label: '辽宁'
      }, {
        value: '吉林',
        label: '吉林'
      }, {
        value: '黑龙江',
        label: '黑龙江'
      }, {
        value: '上海',
        label: '上海'
      }, {
        value: '江苏',
        label: '江苏'
      }, {
        value: '浙江',
        label: '浙江'
      }, {
        value: '安徽',
        label: '安徽'
      }, {
        value: '福建',
        label: '福建'
      }, {
        value: '江西',
        label: '江西'
      }, {
        value: '山东',
        label: '山东'
      }, {
        value: '河南',
        label: '河南'
      }, {
        value: '湖北',
        label: '湖北'
      }, {
        value: '湖南',
        label: '湖南'
      }, {
        value: '广东',
        label: '广东'
      }, {
        value: '广西',
        label: '广西'
      }, {
        value: '海南',
        label: '海南'
      }, {
        value: '重庆',
        label: '重庆'
      }, {
        value: '四川',
        label: '四川'
      }, {
        value: '贵州',
        label: '贵州'
      }, {
        value: '云南',
        label: '云南'
      }, {
        value: '西藏',
        label: '西藏'
      }, {
        value: '陕西',
        label: '陕西'
      }, {
        value: '甘肃',
        label: '甘肃'
      }, {
        value: '青海',
        label: '青海'
      }, {
        value: '宁夏',
        label: '宁夏'
      }, {
        value: '新疆',
        label: '新疆'
      }, {
        value: '香港',
        label: '香港'
      }, {
        value: '澳门',
        label: '澳门'
      }, {
        value: '台湾',
        label: '台湾'
      }],
      // 表单校验规则
      rules: {
        phone: [{ validator: validatePhone, trigger: 'blur' }]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$message({
            showClose: true,
            message: '修改成功',
            type: 'success'
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    GetAdminList() {
      getLoginAdminInfo().then(resp => {
        this.ruleForm = resp.data.adminLoginInfo
      })
    },
    // 校验手机号
    isCellPhone(val) {
      if (!/^1(3|4|5|6|7|8)\d{9}$/.test(val)) {
        return false
      } else {
        return true
      }
    }
  },
  created() {
    this.GetAdminList()
  }
}
</script>
