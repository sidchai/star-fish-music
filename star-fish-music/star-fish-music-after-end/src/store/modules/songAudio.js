const songAudio = {
  state: {
    isPlay: false, // 当前播放状态
    url: '', // 音乐url
    id: '' // 歌曲id
  },
  mutations: { // sync
    SET_IS_PLAY: (state, isplay) => {
      state.isplay = isplay
    },
    SET_URL: (state, url) => {
      state.url = url
    },
    SET_ID: (state, id) => {
      state.id = id
    }
  }
}

export default songAudio
