import { Injectable } from "@angular/core";
import { BaseService } from "my-angular-commons2";

@Injectable({
  providedIn: 'root'
})
export class TicketService extends BaseService{
  constructor() {
    super();
    super.setResourceName('tickets');
    super.setBaseUrl('http://localhost:8081');
  }
}