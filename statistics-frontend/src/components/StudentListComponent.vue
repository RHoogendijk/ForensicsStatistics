<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useClasses } from '@/composables/useClasses';
import {onMounted} from "vue";

const { fetchStudentsForClass, classesErr, classesLoading, students } = useClasses();

const route = useRoute()
const router = useRouter()
const classId = route.params.id
const goBack = () => {
  router.back()
}
onMounted(async () => {
  await fetchStudentsForClass(classId);
});
const goToStudent= (id) => {
  router.push({ name: "Student Sessions", params: { id: id } });
};
</script>

<template>
  <h2>Student List ({{classId}})</h2>
  <a href="#" @click="goBack">Go back</a>
  <div v-if="classesLoading">Loading...</div>
  <div v-else-if="classesErr">Error: {{ usersErr.message }}</div>
  <table v-else>
    <thead>
    <tr>
      <th>Name</th>
      <th class="small">Sessions</th>
    </tr>
    </thead>
    <tbody>

    <tr
      v-for="student in students"
      @click="goToStudent(student.id)"
    >
      <td>{{ student.name }} </td>
      <td class="end">32</td>
    </tr>
    <tr v-if="students.length === 0">
      <td>This group is empty...</td>
    </tr>
    </tbody>
  </table>
</template>

<style scoped>
a{
  width: 100%;
  color: blue;
  font-size: 14px;
  padding: 0 8px;
}

tr{
  transition: background-color 0.4s;
}
tr:hover{
  background-color: #F6F7F8;
  cursor: pointer;
}
</style>
