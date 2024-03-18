export const config = {
  node: { // 节点的默认配置
    label: { // 标签配置
      show: true, // 是否显示
      color: '50,50,50', // 字体颜色
      font: '12px 微软雅黑', // 字体大小及类型
      wrapText: false, // 节点包裹文字 (节点大小由文字大小决定，绘制较耗性能)
      textPosition: 'Bottom_Center',// 文字位置 Middle_Center,Bottom_Center
      //textOffsetY:8, //文字竖向偏移距离
      //background:'250,250,250', //文字背景颜色
      //borderColor:'100,120,220' //背景边框
    },
    shape: 'circle', // 节点形状 circle
    color: '50,155,230', // 节点颜色
    borderColor: '255,255,255', // 边框颜色
    borderWidth: 0, // 边框宽度,
    lineDash: [0], // 边框虚线间隔,borderWidth>0时生效
    alpha: 1, // 节点透明度
    size: 66, // 节点大小
    selected: { // 选中时的样式设置
      borderColor: '50,120,230', // 选中时边框颜色
      borderAlpha: 1, // 选中时的边框透明度
      borderWidth: 4, // 选中是的边框宽度
      showShadow: true, // 是否展示阴影
      shadowBlur:30, //阴影范围大小
      shadowColor: '50,80,250'// 选中是的阴影颜色
    }
  },
  link: { // 连线的默认配置
    label: { // 连线标签
      show: true, // 是否显示
      color: '50,50,50', // 字体颜色
      font: '12px 微软雅黑',// 字体大小及类型
      background:'250,250,250',
      borderColor:'250,250,250'
    },
    lineType: 'direct', // 连线类型,direct,curver,
    arrowType:'thired',
    colorType: 'defined', // 连线颜色类型 source:继承source颜色,target:继承target颜色 both:用双边颜色，defined:自定义
    color: '145,145,155', // 连线颜色
    alpha: 1, // 连线透明度
    lineWidth: 2, // 连线宽度
    lineDash: [0], // 虚线间隔样式如：[5,8]
    showArrow: true, // 显示箭头
    selected: { // 选中时的样式设置
      color: '50,120,230', // 选中时的颜色
      alpha: 1,
      showShadow: false, // 是否展示阴影
      shadowColor: '250,40,30'// 选中连线时的阴影颜色
    }
  },
  highLightNeiber: true, // 相邻节点高亮开关
  wheelZoom: 0.8// 滚轮缩放开关，不使用时不设置[0,1]
}
