<template>
  <div>
    <el-card id="network-container">
      <el-row>
        <el-col span="9">
          <div id="left-aside">
            {{chooseNode}}
            <br>
            {{chooseEdge}}
          </div>
        </el-col>
        <el-col span="15">
          <div id="network"></div>
        </el-col>
      </el-row>

    </el-card>

  </div>
</template>

<script>
import vis from 'vis'

export default {
  name: "graph",
  data() {
    return {

      chooseNode: 'chooseNode',
      chooseEdge: 'chooseEdge',
      network: '',

      nodes: [
        { id: 1, label: 'Node 1' ,prop: {test:123}},
        { id: 2, label: 'Node 2' ,prop: {test:123}},
        { id: 3, label: 'Node 3' ,prop: {test:123}},
        { id: 4, label: 'Node 4' ,prop: {test:123}},
        { id: 5, label: 'Node 5' ,prop: {test:123}}
      ],
      edges: [
        { from: 1, to: 3 ,id: 1, label: "test13"},
        { from: 1, to: 2 ,id: 2, label: "test12"},
        { from: 2, to: 4 ,id: 3, label: "test24"},
        { from: 2, to: 5 ,id: 4, label: "test25"}
      ]
    }
  },
  mounted () {
    this.globalTrace() // 页面初始化的时候调用方法
  },
  methods: {
    /**
     * vis实现
     */
    globalTrace () {
      // 建立拓扑图
      var container = document.querySelector('#network')
      // 以vis格式提供数据
      var data = {
        nodes: new vis.DataSet(this.nodes),
        edges: new vis.DataSet(this.edges),
      }
      var options = { // 对vis的一些设置

      }
      // 初始化你的网络
      this.network = new vis.Network(container, data, options)
      // this.network.setOption(options)

      var view = this;

      this.network.on('click', function (properties) {
        // 打印的是点击的节点的对应id
        let nodeId = properties.nodes[0];
        let edgeId = properties.edges[0];
        console.log("nodeId:"+nodeId);
        console.log("edgeId:"+edgeId);

        console.log(view.nodes[nodeId-1])
        view.chooseNode = view.nodes[nodeId-1]


        console.log(view.edges[edgeId-1])
        view.chooseEdge = view.edges[edgeId-1]

      })


    }
  }

}
</script>

<style scoped>
 #network-container {
   background-color: #FFFFFF;
   width:100vw;
   height:100vh;
 }

 #network {
   background-color: #00afff;
   height:100vh;
 }

 #left-aside {
   background-color: #42b983;
   height:100vh;
 }

 * {
   margin: 0;
   padding: 0;
 }
</style>
