<script setup>
import {computed, inject, onMounted, ref} from "vue";
import {useClasses} from "@/composables/useClasses.js"

const sessionService = inject("sessionService");
const username = ref(sessionService.getUsername());
const isStudent = computed(() => sessionService.isStudent());
const userId = ref(sessionService.getId())
const selectedClass = ref('none')
const {classes, classesErr, classesLoading, fetchClasses, enrollStudent} = useClasses()

onMounted(async () => {
  await fetchClasses()
})


</script>

<template>
  <div class="body-container">
    <h2>Hello, {{username}}</h2>
    <div class="inputs" v-if="isStudent">
      <h4>Change class:</h4>
      <div v-if="classesLoading">Loading...</div>
      <div v-else-if="classesErr">Error: {{ classesErr.message }}</div>
      <select
        v-else
        id="classes"
        v-model="selectedClass"
      >
        <option
          v-for="(schoolClass, index) in classes"
          :value="index"
          :key="schoolClass.id"
        >
          {{schoolClass.id}}</option>
        <option value="none">none</option>
      </select>
      <button @click="enrollStudent(classes[selectedClass].id, userId)">Save</button>
    </div>


  </div>
</template>

<style scoped>
.inputs{
  margin-top:0;
}
select{
  width: 100%;
  margin: 4px 16px 4px 8px;
  padding: 8px;
  border-radius: 8px;
  border: 2px solid #E8E9EC;
}
h4 {
  text-align: center;
  padding-top: 2px;
}
</style>
