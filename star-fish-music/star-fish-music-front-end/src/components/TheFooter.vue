<template>
  <div class="the-footer">
    <p v-for="(item, index) in list" :key="index">
      {{item}}
      <el-link type="primary" href="http://www.beian.miit.gov.cn/" icon="el-icon-info">鄂ICP备2020018174号</el-link>
    </p>
  </div>
</template>

<script>
export default {
  name: 'TheFooter',
  data () {
    return {
      list: [
        `星の鱼音乐 版权所有© since 2020  `
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/the-footer.scss';
</style>
