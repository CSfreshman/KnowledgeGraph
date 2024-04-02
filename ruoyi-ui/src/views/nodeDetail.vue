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
                  <el-button @click="changeDegree(1)">1度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(2)">2度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="changeDegree(3)">3度</el-button>
                </el-col>
                <el-col span="2">
                  <el-button @click="deleteNode">删除</el-button>
                </el-col>
              </el-row>
            </div>
            <div id="network-body"></div>
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
import {getNodeDetail, updateNodeDetail, deleteNode} from "@/api/nodeDetail"
import vis from "vis";

export default {
  name: "node",
  data() {
    return{
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
      open: false,
      form: {
        nodeId: '', // 节点id
        props: []
      }
    }
  },
  mounted(){
    this.routerData = this.$route.query.data;
    this.nodeId = this.routerData.id;
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
      //
      // var self = this;
      //
      //
      // this.network.on("afterDrawing",() => {
      //    self.network.focus(self.nodeId);
      // });
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
      updateNodeDetail(this.form).then(resp=>{
        this.getNodeDetail(this.nodeId,1);
      })
    },

    cancel() {
      this.open = false;
      this.form.props = [];
    },

    // 删除节点
    deleteNode(){
      deleteNode(this.nodeId).then(resp=>{
        this.$router.push({
          path: '/graph'
        })
      })
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

  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
