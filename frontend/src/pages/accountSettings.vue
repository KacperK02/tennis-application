<template>
    <div>
      <h1>Zmień nazwę użytkownika</h1>
      <form @submit.prevent="changeUsername">
        <div>
          <label>Nowa nazwa użytkownika:</label>
          <input v-model="newUsername" type="text" required />
        </div>
        <div>
          <label>Potwierdź nową nazwę:</label>
          <input v-model="confirmUsername" type="text" required />
        </div>
        <div>
          <label>Hasło:</label>
          <input v-model="password" type="password" required />
        </div>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <button type="submit">Zmień nazwę użytkownika</button>
      </form>
    </div>
  </template>
  
  <script>
    import '../css/accountSettings.css'

  export default {
    data() {
      return {
        newUsername: '',
        confirmUsername: '',
        password: '',
        errorMessage: '',
        successMessage: ''
      };
    },
    methods: {
      async changeUsername() {
        if (this.newUsername !== this.confirmUsername) {
          this.errorMessage = 'Nazwy użytkownika nie są zgodne.';
          return;
        }
        this.errorMessage = '';
        try {
          const response = await fetch('http://localhost:8080/change-username', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
              newUsername: this.newUsername,
              password: this.password
            })
          });
          const result = await response.json();
          if (response.ok) {
            this.$router.push(`/account`);
          } else {
            this.errorMessage = result.message || 'Błąd podczas zmiany nazwy użytkownika.';
          }
        } catch (error) {
          console.error('Błąd:', error);
          this.errorMessage = 'Wystąpił problem z połączeniem.';
        }
      }
    }
  };
  </script>