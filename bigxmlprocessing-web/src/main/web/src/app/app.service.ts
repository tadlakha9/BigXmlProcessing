import { HttpClient, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class AppService{
    transformXmlUrl:string = "http://127.0.0.1:8090/home/transformXml";

    constructor(private httpClient: HttpClient) {}


    transformXML(data){
        const newRequest = new HttpRequest('POST', 'http://127.0.0.1:8090/home/transformXml', data);
        return this.httpClient.request(newRequest);
    }
}