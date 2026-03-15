<template>
  <div class="login">
    <div class="login-container">
      <div class="logo">🚀</div>
      <h1>CRM System</h1>
      <p>Customer Relationship Management</p>

      <a href="/oauth2/authorization/oauth2-server" class="oauth-btn">
        Sign in with OAuth2
      </a>

      <div v-if="error" class="error-message">
        Login failed. Please try again.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const error = ref(false)

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  error.value = urlParams.get('error') === 'true'
})
</script>

<style scoped>
.login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-container {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.logo {
  font-size: 48px;
  margin-bottom: 20px;
}

h1 {
  color: #333;
  margin-bottom: 10px;
}

p {
  color: #666;
  margin-bottom: 30px;
}

.oauth-btn {
  background: #667eea;
  color: white;
  padding: 12px 30px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: background 0.3s;
}

.oauth-btn:hover {
  background: #5568d3;
}

.error-message {
  color: #e74c3c;
  margin-top: 20px;
  padding: 10px;
  border-radius: 5px;
  background: #ffeaea;
}
</style>
