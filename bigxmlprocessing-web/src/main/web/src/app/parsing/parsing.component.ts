import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder, FormControl} from "@angular/forms";
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';						
import { ToastrService } from 'ngx-toastr';

import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-parsing',
  templateUrl: './parsing.component.html',
  styleUrls: ['./parsing.component.css']
})
export class ParsingComponent implements OnInit {

  
 // profileForm:FormGroup;
  inputFilePath:File;
  xsdFilePath:File;
 // xsltFilePath:File;
  errorFilePath:File;
  catalogFile:File;
  fileType:String;
  showcatalog:boolean = false;
  progress: number = 0;
 

  constructor(private fb:FormBuilder, private appService:AppService,private toastr:ToastrService,
    ){
    
  }
  
  ngOnInit(){
    }
  
  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.inputFilePath = event.target.files[0];
  }
  onSelectedXSDFile(event){
    console.log(event.target.files);
    this.xsdFilePath = event.target.files[0];
  }
  
  onSelectedERRORFile(event){
    console.log(event.target.files);
    this.errorFilePath = event.target.files[0];
  }
  
  onSelectedCatalogFile(event){
    console.log(event.target.files);
    this.catalogFile= event.target.files[0];
  }
 
  toggelXML() :void{
    this.showcatalog=false;
  }

  toggelcatalog() :void{
    this.showcatalog=true;
}

  parseXML(form:NgForm) {

    console.log("within on submit method" );
    
    let formData = new FormData();
    formData.append('file', this.inputFilePath, this.inputFilePath.name);
    formData.append('fileType', form.value.fileType);
    if(this.xsdFilePath ==undefined){
      this.xsdFilePath = new File([""],"xsd.txt");
    }
    formData.append('fileXsd', this.xsdFilePath,this.xsdFilePath.name);
   
    if(this.errorFilePath == undefined){
      this.errorFilePath = new File([""], "error.log");
    }
    formData.append('fileerror', this.errorFilePath, this.errorFilePath.name);
    
    if(this.catalogFile == undefined){
      this.catalogFile = new File([""], "catalog.txt");
    }
    formData.append('filecatalog', this.catalogFile, this.catalogFile.name);
    
    this.appService.parseXML(formData).
    subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          this.progress = Math.round(event.loaded / event.total * 100);
          console.log(`Uploaded! ${this.progress}%`);
          break;
        case HttpEventType.Response:
          console.log('Ok', event.body);
          // alert("Alert   " +event.body);
        this.toastr.success('Parsing Successfully')
          setTimeout(() => {
            this.progress = 100;
          }, 1500);
      }
    });
    form.reset();
  }

}
