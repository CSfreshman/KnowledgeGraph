<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
<!--      <el-form-item label="对应的主体的id" prop="targetId">-->
<!--        <el-input-->
<!--          v-model="queryParams.targetId"-->
<!--          placeholder="请输入对应的主体的id"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="记录名称" prop="targetName">
        <el-input
          v-model="queryParams.targetName"
          placeholder="请输入记录名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="修改前的值" prop="originValue">-->
<!--        <el-input-->
<!--          v-model="queryParams.originValue"-->
<!--          placeholder="请输入修改前的值"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="修改后的值" prop="curValue">-->
<!--        <el-input-->
<!--          v-model="queryParams.curValue"-->
<!--          placeholder="请输入修改后的值"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="生成时间" prop="time">
        <el-date-picker clearable
          v-model="queryParams.time"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择记录生成时间">
        </el-date-picker>
      </el-form-item>

      <el-form-item label="记录类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择记录类型">
          <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>


      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="refresh">刷新</el-button>
      </el-form-item>

      <el-row>
        <el-col :span="20">
          <el-form-item label="目标类型" prop="targetType">
            <el-select v-model="queryParams.targetType" placeholder="请选择目标类型">
              <el-option
                v-for="item in targetTypeList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>

      </el-row>

<!--      <el-form-item label="操作用户id" prop="userId">-->
<!--        <el-input-->
<!--          v-model="queryParams.userId"-->
<!--          placeholder="请输入操作用户id"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->

    </el-form>

<!--    <el-row :gutter="10" class="mb8">-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['system:history:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['system:history:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['system:history:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['system:history:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
<!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
<!--    </el-row>-->

    <template>
      <el-table v-loading="loading" :data="historyList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="记录id" align="center" prop="id" />
        <el-table-column label="记录类型" align="center" prop="type">
          <template slot-scope="scope">
            <!-- 根据 type 的值决定显示不同内容 -->
            <span v-if="scope.row.type === 1">新增</span>
            <span v-else-if="scope.row.type === 2">删除</span>
            <span v-else-if="scope.row.type === 3">修改</span>
            <!-- 可以添加更多的条件 -->
          </template>
        </el-table-column>
        <el-table-column label="记录目标id" align="center" prop="targetId" />
        <el-table-column label="记录目标类型" align="center" prop="targetType">
          <template slot-scope="scope">
            <!-- 根据 type 的值决定显示不同内容 -->
            <span v-if="scope.row.targetType === 1">节点类型</span>
            <span v-else-if="scope.row.targetType === 2">节点类型-属性</span>
            <span v-else-if="scope.row.targetType === 3">节点实体</span>
            <span v-else-if="scope.row.targetType === 4">节点实例-属性</span>
            <span v-else-if="scope.row.targetType === 5">关系类型</span>
            <span v-else-if="scope.row.targetType === 6">关系类型-属性</span>
            <span v-else-if="scope.row.targetType === 7">关系实例</span>
            <span v-else-if="scope.row.targetType === 8">关系实例-属性</span>
            <!-- 可以添加更多的条件 -->
          </template>
        </el-table-column>
        <el-table-column label="记录名称" align="center" prop="targetName" />
        <el-table-column label="修改前的值" align="center" prop="originValue" />
        <el-table-column label="修改后的值" align="center" prop="curValue" />
        <el-table-column label="记录生成时间" align="center" prop="time" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.time, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
<!--        <el-table-column label="操作用户id" align="center" prop="userId" />-->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
<!--            <el-button-->
<!--              size="mini"-->
<!--              type="text"-->
<!--              icon="el-icon-edit"-->
<!--              @click="handleUpdate(scope.row)"-->
<!--              v-hasPermi="['system:history:edit']"-->
<!--            >修改</el-button>-->
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="getDetail(scope.row)"
              v-hasPermi="['system:history:edit']"
            >查看</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:history:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    </template>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="对应的主体的id" prop="targetId">
          <el-input v-model="form.targetId" placeholder="请输入对应的主体的id" />
        </el-form-item>
        <el-form-item label="对应主体的名称" prop="targetName">
          <el-input v-model="form.targetName" placeholder="请输入对应主体的名称" />
        </el-form-item>
        <el-form-item label="修改前的值" prop="originValue">
          <el-input v-model="form.originValue" placeholder="请输入修改前的值" />
        </el-form-item>
        <el-form-item label="修改后的值" prop="curValue">
          <el-input v-model="form.curValue" placeholder="请输入修改后的值" />
        </el-form-item>
        <el-form-item label="记录生成时间" prop="time">
          <el-date-picker clearable
            v-model="form.time"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择记录生成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="生成该记录的用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入生成该记录的用户id" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHistory, getHistory, delHistory, addHistory, updateHistory } from "@/api/history";

export default {
  name: "History",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 【请填写功能名称】表格数据
      historyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null,
        targetId: null,
        targetType: null,
        targetName: null,
        originValue: null,
        curValue: null,
        time: null,
        userId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        type: [
          { required: true, message: "记录类型，1新增，2删除，3修改不能为空", trigger: "change" }
        ],
        targetId: [
          { required: true, message: "对应的主体的id不能为空", trigger: "blur" }
        ],
        targetType: [
          { required: true, message: "", trigger: "change" }
        ],
        time: [
          { required: true, message: "记录生成时间不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "生成该记录的用户id不能为空", trigger: "blur" }
        ]
      },
      typeList: [
        {value:1, label:"新增"},
        {value:2, label:"删除"},
        {value:3, label:"修改"},
      ],
      targetTypeList: [
        {value:1, label:"节点本体"},
        {value:2, label:"节点本体-属性"},
        {value:3, label:"节点实例"},
        {value:4, label:"节点实例-属性"},
        {value:5, label:"关系本体"},
        {value:6, label:"关系本体-属性"},
        {value:7, label:"关系实例"},
        {value:8, label:"关系实例-属性"},
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    refresh(){
      this.getList()
    },
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listHistory(this.queryParams).then(response => {
        this.historyList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        type: null,
        targetId: null,
        targetType: null,
        targetName: null,
        originValue: null,
        curValue: null,
        time: null,
        userId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加【请填写功能名称】";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getHistory(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改【请填写功能名称】";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateHistory(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHistory(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    // 查看详情
    getDetail(row) {

    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + ids + '"的数据项？').then(function() {
        return delHistory(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/history/export', {
        ...this.queryParams
      }, `history_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
