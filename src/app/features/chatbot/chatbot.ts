import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatMessage, RagService } from './service/rag.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.html',
  styleUrls: ['./chatbot.scss'],
})
export class ChatbotComponent {
  private ragService = inject(RagService);

  isOpen = signal<boolean>(false);
  isLoading = signal<boolean>(false);
  userInput = '';

  // Typage strict et explicite pour éviter les erreurs d'itérateur
  messages: Array<ChatMessage> = [];

  toggleChat() {
    this.isOpen.update((val) => !val);
  }

  sendMessage() {
    if (!this.userInput.trim() || this.isLoading()) return;

    const questionText = this.userInput;
    this.userInput = '';

    this.messages.push({
      sender: 'user',
      text: questionText,
      timestamp: new Date(),
    });

    this.isLoading.set(true);

    this.ragService.sendMessage(questionText).subscribe({
      next: (response: any) => {
        // On cible spécifiquement response.answer d'après Postman
        const botReply = response.answer || response.response || JSON.stringify(response);

        this.messages.push({
          sender: 'bot',
          text: botReply,
          timestamp: new Date(),
        });
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erreur RAG:', err);
        this.messages.push({
          sender: 'bot',
          text: "Désolé, une erreur est survenue lors de la communication avec le serveur d'intelligence artificielle.",
          timestamp: new Date(),
        });
        this.isLoading.set(false);
      },
    });
  }
}
