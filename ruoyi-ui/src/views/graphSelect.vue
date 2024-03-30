<template>
  <div id="main-container">
<!--    <div id="page-header">-->
<!--      -->

<!--    </div>-->

    <div id="page-body">
      <el-card v-if="showCondition">
        <div>
          <el-button @click="selectIndex = 1">实体查询</el-button>
          <el-button @click="selectIndex = 2">关系查询</el-button>
          <el-button @click="selectIndex = 3">类型联合查询</el-button>
          <el-button @click="doQuery()" icon="el-icon-search">查询</el-button>
          <el-button @click="" icon="el-icon-delete">清空表单</el-button>
        </div>

        <el-form :model="formData" label-width="100px" :inline="true">
          <el-row v-if="selectIndex == 1">
            <el-col>
              <el-form-item label="实体名称">
                <el-input size="mini" style="width: 100px;" v-model="formData.nodeName"></el-input>
              </el-form-item>
            </el-col>

          </el-row>

          <el-row v-if="selectIndex == 2">

            <!--            <el-form-item label="请输入关系名称">-->
            <!--              <el-input size="mini" style="width: 160px;" v-model="formData.edgeName"></el-input>-->
            <!--            </el-form-item>-->
            <el-form-item label="关系起点名称">
              <el-input size="mini" style="width: 100px;" v-model="formData.fromNodeName"></el-input>
            </el-form-item>

            <el-form-item label="关系终点名称">
              <el-input size="mini" style="width: 100px;" v-model="formData.toNodeName"></el-input>
            </el-form-item>
          </el-row>
          <div v-if="selectIndex == 3">
            <el-row>
              <el-col span="8">
                <el-form-item label="选择实体类型">
                  <el-select v-model="selectedNodeClassId" placeholder="选择实体类型" @change="selectNodeClass" size="mini">
                    <el-option
                      v-for="item in nodeClassList"
                      :key="item.id"
                      :label="item.label == null ? item.name : item.label"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col span="8">
                <el-form-item label="选择关系类型">
                  <el-select v-model="selectedEdgeClassId" placeholder="选择实体类型" @change="selectEdgeClass" size="mini">
                    <el-option
                      v-for="item in edgeClassList"
                      :key="item.id"
                      :label="item.label == null ? item.name : item.label"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-tag
                v-for="item in selectedNodeClassList"
                :key="item.id"
                closable
                @close="handleTagCloseForNodeClass(item)"
                style="margin-left: 10px;"
              >
                {{item.name}}
              </el-tag>
            </el-row>
            <el-row style="margin-top: 10px">
              <el-tag
                v-for="item in selectedEdgeClassList"
                :key="item.id"
                closable
                @close="handleTagCloseForEdgeClass(item)"
                style="margin-left: 10px;"
                type="success"
              >
                {{item.label}}
              </el-tag>
            </el-row>
          </div>

        </el-form>
      </el-card>
      <el-row>
<!--        <el-button>保留当前图谱</el-button>-->
        <el-button @click="showCondition = !showCondition">{{showCondition == true ? '隐藏表单' : '打开表单'}}</el-button>
      </el-row>

      <!-- 绘图面板区域 -->
      <div id="graph-panel" style="height: 100%"></div>
    </div>

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

<!--    下面的对话框不需要使用了-->
    <div>
      <el-dialog
        title="添加更多查询条件"
        :visible.sync="openDialog"
        :before-close="handleCloseDialog"
      >
        <el-form>
          <el-form-item label="添加实体">
            <el-input
              placeholder="请输出实体名称"
            >
              <template #suffix>
                <el-button style="border: none" icon="el-icon-plus" size="small" @click="" ></el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="添加关系（输入起点与终点实体，以空格分割，如:疾病 症状）">
            <el-input
              placeholder="请输出关系"
            >
              <template #suffix>
                <el-button style="border: none" icon="el-icon-plus" size="small" @click="" ></el-button>
              </template>
            </el-input>
          </el-form-item>

        </el-form>

        <div>
          <el-row>
            <el-tag></el-tag>
          </el-row>
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button @click="openDialog = false">取 消</el-button>
          <el-button type="primary" @click="openDialog = false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>


<script>

// 图谱可视化模块
import VisGraph from '@/assets/test/js/graphvis.20230812.js'
import LayoutFactory from '@/assets/test/js/graphvis.layout.min.js'
import { config } from '@/assets/test/defaultConfig.js'
import { demoData } from '@/assets/test/demo2.js'

import {getAllNodeClass} from "@/api/mange/class/node";
import {getAll as getAllEdgeClass} from "@/api/mange/class/edge";
import {getAllGraph, graphSelect} from "@/api/graph"

export default {
  name: "graphSelect",
  data() {
    return {
      // 选中的查询方式
      selectIndex: 1,
      showCondition: true,
      openDialog: false,
      nodeClassList: '',
      edgeClassList: '',
      formData: {
        nodeName: '',
        edgeName: '',
        fromNodeName: '',
        toNodeName: '',
        nodeList: [],
        edgeList: [],
      },
      // 单选框中选中的节点类型id
      selectedNodeClassId: '',
      // 已选择的节点的类型
      selectedNodeClassList: [],
      // 单选框中选中的边类型id
      selectedEdgeClassId: '',
      // 已选择的边的类型
      selectedEdgeClassList: [],

      savedGraph: '',


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
    selectNodeClass() {
      // 选择的节点
      var nodeClass = this.nodeClassList.find(it=>it.id == this.selectedNodeClassId);
      console.log(nodeClass);
      // 去重
      if(!this.selectedNodeClassList.find(it=>it.id == nodeClass.id)){
        this.selectedNodeClassList.push(nodeClass)
      }

      // 清空选择状态
      this.selectedNodeClassId = ''
    },

    selectEdgeClass() {
      // 选择的关系
      var edgeClass = this.edgeClassList.find(it=>it.id == this.selectedEdgeClassId);
      console.log(edgeClass);
      // 去重
      if(!this.selectedEdgeClassList.find(it=>it.id == edgeClass.id)){
        this.selectedEdgeClassList.push(edgeClass)
      }

      // 清空选择状态
      this.selectedEdgeClassId = ''
    },

    // 取消选择节点类型
    handleTagCloseForNodeClass(item) {
      console.log("取消的节点类型")
      console.log(item)
      const index = this.selectedNodeClassList.findIndex(element => element.id === item.id);
      if (index !== -1) {
        this.selectedNodeClassList.splice(index, 1);
      }
    },
    // 取消选择关系类型
    handleTagCloseForEdgeClass(item) {
      console.log("取消的关系类型")
      console.log(item)
      const index = this.selectedEdgeClassList.findIndex(element => element.id === item.id);
      if (index !== -1) {
        this.selectedEdgeClassList.splice(index, 1);
      }
    },

    // 点击查询
    doQuery() {
      console.log("查询参数");
      this.formData.nodeClassList = this.selectedNodeClassList;
      this.formData.edgeClassList = this.selectedEdgeClassList;
      // 查询方式，1为实体查询，2为关系查询，3为类型联合查询
      this.formData.selectIndex = this.selectIndex;
      console.log(this.formData)

      // 清空保留的图谱
      this.savedGraph = '';

      graphSelect(this.formData).then(resp=>{
        this.drawGraphData(0,resp)
      })
    },

    handleCloseDialog(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },

    // 图谱部分
    // 获取所有的知识节点信息
    async drawGraphData (id,data) {

      var newData = this.convertData(data);
      console.log(newData);

      if(this.savedGraph != ''){

      }

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
    getAllNodeClass({valid:1}).then(resp=>{
      this.nodeClassList = resp;
      console.log("可选择的节点类型⬇");
      console.log(this.nodeClassList)
    });

    getAllEdgeClass({valid:1}).then(resp=>{
      this.edgeClassList = resp;
      console.log("可选择的关系类型⬇");
      console.log(this.edgeClassList)
    });

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

  #page-header {
    background-color: #ffffff;
    width: 100vw;
    height: 30vh;
  }

  #page-body {
    background-color: #ffffff;
    width: 100vw;
    height: 100vh;
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
