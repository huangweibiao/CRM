<template>
  <div class="dashboard">
    <Navbar />

    <div class="dashboard-content">
      <div class="welcome-section">
        <h1>Welcome to CRM Dashboard</h1>
        <p>Manage your customer relationships efficiently</p>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">150</div>
          <div class="stat-label">Total Customers</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">320</div>
          <div class="stat-label">Contacts</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">45</div>
          <div class="stat-label">Active Deals</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">12</div>
          <div class="stat-label">New This Week</div>
        </div>
      </div>

      <div class="content-section">
        <h2>Recent Activity</h2>
        <ul class="recent-activity">
          <li>
            <div class="activity-item">
              <div class="activity-text">
                <strong>New customer added</strong> - John Doe
              </div>
              <div class="activity-time">2 hours ago</div>
            </div>
          </li>
          <li>
            <div class="activity-item">
              <div class="activity-text">
                <strong>Contact updated</strong> - Jane Smith
              </div>
              <div class="activity-time">4 hours ago</div>
            </div>
          </li>
          <li>
            <div class="activity-item">
              <div class="activity-text">
                <strong>Deal closed</strong> - ABC Corp
              </div>
              <div class="activity-time">1 day ago</div>
            </div>
          </li>
          <li>
            <div class="activity-item">
              <div class="activity-text">
                <strong>Meeting scheduled</strong> - Tech Solutions
              </div>
              <div class="activity-time">2 days ago</div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/Navbar.vue'
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f5f6fa;
}

.dashboard-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.welcome-section {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 10px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  text-transform: uppercase;
}

.content-section {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.content-section h2 {
  color: #333;
  margin-bottom: 20px;
}

.recent-activity {
  list-style: none;
}

.recent-activity li {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.recent-activity li:last-child {
  border-bottom: none;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-text {
  flex: 1;
}

.activity-time {
  color: #999;
  font-size: 12px;
}
</style>
