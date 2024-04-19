<template>
  <div>
    <el-card id="main-container">
      <el-row>
        <el-col span="6">
          <div id="left-container">
            <el-card>
              <el-form>
                <el-form-item :inline="true">
                  <el-input
                    placeholder="请输出实体类型名称"
                    v-model="queryParamsClass.name">
                    <template #suffix>
                      <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQuery"></el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>

              <el-table v-loading="loading" :data="nodeClassList" :show-header="false" @row-click="changeSelectedNodeClass">
                <el-table-column key="name" prop="name"></el-table-column>
              </el-table>
              <pagination
                v-show="totalClass>0"
                :total="totalClass"
                :page.sync="queryParamsClass.pageNum"
                :limit.sync="queryParamsClass.pageSize"
                @pagination="getNodeClassList"
              />

            </el-card>
          </div>
        </el-col>
        <el-col span="18">
          <div id="right-container">
            <div id="right-head">

              <el-button @click="addNode">新建实体</el-button>
<!--              <el-button>图谱展示</el-button>-->
<!--              <el-button>表格展示</el-button>-->
            </div>
            <div id="right-body">
              <el-row>
                <el-col span="16">
                  <!--表格形式展示-->
                  <div>
                    <el-table
                      :data="nodeList"
                      border
                      @row-click="changeSelectedNode"
                    >
                      <el-table-column
                        prop="label"
                        label="label"

                        align="center">
                      </el-table-column>

                      <el-table-column
                        prop="name"
                        label="名称"

                        align="center">
                      </el-table-column>

                      <el-table-column
                        fixed="right"
                        label="操作"

                        align="center">
                        <template slot-scope="scope">
                          <el-button @click="gotoDetail(scope.row)" type="text" size="small">查看</el-button>
                          <el-button type="text" size="small">编辑</el-button>
                        </template>
                      </el-table-column>

                    </el-table>

                    <pagination
                      v-show="total>0"
                      :total="total"
                      :page.sync="queryParams.pageNum"
                      :limit.sync="queryParams.pageSize"
                      @pagination="getNodeInstanceList"
                    />

                  </div>
                </el-col>
                <el-col span="8">
                  <el-row>
                    <el-button>查看详情</el-button>
                  </el-row>
                  <el-row>
                    <div>
                      <el-row>
                        <template>
                          <el-tag
                            v-for="(labelItem, index) in selectedNode.labels"
                            :key="index"
                            :color="selectedNode.color"
                            size="medium"
                            class="rounded-tag"
                            type="info"

                          >
                            {{labelItem}}
                          </el-tag>

                          <el-tag
                            :color="selectedNode.color"
                            size="medium"
                            class="rounded-tag"
                            type="info"
                          >{{selectedNode.label}}</el-tag>
                        </template>
                      </el-row>
                      <template>
                        <div>
                          <el-table :data="propList" :show-header="false" style="width: 100%"  :border=true stripe>
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
                </el-col>
              </el-row>


            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form :model="form">
        <el-form-item label="name" label-width="100">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <div v-for="(prop, index) in form.props" :key="index">
          <el-row><el-col>
            <el-form-item :label="prop.key" label-width="100">
              <el-input v-model="prop.value"></el-input>
            </el-form-item>
          </el-col></el-row>

        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import { listClass, getClass, delClass, addClass, updateClass } from "@/api/mange/class/node";
import {delNodeProperties, listNodeProperties, updateNodeProperties, addNodeProperties} from "@/api/mange/class/nodeProperties"
import {addNode,listNode} from "@/api/mange/instance/node"
export default {
  name: "node",
  data() {
    return {
      // 实体类型查询参数
      queryParamsClass: {
        valid: '',
        pageNum: 1,
        pageSize: 5,
        name: ''
      },
      // 实体实例查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        valid: 1,
        classId: ''
      },
      nodeClassList: '',
      nodeClassPropsList: '',
      totalClass: 0,
      total: 0,
      loading: true,

      nodeList: [
        // {id:1,name:"心理因素",props:{test:"test",test1:"test1"}},
        // {id:2,name:"社会因素",props:{test:"test",test1:"test1"}},
        // {id:3,name:"其他因素",props:{test:"test",test1:"test1"}}
      ],
      selectedNode: '',
      selectedNodeClass: '',
      propList: '',


      // 是否打开对话框
      open: false,
      title: '',

      form: {
        classId: '',
        label: '',
        name: '',
        props: []
      }
    }
  },

  mounted() {
    this.getNodeClassList();
  },
  methods: {
    // 查询实体类型列表
    getNodeClassList() {
      this.loading = true;
      this.queryParamsClass.valid = 1;
      listClass(this.queryParamsClass).then(response => {
        this.nodeClassList = response.rows;
        this.totalClass = response.total;
        this.loading = false;

      });
    },

    getNodeInstanceList() {
      this.queryParams.valid = 1;
      this.queryParams.classId = this.selectedNodeClass.id;
      listNode(this.queryParams).then(resp=>{
        this.nodeList = resp.rows
        this.total = resp.total
      })
    },

    changeSelectedNode(row) {
      this.selectedNode = row;
      this.selectedNode.label = row.label
      this.transformData(this.selectedNode,false)
    },


    changeSelectedNodeClass(row) {
      this.selectedNodeClass = row;
      console.log(row)
      this.getNodeInstanceList()
    },

    resetForm() {
      this.form.classId = '';
      this.form.label = '';
      this.form.name = '';
      this.form.props = [];
    },

    // 新建一个实体
    addNode() {
      this.open = true;
      this.title = "新建实体-------" + this.selectedNodeClass.name;
      this.resetForm();
      // 查询该实体类型所有的属性
      var nodeClassPropQueryParams = {
        nodeId: this.selectedNodeClass.id,
        valid: 1
      };
      listNodeProperties(nodeClassPropQueryParams).then(resp=>{
        this.nodeClassPropsList = resp.rows;
        console.log(this.nodeClassPropsList)

        // 填充form表单
        this.form.label = this.selectedNodeClass.name;
        this.form.classId = this.selectedNodeClass.id;

        //this.form.props.push({key:"name",value: ''})

        this.nodeClassPropsList.forEach(it=>{
          this.form.props.push({key:it.name,value: ''})
        })

        console.log(this.form)
      })
    },

    submit() {
      this.open = false;
      addNode(this.form).then(resp=>{
        this.getNodeInstanceList()
      })
    },

    cancel() {
      this.open = false;
    },

    // 点击表格的查看按钮
    gotoDetail(row) {
      // 页面路由传递的id只需要有neo4jId即可
      var data = {id:row.neo4jId};

      this.$router.push({
        path: '/nodeDetail',
        query: {data}
      })
    },

    // 对一条数据进行格式转换，方便在
    transformData(data,isEdge) {
      let rawData = data.propsList;
      const transformedData = [];
      transformedData.push({key: 'id', value: data.id})
      transformedData.push({key: 'neo4j_id', value: data.neo4jId})
      transformedData.push({ key: 'label', value: data.label});
      if(isEdge){
        transformedData.push({key: 'fromId', value: data.from})
        transformedData.push({key: 'fromName', value: this.nodes.find(node=>node.id == data.from).label})
        transformedData.push({key: 'toId', value: data.to})
        transformedData.push({key: 'toName', value: this.nodes.find(node=>node.id == data.to).label})
      }
      for (const key in rawData) {
        transformedData.push({ key:rawData[key].name, value: rawData[key].value });
      }
      this.propList = transformedData;
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

  #left-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #right-container {
    background-color: #FFFFFF;
    height:100vh;
  }

  #right-head {
    height:8vh;
    background-color: #FFFFFF;
  }
</style>
