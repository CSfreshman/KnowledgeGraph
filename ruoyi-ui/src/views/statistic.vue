<template>
  <div id="main-container">
    <el-row>
      <el-col :span="10">
        <div id="graph-container">
          <el-card>
            <div slot="header" class="card-header">
              <span>图谱数据统计</span>
            </div>
            <div id="node-container"></div>
            <div id="edge-container"></div>
          </el-card>
        </div>
      </el-col>

      <el-col :span="14">
        <div id="system-container">
          <el-card>
            <div slot="header" class="card-header">
              <span>系统数据统计</span>
            </div>

          </el-card>
        </div>
      </el-col>
    </el-row>






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
  #graph-container{
    background-color: #FFFFFF;

    height:100vh;

  }

  #system-container{
    background-color: #FEC171;

    height:100vh;

  }

  #node-container {
    background-color: #FFFFFF;
    width: 40vw;
    height:50vh;
  }

  #edge-container {
    background-color: #FFFFFF;
    width: 40vw;
    height:50vh;
  }

  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
