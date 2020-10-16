import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import tagsView from './modules/tagsView'
import settings from './modules/settings'
import user from './modules/user'
import songAudio from './modules/songAudio'

Vue.use(Vuex)

const modulesFiles = require.context('./modules', true, /\.js$/)

const modules = modulesFiles.keys().reduce((modules, modulePath) => {
  const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
  const value = modulesFiles(modulePath)
  modules[moduleName] = value.default
  return modules
}, {})

const store = new Vuex.Store({
  modules, /* : {    app,
    tagsView,
    settings,
    user,
    songAudio
  },*/
  getters
})

export default store
