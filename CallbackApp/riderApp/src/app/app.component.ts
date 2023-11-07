import { Component, OnInit } from '@angular/core';
import { WebsocketService } from './websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'Biker Hazard Notifier';
  accepting = false;
  safe = true;
  blink = false;

  constructor(private websocketService: WebsocketService) {}

  ngOnInit(): void {}

  toggle(): void {
    this.accepting = !this.accepting;
    if(this.accepting) {
      this.blink = true;
      this.activateSound();
      setTimeout(() => this.blink = false, 3000);

      this.websocketService.connect();
      this.websocketService.messageReceived.subscribe((message: string) => {
        if(message !== 'go') {
          this.safe = false;
          this.soundCaution();
          setTimeout(() => this.safe = true, 5000);
        }
      });
    }
    else {
      this.websocketService.closeConnection();
      this.safe = true;
    }
  }

  soundCaution(): void {
    let audio = new Audio();
    audio.src = 'assets/alert.mp3';
    audio.load();
    audio.play();
  }

  activateSound(): void {
    let audio = new Audio();
    audio.src = 'assets/activate.mp3';
    audio.load();
    audio.play();
  }
}
