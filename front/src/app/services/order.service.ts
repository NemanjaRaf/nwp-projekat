import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  getOrders(
    status?: string,
    dateFrom?: string,
    dateTo?: string,
    userId?: number
  ): Observable<any[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (dateFrom) params = params.set('dateFrom', dateFrom);
    if (dateTo) params = params.set('dateTo', dateTo);
    if (userId) params = params.set('userId', userId.toString());

    return this.http.get<any[]>(this.apiUrl, {
      headers: this.getHeaders(),
      params: params,
    });
  }

  placeOrder(order: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, order, {
      headers: this.getHeaders(),
    });
  }

  scheduleOrder(order: any, scheduledTime: string): Observable<any> {
    const params = new HttpParams().set('scheduledTime', scheduledTime);
    return this.http.post<any>(`${this.apiUrl}/schedule`, order, {
      headers: this.getHeaders(),
      params: params,
    });
  }

  getOrderById(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${orderId}`, {
      headers: this.getHeaders(),
    });
  }

  cancelOrder(orderId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${orderId}`, {
      headers: this.getHeaders(),
    });
  }

  trackOrder(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/track/${orderId}`, {
      headers: this.getHeaders(),
    });
  }

  getMenu(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/menu`, {
      headers: this.getHeaders(),
    });
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${orderId}/status`, { status });
  }

  acceptOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/accept/${orderId}`, null, {
      headers: this.getHeaders(),
    });
  }

  rejectOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/reject/${orderId}`, null, {
      headers: this.getHeaders(),
    });
  }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    console.log('token', token);
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
}
