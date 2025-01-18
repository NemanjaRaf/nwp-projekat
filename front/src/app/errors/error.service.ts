import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  private apiUrl = `${environment.apiUrl}/errors`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  getErrors(userId?: number): Observable<any[]> {
    let params = new HttpParams();
    if (userId) {
      params = params.set('userId', userId.toString());
    }
    return this.http.get<any[]>(this.apiUrl, {
      headers: this.getHeaders(),
      params: params,
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