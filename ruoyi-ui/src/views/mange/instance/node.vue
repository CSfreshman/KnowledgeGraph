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
                    v-model="queryParams.name">
                    <template #suffix>
                      <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQuery"></el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>

              <el-table v-loading="loading" :data="nodeClassList" :show-header="false" @row-click="changeSelectedNodeClass">
                <el-table-column key="label" prop="label"></el-table-column>
              </el-table>
              <pagination
                v-show="total>0"
                :total="total"
                :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize"
                @pagination="getList"
              />

            </el-card>
          </div>
        </el-col>
        <el-col span="18">
          <div id="right-container">
            <div id="right-head">

              <el-button>新建实体</el-button>
              <el-button>图谱展示</el-button>
              <el-button>表格展示</el-button>
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
                        prop="name"
                        label="名称"
                        width="120"
                        align="center">
                      </el-table-column>

                      <el-table-column
                        prop="props"
                        label="属性"
                        align="center">
                      </el-table-column>

                      <el-table-column
                        fixed="right"
                        label="操作"
                        width="100"
                        align="center">
                        <template slot-scope="scope">
                          <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
                          <el-button type="text" size="small">编辑</el-button>
                        </template>
                      </el-table-column>

                    </el-table>

                    <pagination
                      v-show="total>0"
                      :total="total"
                      :page.sync="queryParams.pageNum"
                      :limit.sync="queryParams.pageSize"
                      @pagination="getList"
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
  </div>
</template>

<script>
export default {
  name: "node",
  data() {
    return {
      queryParams: '',
      nodeClassList: [
        {id:1,label:"病因"}
      ],
      nodeList: [
        {id:1,name:"心理因素",props:{test:"test",test1:"test1"}},
        {id:2,name:"社会因素",props:{test:"test",test1:"test1"}},
        {id:3,name:"其他因素",props:{test:"test",test1:"test1"}}
      ],
      selectedNode: '',
      selectedNodeClass: '',
      propList: '',
      total: 3

    }
  },

  mounted() {

  },
  methods: {
    changeSelectedNode(row) {
      this.selectedNode = row;
      this.selectedNode.label = this.selectedNodeClass.label
      this.transformData(this.selectedNode,false)
    },
    changeSelectedNodeClass(row) {
      this.selectedNodeClass = row;
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
    background-color: #00afff;
  }
</style>
