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
            <div id="container1"></div>
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
import {extraStatistic} from "@/api/extra";

export default {
  name: "statistic",
  data() {
    return {
      nodeCountMap: {},
      edgeCountMap: {},
      extraData1: {}
    }
  },
  methods: {

    createChart1(data,elementId,name,legendData,recentDates){
      var chart = echarts.init(document.getElementById(elementId));
      chart.setOption({
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: legendData
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: recentDates
        },
        yAxis: {
          type: ''
        },
        series: [
          {
            name: '辅助诊断',
            type: 'line',
            stack: 'Total',
            data: data['辅助诊断']
          },
          {
            name: '路径分析',
            type: 'line',
            stack: 'Total',
            data: data['路径分析']
          },
          {
            name: '中心多度探寻',
            type: 'line',
            stack: 'Total',
            data: data['中心多度探寻']
          },
          {
            name: '中心度计算',
            type: 'line',
            stack: 'Total',
            data: data['中心度计算']
          },
          {
            name: '相似度计算',
            type: 'line',
            stack: 'Total',
            data: data['相似度计算']
          },
          {
            name: '图谱检索',
            type: 'line',
            stack: 'Total',
            data: data['图谱检索']
          }
        ]
      });
    },
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

    // 辅助函数：按日期升序排序并提取值
    processData(categoryData) {
      var sortedData = categoryData.sort(function(a, b) {
        var dateA = Object.keys(a)[0];
        var dateB = Object.keys(b)[0];
        return new Date(dateA) - new Date(dateB);
      });

      var values = sortedData.map(function(item) {
        return Object.values(item)[0];
      });

      return values;
    }

  },
  mounted() {

    statistic().then(resp=>{
      this.nodeCountMap = resp[0];
      this.edgeCountMap = resp[1];
      this.createNodeChart();
      this.createEdgeChart();
    })

    extraStatistic().then(resp=>{
      this.extraData1 = resp;
      var legendData = []
      for (var key in resp.name) {
        legendData.push(resp.name[key]);
      }
      console.log(legendData)

      // 处理所有类别的数据
      var processedData = {};
      for (var category in resp.data) {
        processedData[category] = this.processData(resp.data[category]);
      }
      console.log(processedData);


      // 创建一个空数组，用于保存日期
      var recentDates = [];

// 循环获取最近五天的日期
      for (var i = 4; i >= 0; i--) {
        // 创建一个新的日期对象，表示当前日期的前 i 天
        var currentDate = new Date();
        currentDate.setDate(currentDate.getDate() - i);

        // 获取月份和日期
        var month = currentDate.getMonth() + 1; // 月份是从 0 开始的，所以需要加 1
        var day = currentDate.getDate();

        // 将月份和日期格式化为两位数，如果小于 10，则在前面加上 '0'
        month = month < 10 ? '0' + month : month;
        day = day < 10 ? '0' + day : day;

        // 构建日期的字符串表示，格式为 MM-DD，并将其添加到数组中
        var formattedDate = month + '-' + day;
        recentDates.push(formattedDate);
      }

      console.log(recentDates); // 输出最近五天的日期数组，格式为 MM-DD


      this.createChart1(processedData,"container1",'',legendData,recentDates)
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
    background-color: #FFFFFF;
    width: 100vw;
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

  #container1 {
    background-color: #FFFFFF;
    width: 40vw;
    height:50vh;
  }

  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
