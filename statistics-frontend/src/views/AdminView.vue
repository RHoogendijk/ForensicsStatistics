<script setup>
import {onMounted, ref} from "vue";
import {useUsers} from "@/composables/useUsers.js";
import {useClasses} from "@/composables/useClasses.js"

const {users, usersErr, usersLoading, fetchUsers} = useUsers()
const {classes, classesErr, classesLoading, fetchClasses, createClass, creatingClass} = useClasses()

const className = ref('')

onMounted(async () => {
  await fetchUsers()
  await fetchClasses()
})
</script>

<template>
  <div class="body-container">
    <h2>Administrator Dashboard</h2>
    <h4>Users:</h4>
    <div v-if="usersLoading">Loading...</div>
    <div v-else-if="usersErr">Error: {{ usersErr.message }}</div>
    <table v-else>
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Role</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users">
        <td>{{ user.id }}</td>
        <td>{{ user.fullName }}</td>
        <!--        TODO: add role change functionality-->
        <td>{{ user.role }}</td>
      </tr>
      </tbody>
    </table>
    <h4>Classes:</h4>
    <div v-if="classesLoading">Loading...</div>
    <div v-else-if="classesErr">Error: {{ classesErr.message }}</div>
    <div
      class="class-container"
      v-else
      v-for="schoolClass in classes"
    >
      <h5>
        {{schoolClass.id}}
      </h5>
    </div>
    <h5>Add a class</h5>
    <div class="inputField">
      <input
        type="text"
        v-model="className"
        placeholder="P203"
      >
    </div>

    <button @click="createClass(className)">Add class</button>
    <p v-if="creatingClass">Adding...</p>
  </div>
</template>

<style scoped>




</style>
