<template>
  <div id="main-container">
    <el-row>
      <el-col :span="7" v-if="showLeft">
        <!--    左侧表单内容-->
        <div id="left-container">

          <!--      头部-->
          <div>
            <el-row>
              <el-col :span="12">
                <el-button size="medium" @click="showCenterDegree = true">中心度计算</el-button>
              </el-col>
              <el-col :span="12">
                <el-button size="medium" @click="showCenterDegree = false">相似实体计算</el-button>
              </el-col>
            </el-row>
            <el-divider></el-divider>
          </div>

<!--          中心度计算部分-->
          <div v-if="showCenterDegree">
            <!-- 选择计算模型-->
            <div>
              <el-row>设置中心度计算模型</el-row>
              <div>
                <el-radio-group v-model="selectedCenterDegreeModel" class="radio-group">
                  <template v-for="model in centerDegreeModel">
                    <el-row style="margin-top: 10px">
                      <el-radio :label="model.id">{{ model.name }}</el-radio>
                    </el-row>
                  </template>
                </el-radio-group>
              </div>

              <el-divider></el-divider>
            </div>

            <!-- 选择参与计算实体种类-->
            <div>
              <el-row> 设置分析实体 </el-row>
              <div>
                <template>
                  <div>
                    <el-checkbox-group v-model="checkedNode">
                      <el-row>
                        <el-col :span="12" v-for="(item, index) in nodeCheckBoxList" :key="item.id">
                          <el-checkbox :label="item" :disabled="!item.isEnable">{{ item.name }}</el-checkbox>
                        </el-col>
                      </el-row>
                    </el-checkbox-group>
                  </div>
                </template>
              </div>
              <el-divider></el-divider>
            </div>

            <div>
              <el-row>
                <el-button @click="calculation">计算</el-button>
              </el-row>
            </div>

          </div>

        </div>
      </el-col>

      <el-col :span="showLeft ? 17 : 24">
        <!--    图谱-->
        <div id="right-container">
          <el-button @click="showLeft = !showLeft"  size="medium">{{this.showLeft?"隐藏表单":"显示表单"}}</el-button>
          <!-- 绘图面板区域 -->
          <div id="graph-panel" style="height: 100%"></div>
        </div>
      </el-col>
    </el-row>


    <!-- 节点或连线属性提示 -->
    <div id="tip-layer" class="tip-wrap" v-show="tipLayer.show">
      <div class="tip-header">{{tipLayer.header}}    <el-button @click="tipLayer.show = false">取消</el-button></div>
      <div class="tip-body">
        <el-table
          :data="tipLayer.data"
          border
          size="small"
          style="width:100%;">
          <el-table-column prop="name" label="属性"></el-table-column>
          <el-table-column prop="value"  label="属性值"></el-table-column>
          <!--          <el-table-column prop="unit" label="单位" width="100"></el-table-column>-->
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
// 图谱可视化模块
import VisGraph from '@/assets/test/js/graphvis.20230812.js'
import LayoutFactory from '@/assets/test/js/graphvis.layout.min.js'
import { config } from '@/assets/test/defaultConfig.js'
import { demoData } from '@/assets/test/demo2.js'

import {getAll as getAllEdgeClass} from "@/api/mange/class/edge";
import {getAllNodeClass} from "@/api/mange/class/node";
import {getAll as getAllNodeInstance} from "@/api/mange/instance/node"
import {centerMultiDegree, centralityCalculation, getAllGraph, pathAnalyse} from "@/api/graph";

export default {
  name: "graphComputation",
  data() {
    return {
      // 展示中心度计算
      showCenterDegree: true,
      // 中心度模型
      centerDegreeModel: [
        {id:1, name: "Degree Centrality"},
        {id:2, name: "Close Centrality"},
        {id:3, name: "Between Centrality"},
      ],
      selectedCenterDegreeModel: 1,
      showLeft: true,
      showPath: true,
      isShortest: true,
      allEdgeClassList: [],
      allNodeClassList: [],
      edgeCheckBoxList: [],
      nodeCheckBoxList: [],
      checkedEdge: [],
      checkedNode: [],
      maxDegree: '',
      fromNode: {name:''},
      toNode: {name: ''},
      queryNodeList: [],
      popoverVisibleFrom: false,
      popoverVisibleTo: false,
      selectedItem: '',
      analyseNode: {name: ''},  // 分析对象实体
      selectedDegree: 1,

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
    }
  },
  methods: {
    // 填充备选关系列表，渲染勾选框
    fillEdgeCheckBoxList() {
      this.allEdgeClassList.forEach(it=>{
        it.isEnable = true;
        it.isChecked = false;
        this.edgeCheckBoxList.push(it);
      })
      console.log("分析关系列表");
      console.log(this.edgeCheckBoxList);
    },
    fillNodeCheckBoxList() {
      this.allNodeClassList.forEach(it=>{
        it.isEnable = true;
        it.isChecked = false;
        this.nodeCheckBoxList.push(it);
      })
      console.log("分析实体列表");
      console.log(this.nodeCheckBoxList);
    },

    // 执行计算动作
    calculation() {
      var formData = {
        selectedCenterDegreeModel: this.selectedCenterDegreeModel,
        nodeClassList: this.checkedNode,
      }

      console.log("中心度计算表单数据如下")
      console.log(formData)

      centralityCalculation(formData).then(resp=>{
        this.drawGraphData(0,resp.graph);
      })
    },

    // 执行分析动作
    analyse() {
      // 路径分析
      if(this.showPath){
        var formData = {
          edgeClassList: this.checkedEdge,
          fromNode: this.fromNode,
          toNode: this.toNode,
          isShortest: this.isShortest,
          maxDegree: this.maxDegree
        }
        console.log("路径分析表单数据如下")
        console.log(formData)

        pathAnalyse(formData).then(resp=>{
          this.drawGraphData(0,resp);
        })
      }else {
        // 中心多度探寻
        var formData = {
          edgeClassList: this.checkedEdge,
          nodeClassList: this.checkedNode,
          analyseNode: this.fromNode, // 分析实体在表单中使用的是fromNode
          selectedDegree: this.selectedDegree
        }

        console.log("中心多读探寻分析表单数据如下")
        console.log(formData)
        centerMultiDegree(formData).then(resp=>{
          this.drawGraphData(0,resp);
        })

      }


    },

    reset(){

    },

    // 根据名称查询节点
    queryNode(isFromNode) {
      var queryNodeName = '';
      if(isFromNode){
        queryNodeName = this.fromNode.name;
      }else{
        queryNodeName = this.toNode.name;
      }
      getAllNodeInstance({valid:1,name:queryNodeName}).then(resp=>{
        console.log(resp);
        this.queryNodeList = resp;
        if(isFromNode){
          this.popoverVisibleFrom = true;
        }else{
          this.popoverVisibleTo = true;
        }

      })
    },

    handleSelectChange(value,isFromNode) {
      if(isFromNode){
        this.fromNode = value
      }else{
        this.toNode = value
      }
      if(isFromNode){
        this.popoverVisibleFrom = false; // 关闭 popover
      }else {
        this.popoverVisibleTo = false; // 关闭 popover
      }

      // 选择完成后需要情况这些状态
      this.selectedItem = '';
      this.queryNodeList = '';
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
  mounted() {
    getAllEdgeClass({valid:1}).then(resp=>{
      this.allEdgeClassList = resp;
      console.log("所有关系类型如下:")
      console.log(this.allEdgeClassList)
      this.fillEdgeCheckBoxList();
    });

    getAllNodeClass({valid:1}).then(resp=>{
      this.allNodeClassList = resp;
      console.log("所有节点类型如下:")
      console.log(this.allNodeClassList)
      this.fillNodeCheckBoxList();
    })

    getAllGraph().then(resp=>{
      console.log("开始绘制图谱")
      this.drawGraphData(0,resp)
    })

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
  background-color: #ffffff;

  height:100vh;
}

.radio-group {
  white-space: nowrap;
}


/*****页面主要布局样式定义******/
.page-header{
  position: relative;
  height: 30px;
}

.page-header .drag-dot-wrap{
  position:absolute;
  top:5px;
  left:10px;
  width: 120px;
}

.drag-dot-wrap .drag-dot{
  display:inline-block;
  width: 30px;
  height: 30px;
  border-radius:15px;
  background-color: dodgerblue;
  cursor: move;
  touch-action: none;
}

.drag-dot-wrap .drag-dot:hover{
  background-color: deepskyblue;
}

.drag-dot-wrap .dot-label{
  position: absolute;
  top: 6px;
  left: 55px;
  font-size: 13px;
  color: #222;
}
.page-header .swtich-wrap{
  position:absolute;
  top:15px;
  right:25px;
  font-size: 13px;
}

.graph-area {
  position: relative;
  height: calc(100% - 75px);
  margin: 0 5px 5px 5px;
  padding: 0;
  background-color: #fafafa;
  border: 1px solid #ddd;
}

/*******图例区域样式定义*****/
.legend-wrap{
  position: absolute;
  left:10px;
  bottom: 10px;
}

.legend-wrap > .legend-item{
  height:24px;
  line-height: 24px;
  font-size: 12px;
}

.legend-item > .item-dot{
  float: left;
  display: inline-block;
  height:20px;
  width: 20px;
  border-radius: 10px;
  background-color: rgb(210,210,210);
  cursor: pointer;
}

.legend-item > .item-label{
  display: inline-block;
  float: left;
  margin-left: 5px;
  max-width: 120px;
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
