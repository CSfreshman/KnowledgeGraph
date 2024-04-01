<template>
  <div id="main-container">
    <div id="charts-container" style="display: flex;">
      <div id="node-container"></div>
      <div id="edge-container"></div>
    </div>



<!--    <el-row>-->
<!--      <el-col :spane="12"><div id="node-container"></div></el-col>-->
<!--      <el-col :spane="12"><div id="edge-container"></div></el-col>-->
<!--    </el-row>-->
  </div>
</template>

<script>
import * as echarts from 'echarts';
import {statistic} from "@/api/graph";

export default {
  name: "statistic",
  data() {
    return {
      nodeCountMap: {},
      edgeCountMap: {},
    }
  },
  methods: {

    createChart(data,elementId,name){
      // 基于准备好的dom，初始化echarts实例
      var chart = echarts.init(document.getElementById(elementId));
      chart.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: name,
            type: 'pie',
            radius: ['30%', '60%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 30,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      });
    },

    createNodeChart(){

      var chartData = Object.entries(this.nodeCountMap).map(([key, value]) => ({ value, name: key }));
      this.createChart(chartData,"node-container","节点数量")
    },

    createEdgeChart(){

      var chartData = Object.entries(this.edgeCountMap).map(([key, value]) => ({ value, name: key }));
      this.createChart(chartData,"edge-container","关系数量")

    },
  },
  mounted() {

    statistic().then(resp=>{
      this.nodeCountMap = resp[0];
      this.edgeCountMap = resp[1];
      this.createNodeChart();
      this.createEdgeChart();
    })

  }
}
</script>

<style scoped>
  #main-container {
    background-color: #FFFFFF;
    width:100vw;
    height:100vh;
  }
  #charts-container{
    background-color: #FFFFFF;
    width: 100vw;
    height:50vh;
  }
  #node-container {
    background-color: #FFFFFF;
    width: 50vw;

  }

  #edge-container {
    background-color: #FFFFFF;
    width: 50vw;

  }
</style>
