<template>
  <div id="main-container">
    <el-row>
      <el-col span="8">
        <div id="left-container">
          <el-card>
<!--            左侧搜索框-->
            <el-form>
              <el-form-item :inline="true">
                <el-input
                  placeholder="请输出关系类型名称"
                  v-model="queryParamsClass.label">
                  <template #suffix>
                    <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQuery"></el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
<!--            左侧类型表格-->
            <el-table v-loading="loading" :data="edgeClassList" :show-header="false" @row-click="changeSelectedEdgeClass">
              <el-table-column key="label" prop="label"></el-table-column>
            </el-table>
            <pagination
              v-show="totalClass>0"
              :total="totalClass"
              :page.sync="queryParamsClass.pageNum"
              :limit.sync="queryParamsClass.pageSize"
              @pagination="getEdgeClassList"
            />

          </el-card>
        </div>
      </el-col>
      <el-col span="16">
        <div id="right-container">
          <div id="network-body"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import {listClass} from "@/api/mange/class/edge"
import {getEdgeInstanceGraph} from "@/api/mange/instance/edge"
import vis from "vis";

export default {
  name: "edge",
  data() {
    return {
      queryParamsClass: {
        valid: '',
        pageNum: 1,
        pageSize: 5,
        label: ''
      },
      // 实例查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        valid: 1,
        label: ''
      },
      edgeClassList: '',
      totalClass: 0,
      loading: true,
      selectedEdge: '',
      selectedEdgeClass: '',
      graphNodes: [],
      graphEdges: [],
      network: ''
    }
  },
  mounted() {
    this.getEdgeClassList();
  },
  methods: {
    getEdgeClassList() {
      this.loading = true;
      this.queryParamsClass.valid = 1;
      listClass(this.queryParamsClass).then(resp=>{
        this.loading = false;
        this.totalClass = resp.total;
        this.edgeClassList = resp.rows;
      })
    },

    // 获得边的实例列表
    getEdgeInstanceList() {

    },

    // 获得边的实例-图谱形式
    getEdgeInstanceGraph() {
      getEdgeInstanceGraph(this.selectedEdgeClass.label).then(resp=>{

        // 对节点以及边的属性进行调整
        const newNodes = resp.nodes.map(node=>{
          if (node.props && node.props.color) {
            return { ...node, color: node.props.color };
          } else {
            return { ...node, color: '#FFFFFF' }; // 如果prop.color不存在，默认设置一个值
            //return {...node}
          }
        })

        const newEdges = resp.edges.map(edge=>{
          if (edge.props && edge.props.color) {
            return { ...edge, color: {color: edge.props.color} };
          } else {
            return {...edge, color: 'gray'}
          }
        })

        this.graphNodes = newNodes;
        this.graphEdges = newEdges;
        console.log(this.graphEdges)
        console.log(this.graphNodes)
        // 生成图谱
        this.createGraph();
      })
    },

    // 更换选择的边的类型
    changeSelectedEdgeClass(row) {
      this.selectedEdgeClass = row;
      this.getEdgeInstanceGraph();
    },

    createGraph() {
      console.log("开始生成图谱")
      // 建立拓扑图
      var container = document.querySelector('#network-body')
      var data = {
        nodes: new vis.DataSet(this.graphNodes),
        edges: new vis.DataSet(this.graphEdges),
      }
      var options = { // 对vis的一些设置
        edges: {
          color: {
            color: 'gray' // 默认边的颜色，如果未指定特定边的颜色
          },
          arrows: {
            to: { enabled: true, scaleFactor: 0.5, type: 'arrow' } //箭头的显示
          }
        }
      }
      // 初始化你的网络
      this.network = new vis.Network(container, data, options)

      console.log("生成图谱完成");
      console.log(this.network)
    }
  }
}
</script>

<style scoped>
  #main-container {
    background-color: #FFFFFF;
    width:100vw;
    height:100vh;
  }

  #left-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #right-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #network-body {
    background-color: #FFFFFF;
    height:100vh;
  }

</style>
