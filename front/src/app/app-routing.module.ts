import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { UsersComponent } from './pages/users/users.component';
import { AddUserComponent } from './pages/add-user/add-user.component';
import { EditUserComponent } from './pages/edit-user/edit-user.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { CreateOrderComponent } from './pages/create-order/create-order.component';
import { ChefOrdersComponent } from './pages/chef-orders/chef-orders.component';
import { TrackOrderComponent } from './pages/track-order/track-order.component';
import { ErrorsComponent } from './errors/errors.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'users', component: UsersComponent, canActivate: [AuthGuard], data: { permission: 'can_read_users' } },
  { path: 'add-user', component: AddUserComponent, canActivate: [AuthGuard], data: { permission: 'can_create_users' } },
  { path: 'edit-user/:id', component: EditUserComponent, canActivate: [AuthGuard], data: { permission: 'can_update_users' } },
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard], data: { permission: 'can_read_orders' } },
  { path: 'create-order', component: CreateOrderComponent, canActivate: [AuthGuard], data: { permission: 'can_create_orders' } },
  { path: 'track-order/:id', component: TrackOrderComponent, canActivate: [AuthGuard], data: { permission: 'can_read_orders' } },
  { path: 'chef-orders', component: ChefOrdersComponent, canActivate: [AuthGuard], data: { permission: 'can_read_orders' } },
  { path: 'errors', component: ErrorsComponent, canActivate: [AuthGuard], data: { permission: 'can_read_orders' } },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
