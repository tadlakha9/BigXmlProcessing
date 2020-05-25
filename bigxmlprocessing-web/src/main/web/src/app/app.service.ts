import { HttpClient, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class AppService{
    
    baseUrl = "http://127.0.0.1:8090/home/";
    transformXmlUrl:string = this.baseUrl + "transformXml";
    splitServiceUrl = this.baseUrl + "splitXml";
    sortServiceUrl = this.baseUrl + "sortXml";
    prettyPrintServiceUrl = this.baseUrl + "prettyPrintXml";
    searchServiceUrl = this.baseUrl + "searching";
    feedbackServiceUrl = this.baseUrl + "feedback";
    convertURL = this.baseUrl + "convert";

    constructor(private httpClient: HttpClient) {}


    parseXML(data){
        return this.httpClient.post('http://127.0.0.1:8090/home/parseXml', data, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
    }

    splitService(formData){        
        return this.httpClient.post(this.splitServiceUrl, formData, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
    }

    sortService(formData){        
        return this.httpClient.post(this.sortServiceUrl, formData, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
     }

     feedbackService(formData){        
        return this.httpClient.post(this.feedbackServiceUrl, formData, {responseType: 'text'});
 	}


    prettyPrintService(formData){
        return this.httpClient.post(this.prettyPrintServiceUrl,formData, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
    }

    searchService(formData){
        return this.httpClient.post(this.searchServiceUrl,formData, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
    }
    convertService(formData){
        return this.httpClient.post(this.convertURL, formData, {
            responseType: 'text',
            reportProgress: true,
            observe: 'body'});
    }

}