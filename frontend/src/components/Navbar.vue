<template>
  <nav class="navbar">
    <div class="navbar-brand">CRM System</div>
    <ul class="navbar-nav">
      <li>
        <router-link to="/dashboard">Dashboard</router-link>
      </li>
      <li>
        <router-link to="/customers">Customers</router-link>
      </li>
      <li>
        <router-link to="/contacts">Contacts</router-link>
      </li>
      <li class="user-info">
        <span class="user-name">{{ userName }}</span>
        <a href="/logout" class="logout-btn">Logout</a>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const userName = ref('User')

onMounted(() => {
  // Get user info from storage or API
  const storedUser = localStorage.getItem('userName')
  if (storedUser) {
    userName.value = storedUser
  }
})
</script>

<style scoped>
.navbar {
  background: #667eea;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-brand {
  font-size: 24px;
  font-weight: bold;
}

.navbar-nav {
  display: flex;
  gap: 20px;
  list-style: none;
  align-items: center;
}

.navbar-nav a {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 5px;
  transition: background 0.3s;
}

.navbar-nav a:hover {
  background: rgba(255,255,255,0.2);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  font-size: 14px;
}

.logout-btn {
  background: #e74c3c !important;
}
</style>
