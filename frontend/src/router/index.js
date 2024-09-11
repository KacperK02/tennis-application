import { createRouter, createWebHistory } from 'vue-router';
import IndexView from '@/pages/index.vue';
import WTARanking from '@/pages/WTARanking.vue';
import ATPRanking from '@/pages/ATPRanking.vue';

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
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;
