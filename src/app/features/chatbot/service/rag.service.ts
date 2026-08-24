import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root',
})
export class RagService {
  private http = inject(HttpClient);
  private apiUrl = 'https://rag.stage.enset.top';

  sendMessage(question: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-API-Key': 'spelev-sec-key-zxvbnEE8YT65', 
    });

    return this.http.post<any>(`${this.apiUrl}/query`, { question }, { headers });
  }
}
