<template>
  <div>
    <div id="mynetwork"></div>
    <div v-if="showContextMenu" :style="{ left: contextMenuPosition.x + 'px', top: contextMenuPosition.y + 'px' }" class="context-menu">
      <el-row><el-button @click="addNode" size="mini">添加节点</el-button></el-row>
      <el-row><el-button @click="deleteNode" size="mini">删除节点</el-button></el-row>
      <el-row><el-button @click="updateNode" size="mini">更新节点</el-button></el-row>
      <el-row><el-button @click="searchNode" size="mini">查找节点</el-button></el-row>
    </div>

<!--    <el-card v-if="showContextMenu" :style="{ left: contextMenuPosition.x + 'px', top: contextMenuPosition.y + 'px' }" class="context-menu">-->
<!--      <el-row><el-button @click="addNode">添加节点</el-button></el-row>-->
<!--      <el-row><el-button @click="deleteNode">删除节点</el-button></el-row>-->
<!--      <el-row><el-button @click="updateNode">更新节点</el-button></el-row>-->
<!--      <el-row><el-button @click="searchNode">查找节点</el-button></el-row>-->
<!--    </el-card>-->

  </div>
</template>

<script>
import vis from 'vis';

export default {
  data() {
    return {
      showContextMenu: false,
      contextMenuPosition: { x: 0, y: 0 },
      network: null
    };
  },
  mounted() {
    const container = document.getElementById('mynetwork');
    const data = {
      nodes: [
        { id: 1, label: 'Node 1' },
        { id: 2, label: 'Node 2' },
        { id: 3, label: 'Node 3' }
      ],
      edges: [
        { from: 1, to: 2 },
        { from: 2, to: 3 }
      ]
    };
    const options = {};
    this.network = new vis.Network(container, data, options);

    this.network.on("click", (params) => {
      if (params.nodes.length > 0) {
        console.log("单击节点");
        this.showContextMenu = false;

      }
    });

    this.network.on("doubleClick",(params) => {
        if (params.nodes.length > 0) {
          console.log("双击节点");
          const nodeId = params.nodes[0];
          const position = this.network.getPositions([nodeId]);
          console.log("点击节点")
          // 状态取反
          this.showContextMenu = true;
          console.log(position[nodeId])

          console.log(params.pointer.DOM)
          console.log(params.pointer.canvas)

          this.contextMenuPosition = {
            x: params.pointer.DOM.x,
            y: params.pointer.DOM.y
          };
        }
    })

    this.network.on("dragStart",(params)=>{
      this.hideContextMenu();
    })

    this.network.on("dragging",(params)=>{
      this.hideContextMenu();
    })

    this.network.on("dragEnd",(params)=>{
      this.hideContextMenu();
    })

    // this.network.on("click", () => {
    //   this.hideContextMenu();
    // });
  },
  methods: {
    addNode() {
      // 添加节点逻辑
      this.hideContextMenu();
    },
    deleteNode() {
      // 删除节点逻辑
      this.hideContextMenu();
    },
    updateNode() {
      // 更新节点逻辑
      this.hideContextMenu();
    },
    searchNode() {
      // 查找节点逻辑
      this.hideContextMenu();
    },
    hideContextMenu() {
      this.showContextMenu = false;
    }
  }
}
</script>

<style scoped>
.context-menu {
  position: absolute;
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 5px;
  z-index: 100;
  cursor: pointer;
}

 #mynetwork{
   height: 500px;
 }
</style>
