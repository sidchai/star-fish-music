<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            用户总数
          </div>
          <count-to :start-val="0" :end-val="userCount" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-singer">
          <svg-icon icon-class="singer-manage" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            歌手总数
          </div>
          <count-to :start-val="0" :end-val="singerCount" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-song">
          <svg-icon icon-class="song" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            歌曲总数
          </div>
          <count-to :start-val="0" :end-val="songCount" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-mv">
          <svg-icon icon-class="mv-manage" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            MV总数
          </div>
          <count-to :start-val="0" :end-val="mvCount" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'
import { getAllUser } from '@/api/user'
import { getAllSinger } from '@/api/singer'
import { getSongList } from '@/api/song'
import { getMvList } from '@/api/mv'

export default {
  data() {
    return {
      userCount: 0, // 用户总数
      singerCount: 0, // 歌手总数
      songCount: 0, // 歌曲总数
      mvCount: 0 // mv总数
    }
  },
  components: {
    CountTo
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    },
    GetUserCount() { // 获取用户总数
      getAllUser().then(resp => {
        this.userCount = resp.data.userInfo.length
      })
    },
    GetSingerCount() { // 获取歌手总数
      getAllSinger().then(resp => {
        this.singerCount = resp.data.singerInfo.length
      })
    },
    GetSongCount() { // 获取歌曲总数
      getSongList().then(resp => {
        this.songCount = resp.data.songInfo.length
      })
    },
    GetMvCount() { // 获取mv总数
      getMvList().then(resp => {
        this.mvCount = resp.data.mvInfo.length
      })
    }
  },
  created() {
    // 初始化数据
    this.GetUserCount()
    this.GetSingerCount()
    this.GetSongCount()
    this.GetMvCount()
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-singer {
        background: #36a3f7;
      }

      .icon-song {
        background: #f4516c;
      }

      .icon-mv {
        background: #34bfa3
      }
    }

    .icon-people {
      color: #40c9c6;
    }

    .icon-singer {
      color: #36a3f7;
    }

    .icon-song {
      color: #f4516c;
    }

    .icon-mv {
      color: #34bfa3
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
