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
          <div id="right-head">
            <el-row v-show="selectedEdgeClass != ''">
              <el-button @click="addEdgeInstance">新建关系</el-button>
<!--              <el-select v-model="fromNodeClass" placeholder="选择起点实体类型"></el-select>-->
<!--              <el-select v-model="toNodeClass" placeholder="选择起点实体类型"></el-select>-->
            </el-row>


          </div>
          <div id="network-body"></div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form :model="form">
        <el-form-item label="label">
          <el-input v-model="this.form.label" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="起点">
          <el-select v-model="form.fromNodeId" placeholder="选择起点">
            <el-option
              v-for="item in fromNodeList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="终点">
          <el-select v-model="form.toNodeId" placeholder="选择终点">
            <el-option
              v-for="item in toNodeList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <div v-for="(prop, index) in form.props" :key="index">
          <el-row><el-col>
            <el-form-item :label="prop.key" label-width="100">
              <el-input v-model="prop.value"></el-input>
            </el-form-item>
          </el-col></el-row>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import {listClass} from "@/api/mange/class/edge"
import {listClassProperties} from "@/api/mange/class/edgeProperties"
import {getEdgeInstanceGraph, addEdgeInstance} from "@/api/mange/instance/edge"
import {getAllByClassId} from "@/api/mange/instance/node"
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
      network: '',

      fromNodeList: '',
      toNodeList: '',

      // 是否打开对话框
      open: false,
      // 新建关系表单
      form: {
        classId: '',
        label: '',
        // fromNode: '',
        fromNodeId: '',
        fromNodeNeo4jId: '',
        // toNode: '',
        toNodeId: '',
        toNodeNeo4jId: '',
        props: []
      },
      title: '',
      edgeClassPropsList: []
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
      console.log("选择的类型");
      console.log(this.selectedEdgeClass);
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
    },

    resetForm() {
      this.form.label = '';
      this.form.classId = '';
      this.form.fromNodeId = '';
      this.form.fromNodeNeo4jId = '';
      this.form.toNodeId = '';
      this.form.toNodeNeo4jId = '';
      this.form.props = [];

    },

    addEdgeInstance() {
      this.resetForm();
      this.title = "添加新的关系类型";
      this.open = true;
      this.form.classId = this.selectedEdgeClass.id;
      this.form.label = this.selectedEdgeClass.label;

      // 查询该关系对应的起点实体以及终点实体
      var queryParamsFrom = {nodeClassId:this.selectedEdgeClass.fromNodeId}
      var queryParamsTo = {nodeClassId:this.selectedEdgeClass.toNodeId}
      getAllByClassId(queryParamsFrom).then(resp=>{
        this.fromNodeList = resp
      })
      getAllByClassId(queryParamsTo).then(resp=>{
        this.toNodeList = resp
      })

      // 还要查询有哪些属性
      var edgeClassPropQueryParams = {
        valid: 1,
        edgeId: this.selectedEdgeClass.id
      };
      listClassProperties(edgeClassPropQueryParams).then(resp=>{
        this.edgeClassPropsList = resp.rows;
        this.edgeClassPropsList.forEach(it=>{
          this.form.props.push({key:it.name,value: ''})
        })
      })


    },

    submit() {
      var fromNode = this.fromNodeList.find(it=>it.id == this.form.fromNodeId)
      var toNode = this.toNodeList.find(it=>it.id == this.form.toNodeId)

      console.log(fromNode)
      console.log(toNode)
      this.form.fromNodeNeo4jId = fromNode.neo4jId;
      this.form.toNodeNeo4jId = toNode.neo4jId
      console.log("表单数据")
      console.log(this.form)
      addEdgeInstance(this.form).then(resp=>{

      })


    },

    cancel() {
      this.resetForm();
      this.open = false;
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

  #right-head {
    height:10vh;
    background-color: #00afff;
  }

  #network-body {
    background-color: #FFFFFF;
    height:90vh;
  }

</style>
