<template>
  <div class="content-list">
    <ul class="section-content">
      <li class="content-item" v-for="(item, index) in mvList" :key="index">
        <div class="kuo" @click="goAblum(item.sourceId)">
          <img class="item-img" :src="item.pic " :alt="item.title">
          <div class="mask"  @click="goAblum(item.sourceId)">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-bofang"></use>
            </svg>
          </div>
        </div>
        <p class="item-name">{{ item.singerName + '-' + item.title  }}</p>
      </li>
    </ul>
  </div>
</template>

<script>

export default {
  name: 'MVList',
  props: {
    mvList: {
      type: Object
    }
  },
  methods: { //
    goAblum (sourceId) {
      this.$router.push({ path: `/mv-album/${sourceId}` })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../../../assets/css/content-list.scss';
</style>
