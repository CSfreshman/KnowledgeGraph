<template>
  <div>
    <el-card id="main-container">
      <el-row>
        <el-col span="6">
          <div id="prop-container">
            <el-row>
              <el-col span="3">
                属性
              </el-col>
              <el-col span="2">
                <el-button>修改</el-button>
              </el-col>
            </el-row>
            <el-row>
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
                    </el-table>
                  </div>
                </template>
              </div>
            </el-row>
          </div>
        </el-col>
        <el-col span="18">
          <div id="network-container">
            <div id="network-head">
              <el-row>
                <el-col span="9">
                  {{ headText }}
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(1)">1度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(2)">2度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(3)">3度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button>删除</el-button>
                </el-col>
              </el-row>

            </div>
            <div id="network-body">

            </div>
            <div id="network-bottom"></div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import {getNodeDetail} from "@/api/nodeDetail"
import vis from "vis";

export default {
  name: "node",
  data() {
    return{
      headText: '图谱：',
      nodes: '',
      edges: '',
      // 该详情页对应的节点
      mainData: '',
      // 点击的节点
      showData: '',
      // 表格中展示的数据（选中的节点的属性）
      tableData: '',
      nodeId: 9
    }
  },
  mounted(){
    this.getNodeDetail(this.nodeId,1);

  },
  methods: {
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
            //return { ...node, color: 'defaultColor' }; // 如果prop.color不存在，默认设置一个值
            return {...node}
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

        this.createNetwork()

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
    },

    changeDegree(degree) {
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

  #prop-container {
    background-color: #ececec;
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

  #network-body {
    background-color: #FFFFFF;
    height:67vh;
  }

  #network-bottom {
    background-color: green;
    height:25vh;
  }

  .rounded-tag {
    border-radius: 20px; /* 调整圆角大小 */
    color: #000000;
  }
</style>
