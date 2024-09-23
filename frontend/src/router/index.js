import { createRouter, createWebHistory } from 'vue-router';
import IndexView from '@/pages/index.vue';
import WTARanking from '@/pages/WTARanking.vue';
import ATPRanking from '@/pages/ATPRanking.vue';
import Account from '@/pages/account.vue';
import RegisterView from '@/pages/register.vue';
import playerDetails from '@/pages/playerDetails.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: IndexView // Domyślnie ładowany komponent
  },
  {
    path: '/WTARanking',
    name: 'WTARanking',
    component: WTARanking
  },
  {
    path: '/ATPRanking',
    name: 'ATPRanking',
    component: ATPRanking
  },
  {
    path: '/account',
    name: 'Account',
    component: Account,
    meta: { requiresAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/player/:id',  // Ścieżka z dynamicznym parametrem id
    name: 'player-details',
    component: playerDetails,
    props: true  // Pozwala przekazać ID zawodnika jako props
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

// Przed każdą trasą sprawdzamy, czy użytkownik jest zalogowany
router.beforeEach(async (to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // Sprawdzenie stanu sesji
    const response = await fetch('http://localhost:8080/checkSession', {
      method: 'GET',
      credentials: 'include'
    });
    const result = await response.json();

    if (!result.loggedIn) {
      next({ path: '/', query: { loginError: 'Musisz być zalogowany, aby uzyskać dostęp do tej strony.' } });
    } else {
      next(); // Użytkownik jest zalogowany, przejście dalej
    }
  } else {
    next(); // Trasa nie wymaga autoryzacji
  }
});

export default router;
