<template>
  <div id="main">
    <el-row>
      <el-col span="7">
        <div id="left">
          123
        </div>
      </el-col>
      <el-col span="17">
        <div id="right">
          132
          <div id="graph-panel"></div>
        </div>
      </el-col>
    </el-row>



  </div>
</template>

<script>

import VisGraph from '@/assets/test/js/graphvis.20230812.js'
import LayoutFactory from '@/assets/test/js/graphvis.layout.min.js'
import { config } from '@/assets/test/defaultConfig.js'
import { demoData } from '@/assets/test/demo2.js'

export default {
  name: "graphComputation",
  data(){
    return{
      visGraph : null,
      graphData:  {
        nodes:[{id:'1',label:'刘备',type:'男',properties:{age:50}},
          {id:'2',label:'关羽',type:'男'},
          {id:'3',label:'张飞',type:'男'}],
        links:[{source:'1',target:'2',label:'二弟',properties:{other:'other prop'}},
          {source:'1',target:'3',label:'三弟'}]
      }
    }
  },
  mounted(){
    this.visGraph = new VisGraph(document.getElementById('graph-panel'));//初始化绘图客户端
    this.visGraph.drawData(this.graphData);//绘制图完成
  }
}
</script>

<style scoped>
#main {

  width:100vw;
  height:100vh;
}

#left {

  height:100vh;
}

#right {

  height:100vh;
}

</style>
