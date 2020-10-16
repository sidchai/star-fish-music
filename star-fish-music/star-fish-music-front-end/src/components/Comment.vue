<template>
  <div>
    <div class="comment">
      <h2>评论</h2>
      <div class="comment-msg">
        <div class="comment-img">
          <img :src=avatar alt="">
        </div>
        <el-input
                class="comment-input"
                type="textarea"
                :rows="2"
                placeholder="请输入内容"
                v-model="textarea">
        </el-input>
      </div>
      <el-button type="primary" class="sub-btn" @click="postComment()">评论</el-button>
    </div>
    <div
      v-loading="loading"
      element-loading-text="拼命加载中"
      element-loading-spinner="el-icon-loading">
      <p>精彩评论: 共 {{commentList.length}} 条评论</p>
      <ul
        class="popular"
        v-for="(item, index) in commentList"
        :key="index">
        <li>
          <div class="popular-img">
            <img :src=userPic[index] alt="">
          </div>
          <div class="popular-msg">
            <ul>
              <li class="name">{{nickName[index]}}</li>
              <li class="time">{{item.createTime}}</li>
              <li class="content">{{item.content}}</li>
            </ul>
          </div>
          <div class="up" ref="up" @click="postUp(item.id, item.up, index)">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-zan"></use>
            </svg>
            {{item.up}}
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
  import {mixin} from '@/mixins'
  import {mapGetters} from 'vuex'
  import {getAllComment, addComment, setLike} from '@/api/comment'

  export default {
    name: 'Comment',
    mixins: [mixin],
    props: [
      'playId', // 歌曲ID或歌单ID
      'type' // 歌单（1）/歌曲（0）
    ],
    data() {
      return {
        commentList: [], // 存放评论内容
        userPic: [], // 保存评论用户头像
        nickName: [], // 保存评论用户名字
        textarea: '', // 存放输入内容
        loading: true
      }
    },
    computed: {
      ...mapGetters([
        'id',
        'userId', // 用户ID
        'index', // 列表中的序号
        'loginIn', // 用户是否登录
        'avatar' // 用户头像
      ])
    },
    watch: {
      id() {
        this.GetAllComment()
      }
    },
    mounted() {
      this.GetAllComment()
    },
    methods: {
      // 获取所有评论
      GetAllComment() {
        getAllComment(this.type, this.playId).then(resp => {
          this.loading = false
          this.commentList = resp.data.commentInfo
          for (let item of resp.data.commentInfo) {
            this.userPic.push(item.avatar)
            this.nickName.push(item.nickName)
          }
        }).catch(err => {
          console.log(err)
        })
      },
      // 提交评论
      postComment() {
        if (this.loginIn) {
          // 0 代表歌曲， 1 代表歌单
          const params = {
            id: this.playId,
            userId: this.userId,
            type: this.type,
            content: this.textarea
          }
          addComment(params)
              .then(resp => {
                if (resp.code === 20000) {
                  this.textarea = ''
                  this.GetAllComment()
                  this.notify(resp.message, 'success')
                } else {
                  this.notify(resp.message, 'error')
                }
              })
              .catch(err => {
                console.log(err)
              })
        } else {
          this.notify('请先登录', 'warning')
        }
      },
      // 点赞
      postUp(id, up, index) {
        if (this.loginIn) {
          const params = {
            id,
            up: up + 1
          }
          setLike(params)
              .then(resp => {
                if (resp.code === 20000) {
                  this.$refs.up[index].children[0].style.color = '#2796dd'
                  this.GetAllComment()
                }
              })
              .catch(err => {
                console.log(err)
              })
        } else {
          this.notify('请先登录', 'warning')
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import '../assets/css/comment.scss';
</style>
