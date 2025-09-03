import { Injectable } from "@angular/core";
import { BaseService } from "my-angular-commons2";

@Injectable({
  providedIn: 'root'
})
export class XxxService extends BaseService{
  constructor() {
    super();
    super.setResourceName('xxxs');
    super.setBaseUrl('http://localhost:8081');
  }
}