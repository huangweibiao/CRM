<template>
  <div class="home">
    <!-- Main Home Page will be handled by Spring Boot template -->
    <div style="text-align: center; padding: 50px;">
      <h1>Loading CRM System...</h1>
    </div>
  </div>
</template>

<script setup lang="ts">
// Home View - This will redirect to the Thymeleaf template
import { onMounted } from 'vue'

onMounted(() => {
  // Let Spring Boot handle the home page
  window.location.href = '/'
})
</script>

<style scoped>
.home {
  min-height: 100vh;
}
</style>
