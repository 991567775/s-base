<template>
  <r-tree-form ref="treeForm" v-model="obj" :treeClick="treeClick"
               :noCleanField="['pid','really','sort']"
               textAlign="left"   >
    <r-hidden   v-model="obj.id" />
    <r-hidden   v-model="obj.pid" />

    <template v-slot:btn>
      <el-button  type="primary" v-permission="'sys:company:add'" @click="add">新增</el-button>
      <el-button type="warning" v-if="obj.pid!=0" v-permission="'sys:company:save'" @click="submit" >保存</el-button>
      <el-button  type="info" v-if="obj.pid!=0"  @click="cleanData">清空</el-button>
      <el-button  type="danger" v-if="obj.pid!=0" v-permission="'sys:company:del'" @click="remove">删除</el-button>
    </template>
  </r-tree-form>
</template>

<script>
import {ref,onMounted} from "vue";
import { useStore} from "vuex";

export default {
  name: "${remark}",
  setup(){
    //treeForm 实例
    const treeForm=ref(null)
    const store=useStore();
    //字典
    const list=store.getters.getDirect("yesNo",Boolean);
    //表单对象
    const obj = ref({id:'',pid:'',label:"",content:"",really:true,sort:0})
    //查询树结构数据
    onMounted(()=>{
      treeForm.value.selectTree();
    } )
    //树节点点击事件
    const treeClick=(node)=>{
    }
    //表单保存事件
    const submit=()=> {
      treeForm.value.submit((data)=>{
        // console.log(data)
      });
    }
    //清空
    const cleanData=()=>{
      treeForm.value.cleanData();
    }
    //新增
    const add=()=>{
      treeForm.value.add();
    }
    //删除
    const  remove=()=>{
      treeForm.value.remove();
    }
    return {treeForm,obj,treeClick,list,add,submit,cleanData,remove}
  }
}
</script>

<style scoped>

</style>