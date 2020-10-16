<template>
  <div class="app-container">
    <!-- 主体信息 -->
    <admin-list-table/>
  </div>
</template>

<script>
import AdminListTable from './components/AdminListTable'

export default {
  name: 'Admin',
  components: {
    AdminListTable
  }
}
</script>

