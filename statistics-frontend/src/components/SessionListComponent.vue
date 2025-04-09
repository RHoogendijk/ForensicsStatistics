<script setup>
import { defineProps } from "vue";
import router from "@/router/index.js";

const props = defineProps({
  sessions: {
    type: Array,
    required: true
  }
});

function formatDate(dateString) {
  return new Date(dateString).toLocaleString(undefined, {
    dateStyle: 'short',
    timeStyle: 'short',
  });
}

const goToSession= (id) => {
  router.push({ name: "SessionDetail", params: { id: id } });
};
</script>

<template>
  <table>
    <thead>
    <tr>
      <th>ID</th>
      <th>TIME</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="session in sessions" :key="session.id" @click="goToSession(session.sessionId)">
      <td>{{ session.sessionId }}</td>
      <td>{{ formatDate(session.createdTime) }}</td>
    </tr>
    </tbody>
  </table>

</template>

<style scoped>
tr{
  transition: background-color 0.4s;
}
tr:hover{
  background-color: #F6F7F8;
  cursor: pointer;
}
</style>
