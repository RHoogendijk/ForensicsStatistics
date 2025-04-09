<script setup>
import {onMounted, ref} from "vue";
import { useRouter } from 'vue-router';
import {useClasses} from "@/composables/useClasses.js"

const {classes, classesErr, classesLoading, fetchClasses} = useClasses()

onMounted(async () => {
  await fetchClasses()
})

const router = useRouter();

const goToClass = (id) => {
  router.push({ name: "ClassDetail", params: { id: id } });
};
</script>

<template>
  <h2>Groups</h2>
  <div v-if="classesLoading">Loading...</div>
  <div v-else-if="classesErr">Error: {{ usersErr.message }}</div>
  <table v-else>
    <thead>
    <tr>
      <th>Name</th>
      <th class="small">Members</th>
    </tr>
    </thead>
    <tbody>

    <tr
      v-for="schoolClass in classes"
      @click="goToClass(schoolClass.id)"
    >
      <td>{{ schoolClass.id }} </td>
      <td class="end">32</td>
    </tr>
    </tbody>
  </table>
</template>

<style scoped>
table{
  width: 60%;
}
.small{
  width: 20%;
  text-align: center;
}
.end{
  text-align: center;
}
tr{
  transition: background-color 0.4s;
}
tr:hover{
  background-color: #F6F7F8;
  cursor: pointer;
}
</style>
