<template>
  <div class="app-container">
    <el-tabs type="border-card">
      <el-tab-pane label="网站信息">  <!-- v-permission="'/webConfig/getWebConfig'" -->
        <message/>
      </el-tab-pane>

      <el-tab-pane label="关注我们">
        <follow-us/>
      </el-tab-pane>

      <el-tab-pane label="阿里云配置">
        <ali-o-s-s-config/>
      </el-tab-pane>

      <el-tab-pane label="邮箱配置">
        <email-config/>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
import Message from './components/Message'
import FollowUs from './components/FollowUs'
import AliOSSConfig from './components/AliOSSConfig'
import EmailConfig from './components/EmailConfig'

export default {
  name: 'System',
  components: {
    Message,
    FollowUs,
    AliOSSConfig,
    EmailConfig
  }
}
</script>

<style scoped>

</style>
