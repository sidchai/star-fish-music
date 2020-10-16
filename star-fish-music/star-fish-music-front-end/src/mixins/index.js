import { getSongBySingerName } from '@/api/singer'
import { getMySongById } from '@/api/mySong'
import { changeCount } from '@/api/song'
import { mapGetters } from 'vuex'

export const mixin = {
  computed: {
    ...mapGetters([
      'userId',
      'loginIn'
    ])
  },
  methods: {
    // 提示信息
    notify (title, type) {
      this.$notify({
        title: title,
        type: type
      })
    },
    // 获取图片信息
    attachImageUrl (srcUrl) {
      return srcUrl ? srcUrl || '@/assets/img/user.jpg' : ''
    },
    attachBirth (val) {
      let birth = String(val).match(/[0-9-]+(?=\s)/)
      return Array.isArray(birth) ? birth[0] : birth
    },
    // 播放
    toplay(id, url, pic, index, name, artist, lyric) {
      changeCount(id)
      this.$store.commit('setId', id);//歌曲ID
      this.$store.commit('setListIndex', index);//歌曲当前下标
      this.$store.commit('setUrl', url)//歌曲URL
      this.$store.commit('setpicUrl', pic)
      this.$store.commit('setTitle', name);
      this.$store.commit('setArtist', artist);
      this.$store.commit('setLyric', this.parseLyric(lyric));
      if (this.loginIn) {
        this.$store.commit('setIsActive', false)
        getMySongById(this.userId).then(resp => {
          for (let item of resp.data.mySongInfo) {
            if (item.songId === id) {
              this.$store.commit('setIsActive', true)
              break
            }
          }
        })
        .catch(err => {
          console.log(err)
        })
      }
    },
    // 解析歌词
    parseLyric (text) {
      let lines = text.split('\n')
      let pattern = /\[\d{2}:\d{2}.(\d{3}|\d{2})\]/g
      let result = []

      // 对于歌词格式不对的特殊处理
      if (!(/\[.+\]/.test(text))) {
        return [[0, text]]
      }

      while (!pattern.test(lines[0])) {
        lines = lines.slice(1)
      }

      lines[lines.length - 1].length === 0 && lines.pop()
      for (let item of lines) {
        let time = item.match(pattern) // 存前面的时间段
        let value = item.replace(pattern, '') // 存歌词
        // console.log(time) // 时间
        // console.log(value) // 歌词数据
        for (let item1 of time) {
          let t = item1.slice(1, -1).split(':')
          if (value !== '') {
            result.push([parseInt(t[0], 10) * 60 + parseFloat(t[1]), value])
          }
        }
      }
      result.sort(function (a, b) {
        return a[0] - b[0]
      })
      return result
    },
    // 搜索音乐
    getSong () {
      if (!this.$route.query.keywords) {
        this.$store.commit('setListOfSongs', [])
        this.notify('您输入内容为空', 'warning')
      } else {
        getSongBySingerName(this.$route.query.keywords)
          .then(resp => {
            console.log(resp.data)
            const data = resp.data.songsBySingerName
            if (!data.length) {
              this.$store.commit('setListOfSongs', [])
              this.notify('系统暂无该歌曲', 'warning')
            } else {
              this.$store.commit('setListOfSongs', data)
            }
          })
          .catch(err => {
            console.log(err)
          })
      }
    }
  }
}
