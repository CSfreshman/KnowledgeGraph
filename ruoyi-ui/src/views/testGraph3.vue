<template>
  <div id="main-container">

    <div class="graph-nav">
      <el-row :gutter="20">
        <el-col :span="8"><div class="grid-content bg-purple">图谱名称：{{knowlegeInfo.name}}</div></el-col>
        <el-col :span="8"><div class="grid-content bg-purple">图谱标识：{{knowlegeInfo.tag}}</div></el-col>
        <el-col :span="8"><div class="grid-content bg-purple">图谱描述：{{knowlegeInfo.desc}}</div></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8"><div class="grid-content bg-purple">实体数量：{{knowlegeInfo.nodeNum}}</div></el-col>
        <el-col :span="8"><div class="grid-content bg-purple">关系数量：{{knowlegeInfo.relationNum}}</div></el-col>
        <el-col :span="8"><div class="grid-content bg-purple">图谱权限：{{knowlegeInfo.permission}}</div></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="grid-content bg-purple">属性展开：
            <el-switch
              v-model="properShow"
              active-color="#13ce66"
              active-text="开"
              inactive-text="关"
              @change="changeProperShow()">
            </el-switch>
          </div>
        </el-col>
      </el-row>
    </div >

    <!--图显示区域-->
    <div class="graph-area" v-loading="loading">
      <!-- 绘图面板区域 -->
      <div id="graph-panel" style="width: 100%;height: 100%;"></div>

      <!-- 图例区域 -->
      <div id="graph-legend" class="legend-wrap">
        <div v-for="(item,index) in graphLegend" :key="index" class="legend-item">
          <div class="item-dot" :style="{'background-color': item.show ? item.color : 'rgb(210,210,210)' }" @click="showOrHideType(index)"></div>
          <div class="item-label" :style="{'color': item.show ? '' : 'rgb(210,210,210)' }">{{ item.type }}</div>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-item" @click="toolBarEvent('layout','fastFR')" title="网络布局"><i class="el-icon-cpu"></i></div>
        <div class="toolbar-item" @click="toolBarEvent('layout','hubsize')" title="层次布局"><i class="el-icon-share"></i></div>

        <div class="toolbar-item" @click="toolBarEvent('zoom','zoomOut')" title="放大"><i class="el-icon-zoom-in"></i></div>
        <div class="toolbar-item" @click="toolBarEvent('zoom','auto')" title="适中"><i class="el-icon-help"></i></div>
        <div class="toolbar-item" @click="toolBarEvent('zoom','zoomIn')" title="缩小"><i class="el-icon-zoom-out"></i></div>
      </div>
    </div>

    <!-- 节点右键操作菜单 -->
    <div id="nodeRightMenuPanel" class="right-menu-layer">
      <button @click="showNodeDeteail()"><i class="el-icon-notebook-2"></i>节点属性</button>
      <button @click="hideNode()"><i class="el-icon-s-release"></i>隐藏</button>
    </div>

    <!-- 连线右键操作对话栏 -->
    <div id="linkRightMenuPanel" class="right-menu-layer">
      <button @click="showLinkDetail()"><i class="el-icon-notebook-2"></i>关系属性</button>
      <button @click="hideLink()"><i class="el-icon-s-release"></i>隐藏</button>
    </div>

    <!-- 节点属性信息显示弹出层 -->
    <el-drawer title="节点属性信息" :modal-append-to-body="false" :wrapperClosable="false" :visible.sync="showNodeInfoDialog" size="380px" ref="drawer" >
      <div style="padding:5px 10px;">
        <div class="box-card">
          <el-form :model="currentNode" label-width="80px" size="mini">
            <el-form-item label="节点ID">
              <el-input type="text" v-model="currentNode.id" label="节点ID" disabled></el-input>
            </el-form-item>
            <el-form-item label="节点名称">
              <el-input type="text" v-model="currentNode.label" label="节点名称" disabled></el-input>
            </el-form-item>
            <el-form-item label="节点类型">
              <el-input type="text" v-model="currentNode.type" label="节点类型" disabled></el-input>
            </el-form-item>
            <el-form-item label="颜色(rgb)">
              <el-color-picker v-model="currentNode.color" :color-format="'rgb'" :show-alpha="false" disabled></el-color-picker>
            </el-form-item>
          </el-form>
        </div>

        <div class="box-card">
          <el-table
            :data="attrbutes"
            border
            size="small"
            style="width:100%;">
            <el-table-column prop="name" label="属性"></el-table-column>
            <el-table-column prop="type"  label="数据类型" width="100"></el-table-column>
            <el-table-column prop="unit" label="单位" width="100"></el-table-column>
          </el-table>
        </div>
      </div>
    </el-drawer>

    <!-- 属性节点显示弹出层 -->
    <el-drawer title="属性信息" :modal-append-to-body="false" :wrapperClosable="false" :visible.sync="showProperNodeInfo" size="380px" ref="drawer" >
      <div style="padding:5px 10px;">
        <div class="box-card">
          <el-form :model="properNode" label-width="80px" size="mini">
            <el-form-item label="名称">
              <el-input type="text" v-model="properNode.name" label="名称" disabled></el-input>
            </el-form-item>
            <el-form-item label="数据类型">
              <el-input type="text" v-model="properNode.type" label="数据类型" disabled></el-input>
            </el-form-item>
            <el-form-item label="单位">
              <el-input type="text" v-model="properNode.unit" label="单位" disabled></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-drawer>

    <!-- 连线属性信息显示弹出层 -->
    <el-drawer title="关系属性信息" :modal-append-to-body="false" :wrapperClosable="false" :visible.sync="showLinkInfoDialog" size="380px" ref="drawer" >
      <div style="padding:5px 10px;">
        <div class="box-card">
          <el-form :model="currentLink" label-width="80px" size="mini">
            <el-form-item label="关系ID">
              <el-input type="text" v-model="currentLink.id" label="关系ID" disabled></el-input>
            </el-form-item>
            <el-form-item label="关系类型">
              <el-input type="text" v-model="currentLink.type" label="关系类型" disabled></el-input>
            </el-form-item>
          </el-form>
        </div>

        <div class="box-card">
          <el-table
            :data="attrbutes"
            border
            size="small"
            style="width:100%;">
            <el-table-column prop="name" label="属性"></el-table-column>
            <el-table-column prop="type"  label="数据类型" width="100"></el-table-column>
            <el-table-column prop="unit" label="单位" width="100"></el-table-column>
          </el-table>
        </div>
      </div>
    </el-drawer>

    <!-- 节点或连线属性提示 -->
    <div id="tip-layer" class="tip-wrap" v-show="tipLayer.show">
      <div class="tip-header">{{tipLayer.header}}</div>
      <div class="tip-body">
        <el-table
          :data="tipLayer.data"
          border
          size="small"
          style="width:100%;">
          <el-table-column prop="name" label="属性"></el-table-column>
          <el-table-column prop="type"  label="数据类型" width="100"></el-table-column>
          <el-table-column prop="unit" label="单位" width="100"></el-table-column>
        </el-table>
      </div>
    </div>

  </div>

</template>

<script>
import VisGraph from '@/assets/test/js/graphvis.20230812.js'
import LayoutFactory from '@/assets/test/js/graphvis.layout.min.js'
import { config } from '@/assets/test/defaultConfig.js'
import { demoData } from '@/assets/test/demo2.js'
export default {
  data () {
    return {
      // visGraph实例对象
      visGraph: null,
      visLayout:null,//布局对象
      layoutLoopName:null,//布局循环对象
      // visGraph可视化交互配置
      config,
      //示例数据
      demoData,
      circleBgImage:require('@/assets/test/images/circle.png'),//节点背景图片
      // visGraph创建节点和连线数据集
      graphData: {
        nodes: [],
        links: []
      },
      graphLegend:[
        //{type:'图例一',color:'rgb(233,120,120)',show:true}
      ],//图例数组，根据数据的节点类型生成
      knowlegeInfo:{//图谱信息，需要服务端查询后赋值
        name:'公司网络图谱',
        tag:'公司',
        desc:'某某公司图谱信息',
        nodeNum:7,
        relationNum:9,
        permission:'私有'
      },
      properShow:false,//属性展示配置
      tipLayer:{ //提示层配置
        show : false, //是否显示提示层
        header:'提示信息', // 提示表头
        data:[] //提示内部的数据
      },
      currentNode: {}, // 选中的节点对象
      attrbutes:[],//选中节点或连线的属性列表
      currentLink: {},// 选中的连线对象
      //节点详细信息弹框
      showNodeInfoDialog:false,
      //关系连线信息弹层
      showLinkInfoDialog:false,
      //属性节点详细弹层控制开关
      showProperNodeInfo:false,
      properNode:{},//属性节点
      currentLayoutType:'fastFR',//当前布局类型，用于区分是否可以拖动
      loading: true
    }
  },
  methods: {
    // 获取所有的知识节点信息
    async drawGraphData (id) {
      this.graphData = this.demoData;
      if (this.visGraph === null) {
        this.createGraph()
        this.refreshGraphData()
      } else {
        this.refreshGraphData()
      }
      this.loading=false;
    },
    // 创建全局绘图客户端对象
    createGraph () {
      this.visGraph = new VisGraph(document.getElementById('graph-panel'), this.config)
    },
    // 刷新知识图谱数据
    refreshGraphData () {
      this.visGraph.drawData(this.graphData)
      this.visGraph.moveCenter(); //移动至中心位置
      this.generateLegend();//生成图例

      this.reDefinedNodes();//重新设定为自定义节点
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

      this.graphLegend = [];
      for(var legend of legendMap.values()){
        this.graphLegend.push(legend); //加入图例记录
      }
    },
    changeProperShow(){ //属性展示开关
      if(this.properShow){
        this.showProperNodes();
      }else{
        this.hideProperNodes();
      }
    },
    showProperNodes(){ //展示属性节点
      var that = this;
      var properNodes=[];//属性节点定义
      var relations=[]; //关系定义
      that.visGraph.nodes.forEach((node) =>{
        if(node.visible){
          var attributes = node.properties.attributes||[];//节点属性

          attributes.forEach((attr,index)=>{
            properNodes.push({
              id:`${node.id}-${index}`, //虚拟节点
              label:attr.name,
              type:'proper-virtual', //定义一个虚拟类型，用于后续处理
              x: node.x,
              y: node.y,
              color:'240,240,240', //属性节点的颜色
              fontColor:'50,50,50',//属性节点的字体颜色
              borderWidth:4, //属性节点边框宽度
              borderColor:'110,110,240',//属性节点的边框颜色
              properties:{
                attr:attr //自定义属性定义
              }
            });

            relations.push({
              id:`edge-${node.id}-${index}`,
              source:node.id,
              target:`${node.id}-${index}`,
              label:'属性',
              type:'proper-virtual',
              lineDash:[3,5], //属性设置虚线
              properties:{
                type:'virtual' //自定义属性定义
              }
            });
          });
        }
      });

      that.visGraph.addNodes(properNodes);//添加属性节点
      that.visGraph.addEdges(relations);//添加属性关系

      that.reLayout();
    },
    hideProperNodes(){//隐藏属性节点
      var that = this;
      var properNodes = that.visGraph.nodes.filter((node) =>{
        return node.type == 'proper-virtual';
      });

      if(properNodes.length>0){
        that.visGraph.deleteNodes(properNodes);
      }
    },
    // 执行布局算法
    reLayout (alpha) {
      var that = this;
      if(alpha == null){
        that.visLayout = null;
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
      }else{
        that.visLayout.alpha += (alpha>1?0.2:alpha); //继续运动
      }

      runLayout();//开始继续动画执行

      //通过动画帧控制控制布局算法的执行，有动画效果
      function runLayout(){
        cancelAnimationFrame(that.layoutLoopName);//停止动画控制
        that.visLayout.runLayout();  //运行布局算法
        that.visGraph.refresh();
        if(that.visLayout.alpha > 0.05){
          that.layoutLoopName = requestAnimationFrame(runLayout);
        }else{
          if(that.visGraph.currentNode && that.visGraph.currentNode.isDragging){
            that.visLayout.alpha = 0.1; //继续运动
            that.layoutLoopName = requestAnimationFrame(runLayout);
          }else{
            that.visLayout.alpha = 0; //停止运动
            cancelAnimationFrame(that.layoutLoopName);
          }
        }
      }
    },
    //显示节点信息
    showNodeDeteail(){
      if(this.currentNode.type == 'proper-virtual'){
        this.properNode = this.currentNode.properties.attr||{};
        this.showProperNodeInfo = true;//显示属性节点信息
      }else{
        this.showNodeInfoDialog = true;//显示实体节点信息
      }
      this.cancelNodeRightMenu();
    },
    showLinkDetail(){
      this.showLinkInfoDialog = true;
      this.cancelLinkRightMenu();
    },
    // 关闭节点提示工具栏
    cancelNodeRightMenu () {
      let nodeRightMenuLayer = document.getElementById('nodeRightMenuPanel')
      nodeRightMenuLayer.style.display = 'none'
    },
    // 关闭知识联系操作栏
    cancelLinkRightMenu () {
      let linkRightMenuLayer = document.getElementById('linkRightMenuPanel')
      linkRightMenuLayer.style.display = 'none'
    },
    showOrHideType(itemIndex){//图例的点击事件
      var legend = this.graphLegend[itemIndex];
      if(legend!=null){

        if(legend.show){ //需要隐藏类型节点
          legend.show = false;
        }else{ //需要显示类型节点
          legend.show = true;
        }
        this.graphLegend[itemIndex] = legend;

        this.renderNodesByLegend();//重新渲染节点图例
      }
    },
    renderNodesByLegend(){ //根据节点图例渲染
      var legendMap = new Map();
      this.graphLegend.forEach((item)=>{
        legendMap.set(item.type,item);
      });

      var that = this;
      var legend;
      var hideNodes=[];
      this.visGraph.nodes.forEach((node) =>{
        legend = legendMap.get(node.type)||{show:true};
        that.resetNodesVisible(node,true);
        if(!legend.show){
          hideNodes.push(node);
        }
      });
      hideNodes.forEach((node) =>{
        that.resetNodesVisible(node,false);
      });
    },
    resetNodesVisible(node,visible=true){ //显示或隐藏节点及其关系
      node.visible = visible;
      (node.inLinks||[]).forEach((link)=>{
        link.visible = visible;
      });
      (node.outLinks||[]).forEach((link)=>{
        link.visible = visible;

        //如果是虚拟属性节点,则跟着实体节点显示或隐藏
        if(link.target.type == 'proper-virtual'){
          link.target.visible = visible;
        }
      });
      this.visGraph.refresh();
    },
    showTipLayer(event){//显示提示层
      this.tipLayer.show = true;

      const tipDom = document.getElementById('tip-layer');
      tipDom.style.top = event.clientY+ 5  + 'px';
      tipDom.style.left = event.clientX + 10 +'px';
    },
    hideNode(){ //隐藏节点(画布上的删除)
      var properNodes = (this.visGraph.currentNode.outLinks||[]).map(function(link){
        return link.target.type == 'proper-virtual'?link.target:null;
      }).filter((node)=>{
        return node != null;
      });

      var that = this;
      if(properNodes.length>0){
        that.visGraph.deleteNodes(properNodes);//删除属性节点
      }
      this.visGraph.deleteNode(this.visGraph.currentNode);//删除当前节点

      this.cancelNodeRightMenu();
    },
    hideLink(){//隐藏连线
      this.visGraph.deleteLink(this.visGraph.currentLink);
      this.cancelLinkRightMenu();
    },
    toolBarEvent(eventType,type){//工具栏事件
      if(eventType == 'zoom'){
        this.visGraph.setZoom(type);
      }else if(eventType == 'layout'){
        cancelAnimationFrame(this.layoutLoopName);
        if(type == 'hubsize'){
          this.sortLayout();
        }else{
          this.forceLayout();
        }
      }
    },
    sortLayout(){ //树形结构布局
      this.currentLayoutType = 'hubsize';//设置当前布局类型
      var tempLayout = new LayoutFactory(this.visGraph.getGraphData()).createLayout('hubsize');
      tempLayout.resetConfig({
        'layerDistance':150,
        'nodeDistance':100,
        'sortMethod':'hubsize',
        'direction':'LR'
      });
      tempLayout.boolTransition=false;
      tempLayout.runLayout();

      this.visGraph.nodes.forEach(function(node) {
        node.fixed = true;
      });
      this.visGraph.moveCenter();
    },
    forceLayout(){ //力导向布局
      this.currentLayoutType = 'fastFR';//设置当前布局类型
      this.visGraph.nodes.forEach((node)=>{
        node.fixed = false;
      });
      var tempLayout = new LayoutFactory(this.visGraph.getGraphData()).createLayout('fastFR');
      tempLayout.resetConfig({
        friction: 0.8,
        linkDistance: 200,
        linkStrength: 0.2,
        charge: -250,
        gravity: 0.01,
        noverlap:false,
        size:[this.visGraph.stage.width,this.visGraph.stage.height]
      });

      var count=0;
      while(count++ <= 100){
        tempLayout.runLayout();
      }
      this.visGraph.moveCenter();
    },
    expandNode(node){ //双击扩展节点数据
      //TODO 需要从服务端查询节点数据,数据结构如demo2格式，以下为构造数据示例
      var newNodes = [],newRelations=[];
      for(var i=1;i<=10;i++){
        var tempId = Math.round(Math.random()*999999999);
        newNodes.push({
          id: 'expandnode-'+tempId,
          label:'扩展节点-'+i,
          type:'扩展节点类型',
          //color: this.randomColor()
        });

        newRelations.push({
          id: 'exedge-'+tempId,
          label:'扩展',
          source: node.id,
          target: 'expandnode-'+tempId
        });
      }

      //计算新节点的旋转坐标
      this.visGraph.incremaNodesCodinate(newNodes);
      this.visGraph.addNodes(newNodes);
      this.visGraph.addEdges(newRelations);

      this.reDefinedNodes();//重新设定为自定义节点
      this.reLayout();//执行动态布局计算
    },
    contractChildNode(node){ //收起节点的叶子节点
      var leafNodes = [],relations=[];
      (node.outLinks || []).forEach(function(l) {
        if ((l.target.outLinks || []).length == 0 && (l.target.inLinks || []).length == 1) {
          leafNodes.push(l.target);
          relations.push(l);
        }
      });

      var that = this;
      that.visGraph.deleteNodes(leafNodes);//删除叶子节点
      that.visGraph.deleteLinks(relations);//删除叶子连线

      this.reLayout();//执行动态布局计算
    },
    randomColor(){
      return Math.floor(255 * Math.random()) + "," +
        Math.floor(180 * Math.random()) + "," +
        Math.floor(255 * Math.random());
    },
    reDefinedNodes(){ //重新设置所有节点为自定义绘制方法（新增节点后调用一下）
      var that = this;
      this.visGraph.nodes.forEach((node) =>{that.drawDefinedNode(node);});
    },
    drawDefinedNode(node){ // 绘制自定义节点(需要在新增节点后注册)
      node.drawNode = function(ctx){ //绘制自定义节点
        if(!this.openAnimation){ //通过openAnimation控制是否绘制自定义
          //this.drawOriginalNode(ctx);//系统内置绘制方法
          ctx.save();
          ctx.beginPath();
          this.paintShape(ctx);
          ctx.closePath();

          /*if ((this.showSelected || this.selected) && this.selectedBorderWidth > 0) {
            ctx.lineWidth = this.borderWidth+this.selectedBorderWidth;
            ctx.strokeStyle = `rgba(${this.selectedBorderColor},${this.alpha*this.selectedBorderAlpha})`;
            ctx.stroke();
          }

          if (!this.selected && !this.showSelected  && this.borderWidth > 0) {
            ctx.lineWidth = this.borderWidth;
            ctx.strokeStyle = this.strokeStyle ? this.strokeStyle : `rgba(${this.borderColor},${this.alpha*this.borderAlpha})`;
            ctx.stroke();
          }*/

          //绘制节点填充色和阴影的设置
          if(this.fillColor){
            ctx.shadowBlur = 10, //阴影的区域大小
              ctx.shadowColor = `rgba(255,255,255,${this.alpha})`, //阴影颜色
              ctx.shadowOffsetX = 2, //阴影X轴偏移量
              ctx.shadowOffsetY = 2; //阴影Y轴偏移量

            ctx.fillStyle = this.fillStyle ? this.fillStyle : `rgba(${this.fillColor},${this.alpha})`;
            ctx.fill();
          }
          ctx.restore();

          if (this.image) {
            var globleAlpha = ctx.globalAlpha;
            ctx.save();
            ctx.globalAlpha = this.alpha;
            this.drawNodeImg(ctx, this.image, -this.width / 2, -this.height / 2, this.width / 2);
            ctx.globalAlpha = globleAlpha;
            ctx.restore();
          }
          this.paintText(ctx);

        }else{
          //以下部分为自定义绘制部分，支持动画效果
          this.animate = this.animate>50?10:this.animate;
          ctx.save();
          ctx.beginPath();
          this.paintShadow(ctx);
          ctx.arc(0, 0, this.radius, 0, 2*Math.PI);
          ctx.fillStyle=`rgba(${this.fillColor},${this.alpha})`;
          ctx.fill();
          ctx.strokeStyle = `rgba(${this.borderColor},${this.alpha})`;
          ctx.stroke();
          ctx.closePath();
          ctx.restore();

          if (this.image) { //有图片的场景，需要绘制图片
            var b = ctx.globalAlpha;
            ctx.save();
            ctx.globalAlpha = this.alpha;
            this.drawNodeImg(ctx, this.image,-this.radius,-this.radius,this.radius);
            ctx.globalAlpha = b;
            ctx.restore();
          }

          // 内环 - 间隔实线环
          this.angleStart = (this.angleStart==null || this.angleStart>=0.51)?0 : this.angleStart+=0.01;
          ctx.save();
          ctx.strokeStyle = `rgba(${this.selectedBorderColor},${this.alpha})`;
          for(var i=0;i<4;i++){
            ctx.beginPath();
            ctx.arc(0, 0, this.radius+3, Math.PI*(0.5*i), Math.PI*(this.angleStart+(0.5*i)));
            ctx.stroke();
            ctx.closePath();
          }
          ctx.restore();

          // 外环-虚线圆环
          ctx.save();
          ctx.beginPath();
          ctx.arc(0, 0, this.radius+8, 0, 2 * Math.PI);
          ctx.strokeStyle = `rgba(${this.selectedBorderColor},${this.alpha})`;
          ctx.setLineDash([3,2]);
          ctx.lineDashOffset=this.animate++;
          ctx.stroke();
          ctx.closePath();
          ctx.restore();

          //绘制文字，调用系统内置绘制文字方法
          this.paintText(ctx);
        }
      }
    },
    drawAnimateCircleNode(node){ // 绘制旋转背景图
      var that = this;

      //实例化图片，作为背景图
      if(node.circleBgImage == null){
        var circleImg = new Image();
        circleImg.setAttribute('crossOrigin', 'Anonymous');
        circleImg.src = that.circleBgImage; //图片资源
        circleImg.onload = function() {
          node.circleBgImage = circleImg; //图片加载完成赋值
        }
      }

      //绘制自定义节点方法重写
      node.drawNode = function(ctx){
        if(this.openAnimation){ //通过openAnimation控制是否绘制自定义
          this.animate = this.animate>360?0:this.animate+=0.02;
          var globleAlpha = ctx.globalAlpha;
          ctx.save();
          ctx.rotate(this.animate); //图片旋转动画控制
          ctx.globalAlpha = this.alpha;
          this.drawNodeImg(ctx,this.circleBgImage,-this.radius-15,-this.radius-15,this.radius+15);//绘制背景图
          ctx.globalAlpha = globleAlpha;
          ctx.restore();
        }

        this.drawOriginalNode(ctx);//调用系统内置绘制方法
      }
    }
  },
  created () {
    var that = this;

    //节点的点击事件
    this.config.node.onClick=function(event, node) {
      node.color = 'rgb('+node.fillColor+')';
      that.currentNode = node;
      that.tipLayer.header = node.label||'';
      that.tipLayer.data = node.properties.attributes||[]; //节点属性列表
      that.attrbutes = node.properties.attributes||[]; //节点属性列表
      that.showTipLayer(event);
    };

    //节点的双击事件
    this.config.node.ondblClick=function(event, node) {
      if(!node.isExpand){
        node.fixed = true; //固定位置
        node.isExpand = true; //展开标识
        //node.openAnimation = true; //启用节点动画特效

        that.expandNode(node);//节点双击展开

        that.drawDefinedNode(node); // 方案1：绘制自定义动画
        //that.drawAnimateCircleNode(node); //方案2：绘制指定背景图

        that.visGraph.switchAnimate(true); //开启全局动画开关（耗性能，按需开启和关闭）
      }else{
        node.fixed = false; //解除锁定
        node.isExpand = false; //展开标识关闭
        that.contractChildNode(node);//节点双击收起

        node.openAnimation = false; //关闭节点动画特效
        node.drawNode = null; //去掉自定义节点绘制
        that.visGraph.switchAnimate(false); //关闭全局动画（减小性能开销）
      }

      that.tipLayer.show = false; //关闭提示层
      that.generateLegend();//重新渲染图例类型
    };

    //节点鼠标弹起事件
    this.config.node.onMouseUp=function(event, node) {
      if(that.currentLayoutType == 'fastFR'){ //如果当前为力导向布局时才可触发力导计算
        that.reLayout(0.2);
      }
    };

    //节点拖拽事件
    this.config.node.onMousedrag=function(event, node) {
      that.tipLayer.show = false; //关闭提示层
      if(that.currentLayoutType == 'fastFR'){ //如果当前为力导向布局时才可触发力导计算
        that.reLayout(0.05);
      }
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
  mounted () {
    //初始化加载绘图
    this.drawGraphData()
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
/*****页面主要布局样式定义******/
.graph-nav{
  height: 80px;
  background-color: #fff;
  line-height: 26px;
  font-size: 13px;
  padding: 10px 20px;
}
.graph-area {
  position: relative;
  height: calc(100% - 105px);
  margin: 0 5px 5px 5px;
  padding: 0;
  background-color: #fff;
  border: 1px solid #ddd;
}

/******工具栏*******/
.toolbar{
  position: absolute;
  right: 0px;
  top: 0px;
  background: #efefef;
}

.toolbar .toolbar-item{
  width:40px;
  height:40px;
  line-height:40px;
  font-size:18px;
  text-align:center;
  color:#888;
  border-bottom:1px solid #ddd;
  cursor: pointer;
}

.toolbar .toolbar-item:hover{
  color:deepskyblue;
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
