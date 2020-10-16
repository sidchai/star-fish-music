<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    value: { // 展示数据
      type: Array
    },
    tagName: { // 标签名
      type: Array
    },
    type: { // 鼠标hover显示内容
      type: Number
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      const that = this
      this.chart = echarts.init(this.$el, 'macarons')

      that.chart.on('click', function(param) {
        that.$emit('clickBar', param.dataIndex)
      })

      that.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: that.tagName,
          axisTick: {
            alignWithLabel: true
          }
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          }
        }],
        series: [{
          name: that.type === 1 ? '歌单风格所占数目' : '歌手类型所占数目',
          type: 'bar',
          stack: 'vistors',
          barWidth: '60%',
          data: that.value,
          animationDuration
        }]
      })
    }
  }
}
</script>
