<template>
  <div class="content-list">
    <ul class="section-content">
      <li class="content-item" v-for="(item, index) in contentList" :key="index">
        <div class="kuo" @click="goAblum(item, item.style)">
          <img class="item-img" :src="item.pic " :alt="item.singerName">
          <div class="mask"  @click="goAblum(item, item.title)">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-bofang"></use>
            </svg>
          </div>
        </div>
        <p class="item-name">{{ item.singerName || item.title }}</p>
      </li>
    </ul>
  </div>
</template>

<script>
import { mixin } from '@/mixins'

export default {
  name: 'ContentList',
  mixins: [mixin],
  props: [
    'contentList'
  ],
  methods: { //
    goAblum (item, type) {
      this.$store.commit('setTempList', item)
      if (type) {
        this.$router.push({path: `/song-list-album/${item.id}`})
      } else {
        this.$router.push({path: `/singer-album/${item.id}`})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/content-list.scss';
</style>
