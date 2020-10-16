module.exports = {
  publicPath: '/',
  productionSourceMap: false,
  devServer: { // 开发调试服务器配置项
    port: 80,
    open: true, // npm run serve后自动打开页面
    overlay: {
      warnings: false,
      errors: true
    }
  }/*,
  configureWebpack: {
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  }*/
}
