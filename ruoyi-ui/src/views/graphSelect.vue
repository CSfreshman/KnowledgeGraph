<template>
  <div id="main-container">
    <div id="page-header">
      <el-card>
        <div>
          <el-button @click="selectIndex = 1">实体查询</el-button>
          <el-button @click="selectIndex = 2">关系查询</el-button>
          <el-button @click="selectIndex = 3">类型联合查询</el-button>
          <el-button @click="doQuery()" icon="el-icon-search">查询</el-button>
          <el-button @click="" icon="el-icon-delete">清空表单</el-button>
        </div>

        <el-form :model="formData" label-width="100px" :inline="true">
          <el-row v-if="selectIndex == 1">
            <el-col>
              <el-form-item label="实体名称">
                <el-input size="mini" style="width: 100px;" v-model="formData.nodeName"></el-input>
              </el-form-item>
            </el-col>

<!--            <el-col span="8">-->

<!--              <el-button @click="doQuery()" icon="el-icon-search">查询</el-button>-->

<!--              <el-button @click="openDialog = true" icon="el-icon-edit">添加更多</el-button>-->

<!--              <el-button @click="showAllCondition = !showAllCondition">{{showAllCondition?"折叠":"展开"}}</el-button>-->
<!--            </el-col>-->
          </el-row>

          <el-row v-if="selectIndex == 2">

            <!--            <el-form-item label="请输入关系名称">-->
            <!--              <el-input size="mini" style="width: 160px;" v-model="formData.edgeName"></el-input>-->
            <!--            </el-form-item>-->
            <el-form-item label="关系起点名称">
              <el-input size="mini" style="width: 100px;" v-model="formData.fromNodeName"></el-input>
            </el-form-item>

            <el-form-item label="关系终点名称">
              <el-input size="mini" style="width: 100px;" v-model="formData.toNodeName"></el-input>
            </el-form-item>
          </el-row>
          <div v-if="selectIndex == 3">
            <el-row>
              <el-col span="8">
                <el-form-item label="选择实体类型">
                  <el-select v-model="selectedNodeClassId" placeholder="选择实体类型" @change="selectNodeClass" size="mini">
                    <el-option
                      v-for="item in nodeClassList"
                      :key="item.id"
                      :label="item.label == null ? item.name : item.label"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col span="8">
                <el-form-item label="选择关系类型">
                  <el-select v-model="selectedEdgeClassId" placeholder="选择实体类型" @change="selectEdgeClass" size="mini">
                    <el-option
                      v-for="item in edgeClassList"
                      :key="item.id"
                      :label="item.label == null ? item.name : item.label"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-tag
                v-for="item in selectedNodeClassList"
                :key="item.id"
                closable
                @close="handleTagCloseForNodeClass(item)"
                style="margin-left: 10px;"
              >
                {{item.name}}
              </el-tag>
            </el-row>
            <el-row style="margin-top: 10px">
              <el-tag
                v-for="item in selectedEdgeClassList"
                :key="item.id"
                closable
                @close="handleTagCloseForEdgeClass(item)"
                style="margin-left: 10px;"
                type="success"
              >
                {{item.label}}
              </el-tag>
            </el-row>
          </div>

        </el-form>
      </el-card>

    </div>

    <div id="page-body">
      <el-button style="margin-right: 50px">保留当前图谱</el-button>
    </div>

<!--    下面的对话框不需要使用了-->
    <div>
      <el-dialog
        title="添加更多查询条件"
        :visible.sync="openDialog"
        :before-close="handleCloseDialog"
      >
        <el-form>
          <el-form-item label="添加实体">
            <el-input
              placeholder="请输出实体名称"
            >
              <template #suffix>
                <el-button style="border: none" icon="el-icon-plus" size="small" @click="" ></el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="添加关系（输入起点与终点实体，以空格分割，如:疾病 症状）">
            <el-input
              placeholder="请输出关系"
            >
              <template #suffix>
                <el-button style="border: none" icon="el-icon-plus" size="small" @click="" ></el-button>
              </template>
            </el-input>
          </el-form-item>

        </el-form>

        <div>
          <el-row>
            <el-tag></el-tag>
          </el-row>
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button @click="openDialog = false">取 消</el-button>
          <el-button type="primary" @click="openDialog = false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>


<script>
import {getAllNodeClass} from "@/api/mange/class/node";
import {getAll as getAllEdgeClass} from "@/api/mange/class/edge";
import {graphSelect} from "@/api/graph"

export default {
  name: "graphSelect",
  data() {
    return {
      // 选中的查询方式
      selectIndex: 1,
      showAllCondition: false,
      openDialog: false,
      nodeClassList: '',
      edgeClassList: '',
      formData: {
        nodeName: '',
        edgeName: '',
        fromNodeName: '',
        toNodeName: '',
        nodeList: [],
        edgeList: [],
      },
      // 单选框中选中的节点类型id
      selectedNodeClassId: '',
      // 已选择的节点的类型
      selectedNodeClassList: [],
      // 单选框中选中的边类型id
      selectedEdgeClassId: '',
      // 已选择的边的类型
      selectedEdgeClassList: [],
    }
  },
  methods: {
    selectNodeClass() {
      // 选择的节点
      var nodeClass = this.nodeClassList.find(it=>it.id == this.selectedNodeClassId);
      console.log(nodeClass);
      // 去重
      if(!this.selectedNodeClassList.find(it=>it.id == nodeClass.id)){
        this.selectedNodeClassList.push(nodeClass)
      }

      // 清空选择状态
      this.selectedNodeClassId = ''
    },

    selectEdgeClass() {
      // 选择的关系
      var edgeClass = this.edgeClassList.find(it=>it.id == this.selectedEdgeClassId);
      console.log(edgeClass);
      // 去重
      if(!this.selectedEdgeClassList.find(it=>it.id == edgeClass.id)){
        this.selectedEdgeClassList.push(edgeClass)
      }

      // 清空选择状态
      this.selectedEdgeClassId = ''
    },

    // 取消选择节点类型
    handleTagCloseForNodeClass(item) {
      console.log("取消的节点类型")
      console.log(item)
      const index = this.selectedNodeClassList.findIndex(element => element.id === item.id);
      if (index !== -1) {
        this.selectedNodeClassList.splice(index, 1);
      }
    },
    // 取消选择关系类型
    handleTagCloseForEdgeClass(item) {
      console.log("取消的关系类型")
      console.log(item)
      const index = this.selectedEdgeClassList.findIndex(element => element.id === item.id);
      if (index !== -1) {
        this.selectedEdgeClassList.splice(index, 1);
      }
    },

    // 点击查询
    doQuery() {
      console.log("查询参数");
      this.formData.nodeClassList = this.selectedNodeClassList;
      this.formData.edgeClassList = this.selectedEdgeClassList;
      // 查询方式，1为实体查询，2为关系查询，3为类型联合查询
      this.formData.selectIndex = this.selectIndex;
      console.log(this.formData)

      graphSelect(this.formData).then(resp=>{

      })
    },

    handleCloseDialog(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    }
  },
  mounted() {
    getAllNodeClass({valid:1}).then(resp=>{
      this.nodeClassList = resp;
      console.log("可选择的节点类型⬇");
      console.log(this.nodeClassList)
    });

    getAllEdgeClass({valid:1}).then(resp=>{
      this.edgeClassList = resp;
      console.log("可选择的关系类型⬇");
      console.log(this.edgeClassList)
    });
  }
}
</script>

<style scoped>
  #main-container {
    background-color: #FFFFFF;
    width:100vw;
    height:100vh;
  }

  #page-header {
    background-color: #ffffff;
    width: 100vw;
  }

  #page-body {
    background-color: #ffffff;
  }
</style>
