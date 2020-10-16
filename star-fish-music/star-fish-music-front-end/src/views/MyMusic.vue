<template>
  <div class="my-music">
    <div class="album-slide">
      <div class="album-img">
        <img :src=form.avatar alt="">
      </div>
      <ul class="album-info">
        <li>昵称： {{ form.username }}</li>
        <li>性别： {{ form.gender }}</li>
        <li>生日： {{ form.birthday }}</li>
        <li>故乡： {{ form.region }}</li>
      </ul>
    </div>
    <div class="album-content">
      <div class="album-title">个性签名: {{ form.introduce }}</div>
      <div class="songs-body">
        <album-content
          v-loading="loading"
          element-loading-text="拼命加载中"
          element-loading-spinner="el-icon-loading"
          :songList="listOfSongs">
          <template slot="title">我的收藏</template>
        </album-content>
      </div>
    </div>
  </div>
</template>

<script>
import {mixin} from '@/mixins'
import { mapGetters } from 'vuex'
import AlbumContent from '@/components/AlbumContent'
import {getUserInfoById} from "@/api/user"
import { getMySongById } from '@/api/mySong'

export default {
  name: 'my-music',
  components: {
    AlbumContent
  },
  mixins: [mixin],
  data () {
    return {
      form: {},
      loading: true
    }
  },
  computed: {
    ...mapGetters([
      'userId',
      'id',
      'listOfSongs' // 存放的音乐
    ])
  },
  mounted () {
    this.getMsg()
    this.GetMySongById()
  },
  methods: {
    getMsg () {
      getUserInfoById(this.userId).then(resp => {
        this.loading = false
        this.form = resp.data.userInfoById
        this.getuserSex(this.form.gender)
      }).catch(err => {
        console.log(err)
      })
    },
    getuserSex (sex) {
      if (sex === '0') {
        this.form.gender = '女'
      } else if (sex === '1') {
        this.form.gender = '男'
      }
    },
    // 收藏的歌曲ID
    GetMySongById () {
      getMySongById(this.userId).then(resp => {
        const data = resp.data.mySongInfo
        // this.collectList.push(data)
        // console.log(data)
        this.$store.commit('setListOfSongs', data)
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../assets/css/my-music.scss';
</style>
