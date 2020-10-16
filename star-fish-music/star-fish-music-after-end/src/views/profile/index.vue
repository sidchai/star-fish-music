<template>
  <div class="app-container">
    <div v-if="user">
      <el-row :gutter="20">

        <el-col :span="8" :xs="24">
          <user-card :user="user" />
        </el-col>

        <el-col :span="16" :xs="24">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="个人信息" name="message">
                <message />
              </el-tab-pane>
              <el-tab-pane label="修改头像" name="avatar">
                <avatar :url="avatar" :adminId="adminId" />
              </el-tab-pane>
              <el-tab-pane label="修改密码" name="password">
                <password :user="user" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import UserCard from './components/UserCard'
import Message from './components/Message'
import Avatar from './components/Avatar'
import Password from './components/Password'

export default {
  name: 'Profile',
  components: { UserCard, Message, Avatar, Password },
  data() {
    return {
      user: {},
      activeTab: 'message'
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles',
      'adminId'
    ])
  },
  created() {
    this.getUser()
  },
  methods: {
    getUser() {
      this.user = {
        adminId: this.adminId,
        name: this.name,
        role: this.roles[0].roleName,
        avatar: this.avatar
      }
    }
  }
}
</script>
