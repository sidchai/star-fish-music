import router from './router'
import store from './store'
import { Message, MessageBox } from 'element-ui'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css' // Progress 进度条样式
import { getToken } from '@/utils/auth' // 从cookie中获得token
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // NProgress 配置

const whiteList = ['/login'] // 不重定向白名单

router.beforeEach(async(to, from, next) => {
  // 开启进度条
  NProgress.start()

  // 设置页面标题
  document.title = getPageTitle(to.meta.title)

  // 确认用户是否已经登录
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      // 如果已经登录, 直接重定向到首页
      next({ path: '/' })
      NProgress.done()
    } else if (to.path === '/deploy') {
      next()
    } else {
      const hasGetUserInfo = store.getters.name
      if (hasGetUserInfo) { // 有用户信息
        next()
      } else {
        try {
          // 拉取用户信息
          await store.dispatch('user/GetInfo').then(resp => {
            if (!resp.roles || resp.roles.length <= 0) { //
              MessageBox.confirm('token已过期，请重新登录', '确定登出', {
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                store.dispatch('user/Logout').then(() => {
                  location.reload() // 为了重新实例化vue-router对象 避免bug
                })
              })
            }
          })
          next()
        } catch (error) {
          // 删除令牌并转到登录页重新登录
          await store.dispatch('user/resetToken')
          Message.error(error || '验证失败，请重新登录')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    /* 没有token*/

    if (whiteList.indexOf(to.path) !== -1) {
      // 直接去登录白名单
      next()
    } else {
      // 没有访问权限的其他页面被重定向到登录页面
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }

  /* if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
    } else {
      if (store.getters.roles.length === 0) {
        store.dispatch('user/GetInfo').then(res => { // 拉取用户信息
          if (!res.roles || res.roles.length <= 0) {
            MessageBox.confirm('token已过期，可以取消继续留在该页面，或者重新登录', '确定登出', {
              confirmButtonText: '重新登录',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              store.dispatch('user/Logout').then(() => {
                location.reload() // 为了重新实例化vue-router对象 避免bug
              })
            })
          }
          next()
        }).catch((err) => {
          store.dispatch('user/Logout').then(() => {
            Message.error(err || '验证失败, 请重新登录')
            next({ path: '/' })
          })
        })
      } else {
        if (whiteListActiveList.indexOf(to.path) !== -1) {
          next()
        } else if (activeList.indexOf(to.path) !== -1) {
          next()
        } else if (allList.indexOf(to.path) !== -1) {
          next()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }*/
})

router.afterEach(() => {
  // 结束Progress
  NProgress.done()
})
