import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environment/environment';
import { SuccessDialogService } from './success-dialog.service';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  private baseUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private successDialog: SuccessDialogService
  ) {}

  get<T>(path: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, { params });
  }

  getBlob(path: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}${path}`, {
      responseType: 'blob'
    });
  }

  post<T>(path: string, body: unknown,
    successMessage = 'Operation completed successfully.'
  ): Observable<T> {
    return this.http.post<T>(
      `${this.baseUrl}${path}`,
      body
    ).pipe(
      tap(() => {
        this.successDialog.show({
          message: successMessage
        });
      })
    );
  }

  postSilently<T>(
    path: string,
    body: unknown
  ): Observable<T> {
    return this.http.post<T>(
      `${this.baseUrl}${path}`,
      body
    );
  }

  put<T>(
    path: string,
    body: unknown,
    successMessage = 'Changes saved successfully.'
  ): Observable<T> {
    return this.http.put<T>(
      `${this.baseUrl}${path}`,
      body
    ).pipe(
      tap(() => {
        this.successDialog.show({
          message: successMessage
        });
      })
    );
  }

  putSilently<T>(
    path: string,
    body: unknown
  ): Observable<T> {
    return this.http.put<T>(
      `${this.baseUrl}${path}`,
      body
    );
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`);
  }

  patch<T>(path: string, body: unknown): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${path}`, body);
  }
}
