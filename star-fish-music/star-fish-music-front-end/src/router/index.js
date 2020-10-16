import Vue from 'vue'
import VueRouter from 'vue-router'

const LoginIn = () => import('@/views/LoginIn');
const SignUp = () => import('@/views/SignUp');
const Home = () => import('@/views/Home');
const SongList = () => import('@/views/SongList');
const Singer = () => import('@/views/Singer');
const MyMusic = () => import('@/views/MyMusic');
const SongListAlbum = () => import('@/views/SongListAlbum');
const SingerAlbum = () => import('@/views/SingerAlbum');
const MV = () => import('@/views/mv/MV')
const MVAlbum = () => import('@/views/mv/MVAlbum')
const Search = () => import('@/views/Search');
const Setting = () => import('@/views/Setting');
const Lyric = () => import('@/views/Lyric');

Vue.use(VueRouter);

const routes = [
  {
    path: '*',
    redirect: '/404'
  },
  {
    path: '/404',
    component: resolve => require(['@/views/404.vue'], resolve)
  },
  {
    path: '/login-in',
    name: 'login-in',
    component: LoginIn
  },
  {
    path: '/sign-up',
    name: 'sign-up',
    component: SignUp
  },
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/song-list',
    name: 'song-list',
    component: SongList
  },
  {
    path: '/my-music',
    name: 'my-music',
    component: MyMusic
  },
  {
    path: '/song-list-album/:id',
    name: 'song-list-album',
    component: SongListAlbum
  },
  {
    path: '/singer',
    name: 'singer',
    component: Singer
  },
  {
    path: '/singer-album/:id',
    name: 'singer-album',
    component: SingerAlbum
  },
  {
    path: '/mv',
    name: 'mv',
    component: MV
  },
  {
    path: '/mv-album/:id',
    name: 'mv-album',
    component: MVAlbum
  },
  {
    path: '/lyric/:id',
    name: 'lyric',
    component: Lyric
  },
  {
    path: '/search',
    name: 'search',
    component: Search
  },
  {
    path: '/setting',
    name: 'setting',
    component: Setting
  }
];

const router = new VueRouter({
  mode: 'history',
  routes,
  scrollBehavior(to, from, savedPosition) {
    return {x: 0, y: 0}
  }
});

export default router
