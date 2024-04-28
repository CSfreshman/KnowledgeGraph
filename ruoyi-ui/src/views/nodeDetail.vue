<template>
  <div>

    <el-row>
      <el-col :span="6">
        <el-card>
          <div slot="header" class="card-header">
            <span>属性列表</span>
          </div>
          <div id="prop-container">
            <el-row>
              <div>
                <el-row>
                  <template>
                    <el-tag
                      v-for="(labelItem, index) in showData.labels"
                      :key="index"
                      size="medium"
                      class="rounded-tag"
                      style="margin-top: 10px; margin-left: 10px"
                    >
                      {{labelItem}}
                    </el-tag>

                    <el-tag
                      size="medium"
                      class="rounded-tag"
                      style="margin-top: 10px; margin-left: 10px"
                    >{{showData.label}}</el-tag>
                  </template>
                </el-row>
                <!--                <template>-->
                <!--                  <el-row>-->
                <!--                    <el-col v-for="(value, key) in chooseNode.props" :key="key">-->
                <!--                      <p>{{ key }}: {{ value }}</p>-->
                <!--                    </el-col>-->
                <!--                  </el-row>-->
                <!--                </template>-->
              </div>
            </el-row>
            <el-row>
              <template>
                <div style="margin-top: 15px;">
                  <el-table :data="tableData" :show-header="false" style="width: 100%"  :border=true stripe>
                    <el-table-column>
                      <template slot-scope="scope">
                        <span>{{ scope.row.key }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column>
                      <template slot-scope="scope">
                        <span>{{ scope.row.value }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column>
                      <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="updateProp(scope.row)" size="mini" circle></el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-row>
          </div>
        </el-card>

      </el-col>
      <el-col :span="18">
        <div id="network-container">
          <el-card>
            <div slot="header" class="card-header">
              <el-row>
                <el-col span="9">
                  {{ headText }}
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(1)" :type="degree === 1?'primary':''">1度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(2)" :type="degree === 2?'primary':''">2度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(3)" :type="degree === 3?'primary':''">3度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="deleteNode">删除</el-button>
                </el-col>
              </el-row>
            </div>
            <div id="graph-panel"></div>
            <div id="network-bottom"></div>
          </el-card>

        </div>
      </el-col>
  </el-row>


    <div>
      <el-dialog title="修改属性" :visible.sync="open" width="500px" append-to-body>
        <el-form :model="form">
          <div v-for="(prop, index) in form.props" :key="index">
            <el-form-item :label="prop.key" label-width="400">
              <el-input v-model="prop.value"></el-input>
            </el-form-item>
          </div>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submit">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {getNodeDetail, updateNodeDetail, deleteNode, getSingleEdgeByEdgeId} from "@/api/nodeDetail"
import vis from "vis";
import {config} from "@/assets/test/defaultConfig";
import VisGraph from "@/assets/test/js/graphvis.20230812";
import LayoutFactory from "@/assets/test/js/graphvis.layout.min";

export default {
  name: "node",
  data() {
    return{
      degree: 1,
      routerData: '',
      headText: '图谱：',
      nodes: '',
      edges: '',
      // 该详情页对应的节点
      mainData: '',
      // 点击的节点
      showData: '',
      // 表格中展示的数据（选中的节点的属性）
      tableData: '',
      nodeId: '',
      edgeId: '',
      flag: '',
      open: false,
      form: {
        nodeId: '', // 节点id
        props: []
      },

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
  mounted(){
    this.routerData = this.$route.query.data;
    console.log(this.$route.query)
    this.flag = this.routerData.flag;
    if(this.flag === 1){
      this.nodeId = this.routerData.node.id;
      this.getNodeDetail(this.nodeId,1);
    }else{
      this.edgeId = this.routerData.edge.id;
      this.getEdgeDetail(this.edgeId);
    }



  },
  methods: {

    getEdgeDetail(edgeId){
      getSingleEdgeByEdgeId(edgeId).then(resp=>{
        console.log("=============")
        console.log(resp)
        console.log("=============")

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
            return {...edge}
          }
        })

        this.nodes = newNodes;
        this.edges = newEdges;


        this.drawGraphData(0,{nodes:this.nodes,edges:this.edges});
        //this.createNetwork()

        this.mainData = this.edges.find(edge=>edge.id == this.edgeId);
        console.log("mainData:" + this.mainData)

        this.showData = this.mainData;
        this.transformData(this.showData,true)
      })
    },

    getNodeDetail(nodeId,degree){
      getNodeDetail(nodeId,degree).then(resp=>{
        console.log("=============")
        console.log(resp)
        console.log("=============")

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
            return {...edge}
          }
        })

        this.nodes = newNodes;
        this.edges = newEdges;

        // 选中的节点高亮
        var highLightNode = this.nodes.find(node=>node.id == this.nodeId);
        highLightNode.color = 'red';
        var index = this.nodes.findIndex(node => node.id === this.nodeId);
        this.nodes[index] = highLightNode;
        console.log(this.nodes)

        this.drawGraphData(0,{nodes:this.nodes,edges:this.edges});
        //this.createNetwork()

        this.mainData = this.nodes.find(node=>node.id == this.nodeId);
        console.log("mainData:" + this.mainData)
        console.log("nodeId:" + this.nodeId)
        this.showData = this.mainData;
        this.transformData(this.showData,false)
      })
    },

    createNetwork() {
      // 建立拓扑图
      var container = document.querySelector('#network-body')
      var data = {
        nodes: new vis.DataSet(this.nodes),
        edges: new vis.DataSet(this.edges),
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
      //
      // var self = this;
      //
      //
      // this.network.on("afterDrawing",() => {
      //    self.network.focus(self.nodeId);
      // });
    },

    changeDegree(degree) {
      this.degree = degree;
      this.getNodeDetail(this.nodeId,degree);
    },

    // 对一条数据进行格式转换，方便在横向表格中展示
    transformData(data,isEdge) {
      let rawData = data.props;
      const transformedData = [];
      transformedData.push({key: 'id', value: data.id})
      transformedData.push({ key: 'label', value: data.label});
      if(isEdge){
        transformedData.push({key: 'fromId', value: data.from})
        transformedData.push({key: 'fromName', value: this.nodes.find(node=>node.id == data.from).label})
        transformedData.push({key: 'toId', value: data.to})
        transformedData.push({key: 'toName', value: this.nodes.find(node=>node.id == data.to).label})
      }
      for (const key in rawData) {
        transformedData.push({ key, value: rawData[key] });
      }
      this.tableData = transformedData;
    },

    // 处理修改属性的按钮
    updateProp(row) {
      console.log(row)
      this.form.nodeId = this.nodeId;
      this.form.props = [];
      this.form.props.push({key: row.key, value: row.value})
      this.open = true;
    },

    // 处理点击提交按钮
    submit() {
      this.open = false;
      if(this.flag === 1){
        // 节点
        updateNodeDetail(this.form).then(resp=>{
          this.getNodeDetail(this.nodeId,1);
        })
      }else{
        // 关系

      }

    },

    cancel() {
      this.open = false;
      this.form.props = [];
    },

    // 删除节点
    deleteNode(){
      if(this.flag === 1){
        deleteNode(this.nodeId).then(resp=>{
          this.$router.push({
            path: '/graph'
          })
        })
      }else{
        deleteEdge(this.edgeId).then(resp=>{
          this.$router.push({
            path: '/graph'
          })
        })
      }

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


  }
}
</script>

<style scoped>

  #main-container {
    background-color: #FFFFFF;
    width:100vw;
    height:100vh;
  }

  #prop-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #network-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #network-head {
    background-color: #FFFFFF;
    height:8vh;
  }

  #graph-panel {
    background-color: #FFFFFF;
    height:92vh;
  }

  /*#network-bottom {*/
  /*  background-color: green;*/
  /*  height:25vh;*/
  /*}*/

  .rounded-tag {
    border-radius: 20px; /* 调整圆角大小 */
    color: #000000;
  }

  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
