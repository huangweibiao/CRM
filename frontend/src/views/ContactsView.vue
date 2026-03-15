<template>
  <div class="contacts">
    <Navbar />

    <div class="contacts-content">
      <div class="header-section">
        <h1>Contacts</h1>
        <button class="btn btn-primary" @click="showAddModal = true">
          Add Contact
        </button>
      </div>

      <div class="contacts-list">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Customer</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="contact in contacts" :key="contact.id">
              <td>{{ contact.id }}</td>
              <td>{{ contact.name }}</td>
              <td>{{ contact.email }}</td>
              <td>{{ contact.phone }}</td>
              <td>{{ contact.customerName }}</td>
              <td>
                <button class="btn btn-sm" @click="viewContact(contact.id)">View</button>
                <button class="btn btn-sm" @click="editContact(contact.id)">Edit</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Navbar from '@/components/Navbar.vue'

interface Contact {
  id: number
  name: string
  email: string
  phone: string
  customerName: string
}

const contacts = ref<Contact[]>([])
const showAddModal = ref(false)

onMounted(() => {
  loadContacts()
})

function loadContacts() {
  // Mock data - replace with API call
  contacts.value = [
    { id: 1, name: 'John Doe', email: 'john@example.com', phone: '+1-234-567-890', customerName: 'ABC Corp' },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com', phone: '+1-234-567-891', customerName: 'XYZ Ltd' },
    { id: 3, name: 'Bob Wilson', email: 'bob@example.com', phone: '+1-234-567-892', customerName: 'ABC Corp' }
  ]
}

function viewContact(id: number) {
  console.log('View contact:', id)
}

function editContact(id: number) {
  console.log('Edit contact:', id)
}
</script>

<style scoped>
.contacts {
  min-height: 100vh;
  background: #f5f6fa;
}

.contacts-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.header-section {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.contacts-list {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.table th {
  background: #f8f9fa;
  font-weight: 600;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  margin-right: 5px;
  background: #667eea;
  color: white;
  border-radius: 4px;
}
</style>
