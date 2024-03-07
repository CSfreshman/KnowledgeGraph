<template>
  <div>
    <el-card id="network-container">
      <el-row>
        <el-col span="7">
          <div id="left-aside">
<!--            没有选中节点或关系，进行预览-->
            <div v-show="!selected">
              <el-row>总览</el-row>
              <el-row>共包含{{totalNodeSize}}个节点，{{totalEdgeSize}}个关系</el-row>
            </div>

<!--            选中了节点或关系，展示节点或关系属性-->
            <div v-show="selected">
              <el-row>属性</el-row>
<!--              点击节点-->
              <div>
                <el-row>
                  <template>
                    <el-tag
                      v-for="(labelItem, index) in showData.labels"
                      :key="index"
                      :color="showData.color"
                      size="medium"
                      class="rounded-tag"
                      type="info"
                    >
                      {{labelItem}}
                    </el-tag>

                    <el-tag
                      :color="showData.color"
                      size="medium"
                      class="rounded-tag"
                      type="info"
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

                <template>
                  <div>
                    <el-table :data="tableData" :show-header="false" style="width: 100%" stripe>
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
                    </el-table>
                  </div>
                </template>
              </div>


<!--              点击边-->
              <div>

              </div>
            </div>

          </div>
        </el-col>
        <el-col span="17">
          <div id="network"></div>
        </el-col>
      </el-row>

    </el-card>

  </div>
</template>

<script>
import vis from 'vis'
import {getAllGraph} from "@/api/graph"

export default {
  name: "graph",
  data() {
    return {

      chooseNode: '',
      chooseEdge: '',
      showData: '',
      network: '',
      tableData: '',
      nodes: [
        { id: 1, label: 'Node 1' ,prop: {test:123,test1:123}},
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
      ],

      selected: false,
      totalNodeSize: 0,
      totalEdgeSize: 0
    }
  },
  mounted () {
    this.getAllGraph();
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
      // this.network.setOption(options)

      var view = this;

      this.network.on('click', function (properties) {

        if(properties.nodes.length == 0 && properties.edges.length == 0){
          console.log("点击画布");
          view.selected = false;
        } else if(properties.nodes.length == 0) {
          console.log("点击边");
          view.selected = true;
          let edgeId = properties.edges[0];
          console.log("edgeId:"+edgeId);
          view.chooseEdge = view.edges.find(edge=>edge.id == edgeId)
          view.showData = view.edges.find(edge=>edge.id == edgeId)
          view.transformData(view.chooseEdge,true)

        } else{
          console.log("点击节点");
          view.selected = true;
          let nodeId = properties.nodes[0];
          console.log("nodeId:"+nodeId);
          view.chooseNode = view.nodes.find(node=>node.id == nodeId)
          view.showData = view.nodes.find(node=>node.id == nodeId)
          view.transformData(view.chooseNode,false)
        }
      })


    },


    getAllGraph() {
      getAllGraph().then(response=>{
        const newNodes = response.nodes.map(node=>{
          if (node.props && node.props.color) {
            return { ...node, color: node.props.color };
          } else {
            //return { ...node, color: 'defaultColor' }; // 如果prop.color不存在，默认设置一个值
            return {...node}
          }
        })

        const newEdges = response.edges.map(edge=>{
          if (edge.props && edge.props.color) {
            return { ...edge, color: {color: edge.props.color} };
          } else {
            return {...edge}
          }
        })


        this.nodes = newNodes;
        this.edges = newEdges;

        console.log(newNodes)
        console.log("执行结果===")
        console.log("nodes:" + this.nodes + "\nedges:" + this.edges)
        console.log("======")
        this.globalTrace();
      })
    },

    // 对一条数据进行格式转换，方便在
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
   background-color: #FFFFFF;
   height:100vh;
 }

 #left-aside {
   background-color: #ececec;
   height:100vh;
 }

 * {
   margin: 0;
   padding: 0;
 }

 .rounded-tag {
   border-radius: 20px; /* 调整圆角大小 */
 }
</style>
