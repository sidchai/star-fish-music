<template>
  <div class="swiper">
    <div class="swiper-container">
      <el-carousel
        v-loading="loading"
        element-loading-text="拼命加载中"
        element-loading-spinner="el-icon-loading"
        :interval="4000"
        type="card"
        height="400px">
        <el-carousel-item v-for="(item, index) in swiperList" :key="index">
          <img :src="item.pic"/>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script>
import { getCarouselAll } from '@/api/carousel'
export default {
  name: 'Swiper',
  data () {
    return {
      swiperList: [],
      loading: true
    }
  },
  created () {
    this.GetCarouselAll()
  },
  methods: {
    GetCarouselAll() {
      getCarouselAll().then(resp => {
        this.loading = false
        this.swiperList = resp.data.carouselInfo
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/swiper.scss';
</style>
