<template>
  <div id="main-container">

    <!-- 页面顶部部分 -->
    <div class="page-header">
      <div class="drag-dot-wrap">
        <div class="drag-dot" draggable="true" @dragstart="dragStart"></div>
        <div class="dot-label">拖拽节点</div>
      </div>
      <div class="swtich-wrap">
        <div class="grid-content">属性展开：
          <el-switch
            v-model="properShow"
            active-color="#13ce66"
            active-text="开"
            inactive-text="关"
            @change="changeProperShow()">
          </el-switch>
        </div>
      </div>
    </div>

    <!--图显示区域-->
    <div class="graph-area" v-loading="loading">
      <!-- 绘图面板区域 -->
      <div id="graph-panel" @dragenter.prevent  @dragover.prevent @drop="dropEnd" style="width: 100%;height: 100%;"></div>

      <!-- 图例区域 -->
      <div id="graph-legend" class="legend-wrap">
        <div v-for="(item,index) in graphLegend" :key="index" class="legend-item">
          <div class="item-dot" :style="{'background-color': item.show ? item.color : 'rgb(210,210,210)' }" @click="showOrHideType(index)"></div>
          <div class="item-label" :style="{'color': item.show ? '' : 'rgb(210,210,210)' }">{{ item.type }}</div>
        </div>
      </div>
    </div>

    <!-- 节点右键操作菜单 -->
    <div id="nodeRightMenuPanel" class="right-menu-layer">
      <button @click="showNodeDeteail()"><i class="el-icon-notebook-2"></i>节点属性</button>
      <button @click="showEditnodeRightMenuLayer()"><i class="el-icon-setting"></i>节点设置</button>
      <el-popover placement="top" :width="180" ref="popoverNode">
        <p>您确定要删除该节点吗?</p>
        <div style="text-align: right; margin: 0">
          <el-button size="mini" type="text" @click="popoverNodeClose()">取消</el-button>
          <el-button type="primary" size="mini" @click="deleteNodeClick()">确定</el-button>
        </div>
        <template #reference>
          <button><i class="el-icon-delete"></i>删除节点</button>
        </template>
      </el-popover>
      <button @click="connectLink()"><i class="el-icon-share"></i>节点连接</button>
    </div>

    <!-- 连线右键操作对话栏 -->
    <div id="linkRightMenuPanel" class="right-menu-layer">
      <button @click="showLinkDetail()"><i class="el-icon-notebook-2"></i>关系属性</button>
      <button @click="resetLinkInfo()"><i class="el-icon-setting"></i>关系设置</button>
      <el-popover placement="top" :width="180" ref="popoverLink">
        <p>您确定要删除该关系吗?</p>
        <div style="text-align: right; margin: 0">
          <el-button size="mini" type="text" @click="popoverLinkClose()">取消</el-button>
          <el-button type="primary" size="mini" @click="deleteLinkClick()">确定</el-button>
        </div>
        <template #reference>
          <button><i class="el-icon-delete"></i>删除关系</button>
        </template>
      </el-popover>
    </div>

    <!-- 节点设置弹框 -->
    <el-drawer title="节点编辑" :visible.sync="editNodeDialog" :close-on-click-modal="false" ref="drawer" width="380px">
      <div>
        <el-form :model="editNode" label-width="100px">
          <el-form-item label="节点ID">
            <el-input v-model="editNode.id" disabled></el-input>
          </el-form-item>
          <el-form-item label="节点名称">
            <el-input v-model="editNode.label"></el-input>
          </el-form-item>
          <el-form-item label="节点图标">
            <el-select v-model="editNode.image" placeholder="请选择">
              <el-option
                v-for="item in imageArray"
                :key="item.id"
                :label="item.label"
                :value="item.icon">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="颜色(rgb)">
            <el-color-picker v-model="editNode.color" :color-format="'rgb'" :show-alpha="false"></el-color-picker>
          </el-form-item>
        </el-form>
        <div style="text-align: center;">
          <el-button @click="editNodeDialog=false">取 消</el-button>
          <el-button type="primary" @click="saveNodeInfo()">确 定</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 连线完成后，弹出关系属性设置 -->
    <el-drawer title="关系属性设置" :visible.sync="relationSetPanel" :close-on-click-modal="false" ref="drawer" width="380px">
      <div>
        <el-form :model="relationInfo" label-width="100px">
          <el-form-item label="起始实体">
            <el-input v-model="relationInfo.source"></el-input>
          </el-form-item>
          <el-form-item label="目标实体">
            <el-input v-model="relationInfo.target"></el-input>
          </el-form-item>
          <el-form-item label="关系类型">
            <el-input v-model="relationInfo.label"></el-input>
          </el-form-item>
        </el-form>
        <div style="text-align: center;">
          <el-button @click="relationSetPanel=false">取 消</el-button>
          <el-button type="primary" @click="saveRelationInfo()">确 定</el-button>
        </div>
      </div>
    </el-drawer>

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
      // visGraph创建节点和连线数据集
      graphData: {
        nodes: [],
        links: []
      },
      imageArray:[ //图片引用缓存定义
        {id:'111',label:'图标一',icon : require('@/assets/test/images/icon.png')},
        {id:'222',label:'图标二',icon : require('@/assets/test/images/icon1.png')}
      ],
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
      // 选中的节点对象
      currentNode: {},
      attrbutes:[],//选中节点或连线的属性列表
      // 选中的连线对象
      currentLink: {},
      // 控制编辑节点对话框的显示
      editNodeDialog: false,
      // 节点在编辑对话框内的信息
      editNode: {
        id: '',
        label: '',
        image:'',
        color: ''
      },
      //关系连线设置的右侧面板控制开关
      relationSetPanel:false,
      //设置的关系信息
      relationInfo:{
        id:'', //关系id,需要服务端保存后生成返回
        sourceId:'',//起始节点的ID
        source:'', //起始节点的名称
        targetId:'', //目标节点的ID
        target:'', //目标节点的名称
        label:'' //关系类型名称
      },
      //节点详细信息弹框
      showNodeInfoDialog:false,
      //关系连线信息弹层
      showLinkInfoDialog:false,
      //属性节点详细弹层控制开关
      showProperNodeInfo:false,
      properNode:{},//属性节点
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
      //this.visGraph.switchAnimate(true);
    },
    // 刷新知识图谱数据
    refreshGraphData () {
      this.visGraph.drawData(this.graphData)
      //this.visGraph.setZoom('auto')
      this.generateLegend();//生成图例

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
        that.visGraph.deleteNodes(properNodes);//删除属性节点
      }
    },
    // 执行布局算法
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
    // 删除知识操作
    async deleteNodeClick () {
      /* const {data: res} = await deleteNode(this.currentNode.id)*/
      this.deleteNodeAndProperNodes(this.currentNode)
      this.$message.success('节点已删除')

      this.cancelNodeRightMenu()
      this.popoverNodeClose()
    },
    deleteNodeAndProperNodes(node){ //删除节点及其属性节点
      var properNodes = (node.outLinks||[]).map(function(link){
        return link.target.type == 'proper-virtual'?link.target:null;
      }).filter((node)=>{
        return node != null;
      });

      var that = this;
      if(properNodes.length>0){
        that.visGraph.deleteNodes(properNodes);//删除属性节点
      }
      this.visGraph.deleteNode(node);
    },
    // 关闭删除知识的对话框
    popoverNodeClose () {
      this.$refs.popoverNode.doClose()
    },
    // 删除知识联系操作
    async deleteLinkClick () {
      /* const {data: res} = await deleteLink(this.currentLink.properties.id)*/
      this.visGraph.deleteLink(this.currentLink)

      this.cancelLinkRightMenu()
      this.popoverLinkClose()
    },
    // 关闭删除知识联系的对话框
    popoverLinkClose () {
      this.$refs.popoverLink.doClose()
    },
    // 确定保存节点操作事件
    saveNodeInfo () {
      this.currentNode.label = this.editNode.label||'未设置';
      this.currentNode.fillColor = this.getColorRgb(this.editNode.color);
      this.currentNode.setImage(this.editNode.image); //设置节点图片
      this.editNodeDialog = false

      //TODO 需要将节点的信息保存到服务端去
    },
    // 打开知识编辑对话框
    showEditnodeRightMenuLayer () {
      this.editNode.id = this.currentNode.id
      this.editNode.label = this.currentNode.label
      this.editNode.color = 'rgb('+this.currentNode.fillColor+')';

      this.editNodeDialog = true
      this.cancelNodeRightMenu()
    },
    //打开关系设置的面板
    resetLinkInfo(){
      var link = this.currentLink;
      this.relationInfo = {
        sourceId:link.source.id,
        source:link.source.label,
        targetId:link.target.id,
        target:link.target.label,
        label:link.label
      };
      this.relationSetPanel = true;
      this.cancelLinkRightMenu()
    },
    getColorRgb(color) {
      if (color && color.length > 0) {
        color = color.replace('rgb(', '').replace(')', '');
      } else {
        color = null;
      }
      return color;
    },
    // 开始实体连线操作
    connectLink () {
      var that = this;
      this.visGraph.beginAddLine(function(link){
        that.currentLink = link; //设置为当前连线
        that.resetLinkInfo();
      });
      this.cancelNodeRightMenu()
    },
    saveRelationInfo(){
      //TODO 需要保存到服务端去，生成id，然后设置给连线
      //console.log(this.relationInfo);
      //示例生成服务端id
      this.currentLink.id = 'line-'+this.randomId();//用服务端返回的id替换掉此处
      this.currentLink.type = this.relationInfo.label;
      this.currentLink.label = this.relationInfo.label;

      this.relationSetPanel = false;
      this.visGraph.refresh();
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

        this.renderNodesByLegend();
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
      tipDom.style.top = event.clientY + 5 + 'px';
      tipDom.style.left = event.clientX + 10 +'px';
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
        label:'自定义'
      },function(node){
        //TODO 节点添加到画布后，需要设置后保存至服务端
        //console.log('节点已添加至画布',node);
        that.currentNode = node;
        that.showEditnodeRightMenuLayer();
      });
    },
    randomId(){
      return Math.round(Math.random() * 99999999);
    },
    drawDefinedNode(node){ // 绘制自定义节点
      node.drawNode = function(ctx){ //绘制自定义节点
        if(!this.openAnimation){ //通过openAnimation控制是否绘制自定义
          this.drawOriginalNode(ctx);//系统内置绘制方法
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
    }
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
      that.reLayout();
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
.page-header{
  position: relative;
  height: 60px;
}

.page-header .drag-dot-wrap{
  position:absolute;
  top:5px;
  left:10px;
  width: 120px;
}

.drag-dot-wrap .drag-dot{
  display:inline-block;
  width: 50px;
  height: 50px;
  border-radius:25px;
  background-color: dodgerblue;
  cursor: move;
  touch-action: none;
}

.drag-dot-wrap .drag-dot:hover{
  background-color: deepskyblue;
}

.drag-dot-wrap .dot-label{
  position: absolute;
  top: 18px;
  left: 55px;
  font-size: 13px;
  color: #222;
}
.page-header .swtich-wrap{
  position:absolute;
  top:20px;
  right:10px;
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
