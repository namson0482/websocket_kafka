import { Injectable } from '@angular/core';
declare var SockJS;
declare var Stomp;
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor() {
    this.initializeWebSocketConnection();
  }
  public stompClient;
  public msg = [];
  initializeWebSocketConnection() {
    const serverUrl = environment.app_url;
    console.log(serverUrl);
    const ws = new SockJS(serverUrl);
    this.stompClient = Stomp.over(ws);
    const that = this;
    this.stompClient.connect({}, function(frame) {
      that.stompClient.subscribe('/message', (message) => {
        if (message.body) {
          const value: string =  message.body;
          const arrayTempOne = value.split('@');
          that.msg = [];
          for (let i = 0; i < arrayTempOne.length; i++) {
            const arrayTempTwo = arrayTempOne[i].split('$');
            console.log(arrayTempTwo);
            that.msg.push(arrayTempTwo);
          }
        }
      });
    });
  }

  sendMessage(message) {
    this.stompClient.send('/app/send/message' , {}, message);
  }
}
