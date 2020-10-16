import { login, logout, getInfo, getMenu } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const state = {
  token: getToken(),
  adminId: '',
  name: '',
  avatar: '',
  roles: [],
  menu: {},
  buttonMap: {}
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_ADMIN_ID: (state, adminId) => {
    state.adminId = adminId
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  SET_MENU: (state, menu) => {
    state.menu = menu
  },
  SET_BUTTON_MAP: (state, buttonMap) => {
    state.buttonMap = buttonMap
  }
}

const actions = {
  // 登录
  Login({ commit }, userInfo) {
    const username = userInfo.username.trim()
    const password = userInfo.password.trim()
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        const data = response.data
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取菜单列表
  GetMenu({ commit, state }) {
    return new Promise((resolve, reject) => {
      getMenu(state.name).then(resp => {
        const data = resp.data
        // 对按钮进行处理
        const buttonList = data.buttonList
        const map = new Map()
        for (let i = 0; i < buttonList.length; i++) {
          map.set(buttonList[i].url, buttonList[i])
        }
        commit('SET_BUTTON_MAP', map)
        commit('SET_MENU', data)
        resolve(resp)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取用户信息
  GetInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const data = response.data
        if (!data.adminInfo) {
          reject('登录已过期，请重新登录')
        }
        commit('SET_NAME', data.adminInfo.username)
        commit('SET_AVATAR', data.adminInfo.avatar)
        commit('SET_ADMIN_ID', data.adminInfo.id)
        commit('SET_ROLES', data.roles)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 登出
  Logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        removeToken() // 必须先删除令牌
        resetRouter()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 删除 token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_ROLES', [])
      removeToken() // 必须先删除令牌
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

