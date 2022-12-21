<template>
  <r-form    v-model="obj" ref="edit">
    <r-hidden   v-model="obj.id" />

    <template v-slot:btn>
      <el-button type="primary" v-permission="'sys:config:save'" @click="submit" >保存</el-button>
      <el-button  type="info" @click="cancel">取消</el-button>
    </template>
  </r-form>
</template>

<script>
import {ref } from "vue";
import { useStore} from "vuex";
import { useRouter } from 'vue-router'
export default {
  name: "编辑【${remark}】",
  setup(props, context){
    const edit=ref(null);
    const store=useStore();
    const router=useRouter();
    const obj = ref(Object.assign({},router.currentRoute.value.meta.data));
    const submit=()=> {
      edit.value.submit()
    }
    const cancel=()=>{
      edit.value.cancel();
    }

    return {edit,obj,submit,cancel,list}
  },
}
</script>

<style scoped>

</style>