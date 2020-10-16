const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  adminId: state => state.user.adminId,
  name: state => state.user.name,
  roles: state => state.user.roles,
  isPlay: state => state.songAudio.isPlay,
  url: state => state.songAudio.url,
  id: state => state.songAudio.id,
  menu: state => state.user.menu,
  buttonMap: state => state.user.buttonMap
}
export default getters
