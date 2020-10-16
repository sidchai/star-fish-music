<template>
  <el-card style="margin-bottom:20px;">
    <div slot="header" class="clearfix">
      <span>关于我</span>
    </div>

    <div class="user-profile">
      <div class="box-center">
        <pan-thumb :image="user.avatar" :height="'100px'" :width="'100px'" :hoverable="false">
          <div>Hello</div>
          {{ user.role }}
        </pan-thumb>
      </div>
      <div class="box-center">
        <div class="user-name text-center">{{ user.name }}</div>
        <div class="user-role text-center text-muted">{{ user.role }}</div>
      </div>
    </div>

    <div class="user-bio">
      <div class="user-education user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="education" /><span>个人介绍</span></div>
        <div class="user-bio-section-body">
          <div class="text-muted">
            {{user.introduce}}
            <span>一个努力学习提升自我的codestar</span>
          </div>
        </div>
      </div>

      <div class="user-skills user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="skill" /><span>技能</span></div>
        <div class="user-bio-section-body">
          <div class="progress-item">
            <span>Java</span>
            <el-progress :percentage="80" />
          </div>
          <div class="progress-item">
            <span>Mysql</span>
            <el-progress :percentage="15" />
          </div>
          <div class="progress-item">
            <span>Vue</span>
            <el-progress :percentage="5" />
          </div>
          <div class="progress-item">
            <span>Game</span>
            <el-progress :percentage="100" status="success" />
          </div>
        </div>
      </div>
    </div>
    <div class="user-images">
      <el-carousel :interval="6000" type="card" height="220px">
        <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
          <img :src="item" class="image">
        </el-carousel-item>
      </el-carousel>
    </div>
  </el-card>
</template>

<script>
import PanThumb from '@/components/PanThumb'
import CarouselOne from '@/assets/carousel_images/1.jpg'
import CarouselTwo from '@/assets/carousel_images/2.jpg'
import CarouselThree from '@/assets/carousel_images/3.jpg'
import CarouselFour from '@/assets/carousel_images/4.jpg'
import CarouselFive from '@/assets/carousel_images/5.jpg'

export default {
  components: { PanThumb },
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          name: '',
          email: '',
          avatar: '',
          role: ''
        }
      }
    }
  },
  data() {
    return {
      carouselImages: [CarouselOne, CarouselTwo, CarouselThree, CarouselFour, CarouselFive]
    }
  }
}
</script>

<style lang="scss" scoped>
.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
}

.user-profile {
  .user-name {
    font-weight: bold;
  }

  .box-center {
    padding-top: 10px;
  }

  .user-role {
    padding-top: 10px;
    font-weight: 400;
    font-size: 14px;
  }

  .box-social {
    padding-top: 30px;

    .el-table {
      border-top: 1px solid #dfe6ec;
    }
  }

  .user-follow {
    padding-top: 20px;
  }
}

.user-bio {
  margin-top: 20px;
  color: #606266;

  span {
    padding-left: 4px;
  }

  .user-bio-section {
    font-size: 14px;
    padding: 15px 0;

    .user-bio-section-header {
      border-bottom: 1px solid #dfe6ec;
      padding-bottom: 10px;
      margin-bottom: 10px;
      font-weight: bold;
    }
  }
}
.image {
  width: 100%;
  height: 100%;

}

.user-images {
  padding-top: 20px;
}
</style>
