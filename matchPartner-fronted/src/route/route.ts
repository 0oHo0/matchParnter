import Search from '../pages/search.vue'
import Home from '../pages/index.vue'
import Team from '../pages/team.vue'
import User from '../pages/user.vue'
import UserEdit from '../pages/userEdit.vue'
import searchRes from '../pages/searchRes.vue'
import login from '../pages/login.vue'
import * as VueRouter from 'vue-router'

const routes = [
    { path: '/', component: Home },
    { path: '/team', component: Team },
    { path: '/search', component: Search },
    { path: '/user', component: User },
    { path: '/user/edit', component: UserEdit },
    { path: '/searchRes', component: searchRes },
    { path: '/user/login', component: login },
]

const router = VueRouter.createRouter({
    // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
    history: VueRouter.createWebHashHistory(),
    routes, // `routes: routes` 的缩写
})

export default router;