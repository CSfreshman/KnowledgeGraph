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
            <el-table v-loading="loading1" :data="edgeClassList" :show-header="false" @row-click="changeSelectedEdgeClass">
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
          <div id="graph-panel"></div>
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
            <el-form-item :label="prop.name" label-width="100">
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

    <!-- 连线右键操作对话栏 -->
    <div id="linkRightMenuPanel" class="right-menu-layer">
      <button @click="goToDetail1()"><i class="el-icon-setting"></i>查看详情</button>
    </div>
  </div>
</template>

<script>

import {listClass} from "@/api/mange/class/edge"
import {listClassProperties} from "@/api/mange/class/edgeProperties"
import {getEdgeInstanceGraph, addEdgeInstance} from "@/api/mange/instance/edge"
import {getAllByClassId} from "@/api/mange/instance/node"
import vis from "vis";
import {config} from "@/assets/test/defaultConfig";
import VisGraph from "@/assets/test/js/graphvis.20230812";
import LayoutFactory from "@/assets/test/js/graphvis.layout.min";

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
      loading1: true,
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
      edgeClassPropsList: [],

      // 图谱可视化模块数据
      graphData: {
        nodes: [],
        links: []
      },
      // 选中的节点对象
      currentNode: {},
      attrbutes:[],//选中节点或连线的属性列表
      // 选中的连线对象
      currentLink: {},
      config,
      // visGraph实例对象
      visGraph: null,
      visLayout:null,//布局对象
      layoutLoopName:null,//布局循环对象
      graphLegend:[],
      loading: true,
      tipLayer:{ //提示层配置
        show : false, //是否显示提示层
        header:'提示信息', // 提示表头
        data:[] //提示内部的数据
      },
      allExistNodeClass: [],
    }
  },
  mounted() {
    this.getEdgeClassList();
  },
  methods: {

    goToDetail1() {
      var data = {flag:0,edge:this.currentLink};
      this.$router.push({
        path: '/nodeDetail',
        query: {data}
      })
    },
    getEdgeClassList() {
      this.loading1 = true;
      this.queryParamsClass.valid = 1;
      listClass(this.queryParamsClass).then(resp=>{
        this.loading1 = false;
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
        // this.createGraph();
        this.drawGraphData(0,{nodes:this.graphNodes,edges:this.graphEdges});
      })
    },

    // 更换选择的边的类型
    changeSelectedEdgeClass(row) {
      this.selectedEdgeClass = row;
      console.log("选择的类型");
      console.log(this.selectedEdgeClass);
      this.getEdgeInstanceGraph();
    },

    // createGraph() {
    //   console.log("开始生成图谱")
    //   // 建立拓扑图
    //   var container = document.querySelector('#network-body')
    //   var data = {
    //     nodes: new vis.DataSet(this.graphNodes),
    //     edges: new vis.DataSet(this.graphEdges),
    //   }
    //   var options = { // 对vis的一些设置
    //     edges: {
    //       color: {
    //         color: 'gray' // 默认边的颜色，如果未指定特定边的颜色
    //       },
    //       arrows: {
    //         to: { enabled: true, scaleFactor: 0.5, type: 'arrow' } //箭头的显示
    //       }
    //     }
    //   }
    //   // 初始化你的网络
    //   this.network = new vis.Network(container, data, options)
    //
    //   console.log("生成图谱完成");
    //   console.log(this.network)
    // },

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
          this.form.props.push({name:it.name,value: it.defaultValue})
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
        this.open = false;
        this.$modal.msgSuccess("添加成功");
        // 重新请求数据
        this.getEdgeInstanceGraph();
      })


    },

    cancel() {
      this.resetForm();
      this.open = false;
    },

    // 图谱部分
    // 获取所有的知识节点信息
    async drawGraphData (id,data) {

      var newData = this.convertData(data);
      console.log(newData);

      this.graphData = newData;
      // 环状布局
      // this.customLayout();
      if (this.visGraph === null) {

        this.createGraph()
        this.refreshGraphData()
      } else {
        this.refreshGraphData()
      }
      this.loading=false;


    },

    customLayout() {
      var radius = 100; // 同心圆半径
      var angle = Math.PI * 2 / this.graphData.nodes.length; // 计算节点之间的角度间隔

      // 根据节点类型分配节点到不同的同心圆
      this.graphData.nodes.forEach(function(node, index) {
        if (node.labels[0] === '根节点') {
          node.x = radius * Math.cos(angle * index);
          node.y = radius * Math.sin(angle * index);
        } else if (node.labels[0] === '疾病') {
          node.x = 2 * radius * Math.cos(angle * index);
          node.y = 2 * radius * Math.sin(angle * index);
        }
        else if (node.labels[0] === '并发症') {
          node.x = 3 * radius * Math.cos(angle * index);
          node.y = 3 * radius * Math.sin(angle * index);
        }
        // 其他类型以此类推
      });
    },

    // 将后端的图谱数据转换成图谱对应的数据结构
    convertData(originalData) {
      console.log("originalData")
      console.log(originalData)
      var newData =  {
        nodes: originalData.nodes.map(node=>({
          "id": String(node.id),
          "label": node.label,
          "labels": node.labels,
          "color": node.props.color ? node.props.color : "#FFFFFF",
          "type": node.labels[0],
          "properties": {
            "attributes": Object.keys(node.props).map(key => ({
              "name": key,
              "value": node.props[key]
            }))
          }
        })),
        links: originalData.edges.map(edge=>({
          "id": String(edge.id),
          "source": edge.from,
          "target": edge.to,
          "type": edge.label,
          "properties":{
            "attributes": Object.keys(edge.props).map(key => ({
              "name": key,
              "value": edge.props[key]
            }))
          }
        }))
      };
      return newData;
    },

    // 创建全局绘图客户端对象
    createGraph () {
      this.visGraph = new VisGraph(document.getElementById('graph-panel'), this.config)
      //this.visGraph.switchAnimate(true);
    },
    // 刷新知识图谱数据
    refreshGraphData () {
      this.visGraph.drawData(this.graphData)
      //this.visGraph.setZoom('auto')
      this.graphLegend = [];
      this.generateLegend();//生成图例
      // 刷新图谱的时候更新图例

      this.reLayout();
    },
    generateLegend(){ //生成图例
      var legendMap = new Map();
      this.graphLegend.forEach((item)=>{
        legendMap.set(item.type,item);
      });

      this.visGraph.nodes.forEach((node) =>{
        if(!legendMap.get(node.type)){
          legendMap.set(node.type,{
            type:node.type,
            color:`rgb(${node.fillColor})`,
            show:true
          });
        }
      });

      for(var legend of legendMap.values()){
        this.graphLegend.push(legend); //加入图例记录
      }
    },
    reLayout () {
      var that = this;

      that.visLayout = null;//置空原有布局对象
      that.visLayout = new LayoutFactory(this.visGraph.getGraphData()).createLayout('fastFR');
      that.visLayout.resetConfig({
        friction: 0.8,
        linkDistance: 200,
        linkStrength: 0.2,
        charge: -250,
        gravity: 0.01,
        noverlap:false,
        size:[that.visGraph.stage.width,that.visGraph.stage.height]
      });
      runLayout();//开始继续动画执行

      //通过动画帧控制控制布局算法的执行，有动画效果
      function runLayout(){
        cancelAnimationFrame(that.layoutLoopName);//停止动画控制
        that.visLayout.runLayout();  //运行布局算法
        that.visGraph.refresh();
        if(that.visLayout.alpha > 0.1){
          that.layoutLoopName = requestAnimationFrame(runLayout);
        }else{
          if(that.visGraph.currentNode && that.visGraph.currentNode.isDragging){
            that.visLayout.alpha = 0.5; //继续运动
            that.layoutLoopName = requestAnimationFrame(runLayout);
          }else{
            that.visLayout.alpha = 0; //停止运动
            cancelAnimationFrame(that.layoutLoopName);
          }
        }
      }
    },
    //节点开始拖拽
    dragStart(event){
      event.stopPropagation();
    },
    //完成节点拖拽至画布操作
    dropEnd(event){
      event.stopPropagation();
      var that = this;
      this.visGraph.addNodeForDrag({
        id:'temp-node'+that.randomId(),
        label:'新节点'
      },function(node){
        //TODO 节点添加到画布后，需要设置后保存至服务端
        console.log('节点已添加至画布',node);
        // 不是编辑节点
        that.edit = false;
        that.currentNode = node;
        that.showEditnodeRightMenuLayer();
      });
    },
    randomId(){
      return Math.round(Math.random() * 99999999);
    },

    //显示提示层
    showTipLayer(event){
      this.tipLayer.show = true;

      const tipDom = document.getElementById('tip-layer');
      tipDom.style.top = event.clientY + 5 + 'px';
      tipDom.style.left = event.clientX + 10 +'px';
    },
  },
  created () {
    var that = this;

    //节点的点击事件
    this.config.node.onClick=function(event, node) { //节点点击事件回调
      node.color = 'rgb('+node.fillColor+')';
      that.currentNode = node;
      that.tipLayer.header = node.label||'';
      that.tipLayer.data = node.properties.attributes||[]; //节点属性列表
      that.attrbutes = node.properties.attributes||[]; //节点属性列表
      that.showTipLayer(event);
    };

    //节点的双击事件
    this.config.node.ondblClick=function(event, node) { //节点双击事件回调
      /* node.color = 'rgb('+node.fillColor+')';
      that.currentNode = node;
      that.attrbutes = node.properties.attributes||[];
      that.showNodeDeteail(); */

      node.openAnimation = true; //启用节点动画特效
      that.drawDefinedNode(node); // 注册自定义节点绘制方法
      that.visGraph.switchAnimate(true);//开启动画示例
    };

    //节点拖拽事件
    this.config.node.onMousedrag=function(event, node) { //节点拖拽事件
      that.tipLayer.show = false; //关闭提示层
      //that.reLayout();
    };

    //连线的点击事件
    this.config.link.onClick=function(event, link) { //节点点击事件回调
      that.currentLink = link;
      that.tipLayer.header = link.label||'';
      that.tipLayer.data = link.properties.attributes||[]; //关系属性列表
      that.attrbutes = link.properties.attributes||[]; //关系属性列表

      that.showTipLayer(event);
    };

    //空白处的点击事件
    this.config.noElementClick=function(event){
      that.currentNode = {};
      that.currentLink = {};
      that.tipLayer.show = false; //关闭提示层
    };

    //右键配置
    this.config.rightMenu = {
      nodeMenu:{ //节点右键菜单
        nodeRightMenuLayer : null,
        init:function(_graphvis){
          if(!that.nodeRightMenuLayer){
            that.nodeRightMenuLayer = document.getElementById('nodeRightMenuPanel');
          }
        },
        show : function(event,_graphvis){
          this.init();
          if(that.nodeRightMenuLayer){
            that.nodeRightMenuLayer.style.left = (event.clientX + 10) + 'px'
            that.nodeRightMenuLayer.style.top = (event.clientY - 5) + 'px'
            that.nodeRightMenuLayer.style.display = 'block'
          }
          var node = _graphvis.currentNode;
          node.color = 'rgb('+node.fillColor+')';
          that.currentNode = node;
          that.attrbutes = node.properties.attributes||[];
        },
        hide : function(){
          if(that.nodeRightMenuLayer){
            that.nodeRightMenuLayer.style.display = 'none'
          }
        }
      },
      linkMenu:{  //连线右键菜单
        linkDialog : null,
        init:function(_graphvis){
          if(!that.linkDialog){
            that.linkDialog = document.getElementById('linkRightMenuPanel');
          }
        },
        show : function(event,_graphvis){
          this.init();
          if(that.linkDialog){
            that.linkDialog.style.left = (event.clientX + 10) + 'px'
            that.linkDialog.style.top = (event.clientY - 5) + 'px'
            that.linkDialog.style.display = 'block'
          }
          var link = _graphvis.currentLink;
          that.currentLink = link;
          that.attrbutes = link.properties.attributes||[];
        },
        hide : function(){
          if(that.linkDialog){
            that.linkDialog.style.display = 'none'
          }
        }
      }
    }
  },
  destroyed(){
    cancelAnimationFrame(this.layoutLoopName);
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
    background-color: #FFFFFF;
  }

  #graph-panel {
    background-color: #FFFFFF;
    height:90vh;
  }
  /*****节点弹出鼠标提示层样式*****/
  .tip-wrap{
    position: absolute;
    width: 350px;
    height: auto;
    min-height: 150px;
    background: #fff;
    box-shadow: 0px 0px 10px #999;
    font-size: 14px;
  }
  .tip-wrap > .tip-header{
    height:30px;
    line-height: 30px;
    padding: 5px 10px;
    border-bottom: 1px solid #ddd;
  }
  .tip-wrap > .tip-body{
    padding: 0 10px 10px;
  }
  /*****右键菜单样式******/
  .right-menu-layer {
    position: absolute;
    width: 100px;
    z-index: 5;
    display: none;
    border-radius: 3px;
    overflow: hidden;
    background: #fafafa;
    border: 1px solid #e1e2e2;
    box-shadow: 0 0 5px #ddd;
    padding: 5px 3px;
  }
  .right-menu-layer button {
    display: block;
    height:24px;
    line-height: 24px;
    background: transparent;
    border: none;
    color: #444;
    text-align:center;
    cursor: pointer;
  }
  .right-menu-layer button > i{
    margin-right: 5px;
  }
  .right-menu-layer button:hover {
    color: slateblue;
  }
  .right-menu-layer button:focus {
    outline:0;
  }

  .box-card{
    box-shadow: 0 0 3px #d4c8c8;
    padding: 10px;
    margin-bottom: 10px;
  }
</style>
