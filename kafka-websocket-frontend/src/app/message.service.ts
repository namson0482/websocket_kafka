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
    // tslint:disable-next-line:only-arrow-functions
    this.stompClient.connect({}, function(frame) {
      that.stompClient.subscribe('/message', (message) => {
        if (message.body) {
          const value: string =  message.body;
          const array = value.split('@');
          that.msg = [];
          for (let i = 0; i < array.length; i++) {
            that.msg.push(array[i]);
            console.log(array[i]);
          }
        }
      });
    });
  }

  sendMessage(message) {
    this.stompClient.send('/app/send/message' , {}, message);
  }
}
