<template>
  <div>
    <div id="mynetwork"></div>
    <div id="cricle" class="cricle">
      <span>选项</span>
      <span>选项</span>
      <span>选项</span>
      <span>选项</span>
      <span>选项</span>
      <span>选项</span>
    </div>
  </div>
</template>

<script>
import vis from 'vis'

export default {
  name: "testGraph",
  mounted () {
    alert("执行")
    this.globalTrace();
    alert("执行完成")
  },
  methods: {
    globalTrace () {
      // 创建节点
      var nodes = new vis.DataSet([
        {id: 1, label: 'Node 1',image:'https://ai-open-platform-1251483553.cos.ap-guangzhou.myqcloud.com/m1.png',shape: 'circularImage'},
        {id: 2, label: 'Node 2',image:'https://ai-open-platform-1251483553.cos.ap-guangzhou.myqcloud.com/m1.png',shape: 'circularImage'},
        {id: 3, label: 'Node 3',image:'https://ai-open-platform-1251483553.cos.ap-guangzhou.myqcloud.com/m1.png',shape: 'circularImage'},
        {id: 4, label: 'Node 4',image:'https://ai-open-platform-1251483553.cos.ap-guangzhou.myqcloud.com/m1.png',shape: 'circularImage'},
        {id: 5, label: 'Node 5',image:'https://ai-open-platform-1251483553.cos.ap-guangzhou.myqcloud.com/m1.png',shape: 'circularImage'}
      ]);

      // 创建关系
      var edges = new vis.DataSet([
        {from: 1, to: 2},
        {from: 2, to: 4},
        {from: 2, to: 5},
      ]);

      var container = document.getElementById('mynetwork');
      var data = {
        nodes: nodes,
        edges: edges
      };

      // 节点相关参数
      var options = {
        interaction:{hover:true},
        // locale: 1.5,
        height:'100%',
        width:'100%',
        layout:{
          randomSeed: 2,
          // hierarchical: {
          //   direction: 'LR',
          //   sortMethod:'directed'
          // }
        },
        nodes: {
          borderWidth:4,
          size:20,
          font: {
            color:'#333',
            size: 14
          },
          color: {
            border: 'rgba(167, 173, 221, 1)',
            hover:'rgba(2, 126, 238, 1)',
            highlight:'rgba(2, 126, 238, 1)'
          }
        },
        edges: {
          arrows: {
            to: { enabled: true, scaleFactor: 0.5, type: 'arrow' } //箭头的显示
          },
          color: {
            color:'rgba(167, 173, 221, 1)',
            hover:'rgba(2, 126, 238, 1)',
            highlight:'rgba(2, 126, 238, 1)'
          },
          font:{
            color:'#999',
            size:12
          }
        }
      };
      // 创建拓扑图对象
      var network = new vis.Network(container, data, options);
      var view = this;
      // 全局事件监听
      network.on("click", function(e){
        console.log(e)
        console.log(network)
        // 获取节点canvas坐标
        let p = network.getPosition(e.nodes[0]);
        console.log(p)
        // 获取缩放尺寸
        let scale = network.getScale();
        console.log(scale)
        // canvas->dom 坐标转化
        console.log(network.canvasToDOM({x:p.x, y:p.y}))
        let domPosi = network.canvasToDOM({x:p.x, y:p.y});

        // 设置环形位置
        view.setCriclePosition(domPosi.x - 50, domPosi.y - 50, scale);
      })
      // network.on("dragStart", function(e){
      //   console.log(e)
      //   view.hideCricle();
      // })
      // network.on("dragging", function(e){
      //   console.log(e)
      // })
      // network.on("dragEnd", function(e){
      //   console.log(e)
      // })
      // network.on("controlNodeDragging", function(e){
      //   console.log(e)
      //   view.hideCricle();
      // })
      // network.on("zoom", function(e){
      //   console.log(e)
      //   view.hideCricle();
      // })
    },
    // 定位div
    setCriclePosition(x,y, scale){
      alert("执行定位div")
      let cricle = document.getElementById("cricle");
      cricle.style = `left: ${x}px; top: ${y}px;transform:scale(${scale});display:block;`;
    },

    // 隐藏div
    hideCricle(){
      let cricle = document.getElementById("cricle");
      cricle.style = `display:none;`;
    }
  }
}
</script>

<style scoped>
body{
  padding:0;
  margin:0;
}
#mynetwork {
  width: 100%;
  height: 600px;
  border: 1px solid lightgray;
}
.cricle{
  position: absolute;
  left: 0px;
  top: 0px;
  width: 100px;
  height: 100px;
  border: 30px solid rgba(200,200,200,0.7);
  border-radius: 50px;
  box-sizing: border-box;
  z-index: 10;
  display: none;
}
.cricle > span{
  position: absolute;
  left: 0;
  right: 0;
  width: 30px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  font-size: 10px;
  cursor: pointer;
}
.cricle > span:hover{
  color: #1b68ff;
}
.cricle > span:nth-child(1){
  left: 5px;
  top: -25px;
}
.cricle > span:nth-child(2){
  left: 37px;
  top: -5px;
}
.cricle > span:nth-child(3){
  left: 37px;
  top: 25px;
}
.cricle > span:nth-child(4){
  left: 5px;
  top: 44px;
}
.cricle > span:nth-child(5){
  left: -27px;
  top: 25px;
}
.cricle > span:nth-child(6){
  left: -27px;
  top: -5px;
}
</style>
