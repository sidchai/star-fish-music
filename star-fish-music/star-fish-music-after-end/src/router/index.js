import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout/index'

/**
 *  constantRoutes : 不需要动态刷新的路由
 * 不需要权限进入的页面
 *  所有角色都可以访问
 */
export const constantRoutes = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },

  { path: '/404', component: () => import('@/views/error-page/404'), hidden: true },

  { path: '/401', component: () => import('@/views/error-page/401'), hidden: true },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    alwaysShow: true,
    name: '网站管理',
    meta: {
      title: '网站管理',
      icon: 'manage'
    },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: '网站首页',
        meta: { title: '网站首页', icon: 'dashboard' }
      },
      {
        path: 'deploy',
        component: () => import('@/views/dashboard/system/index'),
        name: '网站配置',
        meta: { title: '网站配置', icon: 'deploy' }
      },
      {
        path: 'sys-dict-type',
        component: () => import('@/views/dashboard/sys-dict/index'),
        name: '字典管理',
        meta: { title: '字典管理', icon: 'sys-dict-type' }
      },
      {
        path: 'sys-dict',
        hidden: true,
        component: () => import('@/views/dashboard/sys-dict/SysDictData'),
        name: '字典数据',
        meta: { title: '字典数据', icon: 'sys-dict' }
      }
    ]
  },

  {
    path: '/authority',
    component: Layout,
    redirect: '/authority/admin',
    alwaysShow: true,
    name: '权限管理',
    meta: {
      title: '权限管理',
      icon: 'authority'
    },
    children: [
      {
        path: 'admin',
        name: '管理员管理',
        component: () => import('@/views/authority/admin/index'),
        meta: { title: '管理员管理', icon: 'admin' }
      },
      {
        path: 'role',
        name: '角色管理',
        component: () => import('@/views/authority/role/index'),
        meta: { title: '角色管理', icon: 'role' }
      },
      {
        path: 'admin-recycle',
        name: '管理员回收站',
        component: () => import('@/views/authority/admin/AdminRecycle'),
        meta: { title: '管理员回收站', icon: 'recycle' }
      },
      {
        path: 'button',
        name: '按钮管理',
        component: () => import('@/views/authority/button/index'),
        meta: { title: '按钮管理', icon: 'button' }
      },
      {
        path: 'category-menu',
        name: '菜单管理',
        component: () => import('@/views/authority/category-menu/index'),
        meta: { title: '菜单管理', icon: 'category-menu' }
      }
    ]
  },

  {
    path: '/user',
    component: Layout,
    redirect: '/user/user-manage',
    name: '用户管理',
    meta: {
      title: '用户管理',
      icon: 'peoples'
    },
    children: [
      {
        path: 'user-manage',
        component: () => import('@/views/user/index'),
        name: '用户管理',
        meta: {
          title: '用户管理',
          icon: 'user'
        }
      },
      {
        path: 'user-recycle',
        component: () => import('@/views/user/UserRecycle'),
        name: '用户回收站',
        meta: { title: '用户回收站', icon: 'recycle' }
      }
    ]
  },

  {
    path: '/carousel',
    component: Layout,
    redirect: '/carousel/carousel',
    name: '轮播图管理',
    meta: {
      title: '轮播图管理',
      icon: 'carousel'
    },
    children: [
      {
        path: 'carousel',
        component: () => import('@/views/carousel/index'),
        name: '轮播图管理',
        meta: {
          title: '轮播图管理',
          icon: 'carousel'
        }
      }
    ]
  }

  ,
  {
    path: '/singer',
    component: Layout,
    redirect: '/singer/singer-manage',
    name: '歌手管理',
    meta: {
      title: '歌手管理',
      icon: 'singer'
    },
    children: [
      {
        path: 'singer-manage',
        component: () => import('@/views/singer/index'),
        name: '歌手管理',
        meta: {
          title: '歌手管理',
          icon: 'singer-manage'
        }
      },
      {
        path: 'song',
        component: () => import('@/views/singer/Song'),
        name: '歌曲管理',
        meta: {
          title: '歌曲管理',
          icon: 'song'
        }
      }
    ]
  },

  {
    path: '/song-list',
    component: Layout,
    redirect: '/song-list/song-list-manage',
    name: '歌单管理',
    meta: {
      title: '歌单管理',
      icon: 'song-list'
    },
    children: [
      {
        path: 'song-list-manage',
        component: () => import('@/views/song-list/index'),
        name: '歌单管理',
        meta: {
          title: '歌单管理',
          icon: 'song-list'
        }
      },
      {
        path: 'content',
        component: () => import('@/views/song-list/Content'),
        name: '歌单内容',
        meta: {
          title: '歌单内容',
          icon: 'content'
        }
      }
    ]
  },

  {
    path: '/message',
    component: Layout,
    redirect: '/message/comment',
    name: '消息管理',
    meta: {
      title: '消息管理',
      icon: 'message'
    },
    children: [
      {
        path: 'comment',
        component: () => import('@/views/message/comment/index'),
        name: '评论管理',
        meta: {
          title: '评论管理',
          icon: 'comment'
        }
      }
    ]
  },

  {
    path: '/mv',
    component: Layout,
    redirect: '/mv/mv-manage',
    name: 'MV管理',
    meta: {
      title: 'MV管理',
      icon: 'mv'
    },
    children: [
      {
        path: 'mv-manage',
        component: () => import('@/views/mv/index'),
        name: 'MV管理',
        meta: {
          title: 'MV管理',
          icon: 'mv'
        }
      }
    ]
  },

  // 关于我 页面信息
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: '个人信息',
        meta: { title: '个人信息', icon: 'user', noCache: true }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
