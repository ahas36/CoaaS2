import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private socket!: WebSocket;
  messageReceived: Subject<string> = new Subject<string>();

  constructor() { }

  connect(): void {
    // This application emulates the application on BIC001.
    this.socket = new WebSocket('ws://localhost:8070/CASM-2.0.1/contexts/BIC001');

    this.socket.onopen = () => {
      console.log('WebSocket connection established.');
    };

    this.socket.onmessage = (event) => {
      let sendMessage = 'go';
      let message = JSON.parse(event.data);
      // console.log(message);
      if(message['car'].hasOwnProperty('results')) {
        var numberOfHazs = Object.keys(message['car']['results']).length
        if(numberOfHazs > 0){
          sendMessage = 'stop';
        }
      }
      else {
        sendMessage = 'stop';
      }
      this.messageReceived.next(sendMessage);
    };

    this.socket.onclose = (event) => {
      console.log('WebSocket connection closed:', event);
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  sendMessage(message: string): void {
    this.socket.send(message);
  }

  closeConnection(): void {
    this.socket.close();
  }
}