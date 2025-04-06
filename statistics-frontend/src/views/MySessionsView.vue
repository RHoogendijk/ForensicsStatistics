<script setup>
import {computed, inject, onMounted, ref} from "vue";
import SessionListComponent from "@/components/SessionListComponent.vue";

const sessionService = inject("sessionService");
const userId = ref(sessionService.getId())
const sessions = ref([])
</script>

<template>
<div class="body-container">
  <h2>My Sessions</h2>
  <h3 v-if="sessions.length === 0">You don't have any sessions yet</h3>
  <SessionListComponent
    v-else
    :sessions="sessions"
  ></SessionListComponent>
</div>
</template>

<style scoped>

</style>
