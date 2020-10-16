import defaultSettings from '@/settings'

const title = defaultSettings.title || '星鱼音乐后台管理'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
