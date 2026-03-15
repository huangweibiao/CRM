<template>
  <div class="customers">
    <Navbar />

    <div class="customers-content">
      <div class="header-section">
        <h1>Customers</h1>
        <button class="btn btn-primary" @click="showAddModal = true">
          Add Customer
        </button>
      </div>

      <div class="customers-list">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="customer in customers" :key="customer.id">
              <td>{{ customer.id }}</td>
              <td>{{ customer.name }}</td>
              <td>{{ customer.email }}</td>
              <td>{{ customer.phone }}</td>
              <td>
                <span class="status" :class="customer.status">
                  {{ customer.status }}
                </span>
              </td>
              <td>
                <button class="btn btn-sm" @click="viewCustomer(customer.id)">View</button>
                <button class="btn btn-sm" @click="editCustomer(customer.id)">Edit</button>
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

interface Customer {
  id: number
  name: string
  email: string
  phone: string
  status: string
}

const customers = ref<Customer[]>([])
const showAddModal = ref(false)

onMounted(() => {
  loadCustomers()
})

function loadCustomers() {
  // Mock data - replace with API call
  customers.value = [
    { id: 1, name: 'John Doe', email: 'john@example.com', phone: '+1-234-567-890', status: 'Active' },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com', phone: '+1-234-567-891', status: 'Active' },
    { id: 3, name: 'Bob Wilson', email: 'bob@example.com', phone: '+1-234-567-892', status: 'Inactive' }
  ]
}

function viewCustomer(id: number) {
  console.log('View customer:', id)
}

function editCustomer(id: number) {
  console.log('Edit customer:', id)
}
</script>

<style scoped>
.customers {
  min-height: 100vh;
  background: #f5f6fa;
}

.customers-content {
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

.customers-list {
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

.status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status.Active {
  background: #d4edda;
  color: #155724;
}

.status.Inactive {
  background: #f8d7da;
  color: #721c24;
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
