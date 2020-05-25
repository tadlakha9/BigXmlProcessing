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
  display = true;
 

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
    subscribe((response) => {
          console.log('Ok', response);
          alert("Alert   " +response);
        this.toastr.success('Parsing Successfully')
        this.progress = 100;
        setTimeout(() => {          
          this.display = false;
        }, 1100);
      
    });
    this.updatingProgressBar();
    form.reset();
  }

  updatingProgressBar(){
    console.log(this.progress);
    setTimeout(() => {
      this.progress = 1;
    }, 1000);
    setTimeout(() => {
      this.progress = 4;
    }, 1500);
    setTimeout(() => {
      this.progress = 11;
    }, 2000);
    setTimeout(() => {
      this.progress = 23;
    }, 7000);
    setTimeout(() => {
      this.progress = 29;
    }, 10000);
    setTimeout(() => {
      this.progress = 36;
    }, 15000);
    setTimeout(() => {
      this.progress = 46;
    }, 20000);
    setTimeout(() => {
      this.progress = 52;
    }, 30000);
    setTimeout(() => {
      this.progress = 62;
    }, 40000);
    setTimeout(() => {
      this.progress = 70;
    }, 50000);
    setTimeout(() => {
      this.progress = 90;
    }, 70000);
  }

}
