<template>
  <div class="menu-wrapper">
    <template v-for="(item,index) in items" v-if="items.length>0">
      <el-submenu :key="index" :index="index+''" v-if="checkShowOrHidden(item)">
        <template slot="title">
          <svg-icon v-if="item.parent.icon" :icon-class="item.parent.icon"></svg-icon>
          <span v-if="item.parent.menuName" slot="title">{{item.parent.menuName}}</span>
        </template>

        <template v-for="(child,index2) in item.sonItem" v-if="hasOneShowingChild(child)">
          <router-link :to="child.url" :key="index2">
            <el-menu-item :index="child.url" :key="child.menuName">
              <svg-icon v-if="child.icon" :icon-class="child.icon"></svg-icon>
              <span v-if="child.menuName" slot="title">{{child.menuName}}</span>
            </el-menu-item>
          </router-link>
        </template>
      </el-submenu>

    </template>
  </div>
</template>

<script>
import path from 'path'
import { isExternal } from '@/utils/validate'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItem',
  components: { Item, AppLink },
  mixins: [FixiOSBug], // 混入对象
  props: {
    // route object
    items: {
      type: Array
    },
    isNest: {
      type: Boolean,
      default: false
    },
    routes: {
      type: Array
    }
  },
  data() {
    // TODO: refactor with render function
    this.onlyOneChild = null
    return {
      isShow: false
    }
  },
  methods: {
    // hasOneShowingChild(children = [], parent) {
    //   const showingChildren = children.filter(item => {
    //     if (item.hidden) {
    //       return false
    //     } else {
    //       // Temp set(will be used if only has one showing child)
    //       this.onlyOneChild = item
    //       return true
    //     }
    //   })
    //
    //   // When there is only one child router, the child router is displayed by default
    //   if (showingChildren.length === 1) {
    //     return true
    //   }
    //
    //   // Show parent if there are no child router to display
    //   if (showingChildren.length === 0) {
    //     this.onlyOneChild = { ... parent, path: '', noShowingChildren: true }
    //     return true
    //   }
    //
    //   return false
    // },
    // resolvePath(routePath) {
    //   if (isExternal(routePath)) {
    //     return routePath
    //   }
    //   if (isExternal(this.basePath)) {
    //     return this.basePath
    //   }
    //   return path.resolve(this.basePath, routePath)
    // }

    hasOneShowingChild(children) {
      return (children.isShow === 1)
    },
    // 检测父菜单是否隐藏
    checkShowOrHidden(item) {
      return (item.parent.isShow === 1)
    }
  }
}
</script>
