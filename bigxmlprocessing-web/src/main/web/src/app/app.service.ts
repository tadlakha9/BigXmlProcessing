import { HttpClient, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class AppService{
    
    baseUrl = "http://127.0.0.1:8090/home/";
    transformXmlUrl:string = this.baseUrl + "transformXml";
    splitServiceUrl = this.baseUrl + "splitXml";
    sortServiceUrl = this.baseUrl + "sortXml";

    constructor(private httpClient: HttpClient) {}


    transformXML(data){
        const newRequest = new HttpRequest('POST', 'http://127.0.0.1:8090/home/transformXml', data);
        return this.httpClient.request(newRequest);
    }

    splitService(formData){        
        return this.httpClient.post(this.splitServiceUrl, formData, {responseType: 'text'});
    }
    sortService(formData){        
        return this.httpClient.post(this.sortServiceUrl, formData, {responseType: 'text'});
    }
}