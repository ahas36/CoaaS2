import { Component, OnInit } from '@angular/core';
import { WebsocketService } from './websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  receivedMessages: string[] = [];

  constructor(private websocketService: WebsocketService) {}

  ngOnInit(): void {
    this.websocketService.connect();
    this.websocketService.messageReceived.subscribe((message: string) => {
      this.receivedMessages.push(message);
    });
  }

  title = 'riderApp';
}
