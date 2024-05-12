<template>
  <div>
    <div>
      <el-input
        placeholder="请输出实体类型名称"
        v-model="zzName"
        style="margin-left: 20%; width: 60%">
        <template #suffix>
          <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQueryWithName"></el-button>
        </template>
      </el-input>
      <el-card style="margin-left: 20%; width: 60%">
        <el-table
          :data="dictData"
          border
          style="width: 100%">
          <el-table-column
            fixed
            prop="key"
            label="症状描述"
            >
          </el-table-column>
          <el-table-column
            prop="value"
            label="症状"
            >
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            >
            <template slot-scope="scope">
<!--              <el-button type="primary" size="small" style="margin-left: 50px" @click="handleUpdate(scope.row)">编辑</el-button>-->
              <el-button type="danger" size="small" style="margin-left: 50px" @click="handleDel(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-row>
          <el-col :span="6">
            <el-button size="medium" style="margin-top: 10px" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="18">
            <pagination
              v-show="total>0"
              :total="total"
              :page.sync="pageNum"
              :limit.sync="pageSize"
              @pagination="getZzDict"
            />
          </el-col>
        </el-row>

      </el-card>

      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="症状描述" prop="key">
            <el-input v-model="form.key" placeholder="请输入症状描述" />
          </el-form-item>
          <el-form-item label="对应症状" prop="value">
            <el-select v-model="form.value" placeholder="选择关系类型">
              <el-option
                v-for="item in nodeList"
                :key="item.id"
                :label="item.name"
                :value="item.name">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div>
          <el-button type="primary" @click="submit">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {addZzDict, delZzDict, getZzDict, updateZzDict} from "@/api/redis";
import {delNodeProperties} from "@/api/mange/class/nodeProperties";
import {getAll} from "@/api/mange/instance/node";

export default {
  name: "redis",
  data() {
    return {
      zzName: '',
      isEdit: false,
      dictData: '',
      pageSize: 10,
      pageNum: 1,
      total: 0,
      title: '',
      open: false,
      form: {
        key: '',
        value: ''
      },
      nodeList: ''
    }
  },
  methods: {
    getZzDict(){
      getZzDict({pageSize:this.pageSize,pageNum:this.pageNum}).then(resp=>{
        this.dictData = resp.data.pageData;
        this.total = resp.data.total;
      })
    },
    handleQueryWithName(){
      getZzDict({pageSize:this.pageSize,pageNum:this.pageNum,zzName:this.zzName}).then(resp=>{
        this.dictData = resp.data.pageData;
        this.total = resp.data.total;
      })
    },
    resetForm(){
      this.form.key = '';
      this.form.value = '';
    },

    handleAdd(){
      this.title = "新增键值对";
      this.open = true;
      this.resetForm();
      this.isEdit = false;
    },

    handleUpdate(row){
      this.title = "修改键值对";
      this.open = true;
      this.resetForm();
      console.log(row)
      this.form.key = row.key;
      this.form.value = row.value;
      this.isEdit = true;
    },
    handleDel(row){
      this.$modal.confirm('是否确认删除key为' + row.key + '的数据？').then(function() {
        return delZzDict(row);
      }).then(() => {
        this.getZzDict();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    delZzDict(row){
      delZzDict({key:row.key}).then(resp=>{
        this.getZzDict();
      })
    },

    submit(){
      if(this.isEdit){
        updateZzDict(this.form).then(resp=>{
          this.getZzDict();
          this.cancel();
        })
      }else{
        addZzDict(this.form).then(resp=>{
          this.getZzDict();
          this.cancel();
        })
      }
    },

    cancel(){
      this.open = false;
      this.resetForm();
    },

    getNodeInstanceList() {
      getAll({valid:1,label:'症状'}).then(resp=>{
        this.nodeList = resp;
        this.nodeList.sort((a, b) => {
          return a.localeCompare(b, 'zh-Hans-CN'); // 按照中文简体的语言规则比较
        });

        console.log(this.nodeList)
      })
    },
  },
  created() {
    this.getZzDict();
    this.getNodeInstanceList();
  }
}
</script>

<style scoped>

</style>
