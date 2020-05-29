import { Component, OnInit } from '@angular/core';
import {AppService } from '../app.service';
import {ToastrService } from 'ngx-toastr';
import { NgForm } from '@angular/forms';
import { NAMED_ENTITIES } from '@angular/compiler';

import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-convert',
  templateUrl: './convert.component.html',
  styleUrls: ['./convert.component.css']
})
export class ConvertComponent implements OnInit {

  sgmlfile:File;
  catalogfile:File;
  progress: number = 0;
  display = true;

  constructor(private appService:AppService, private toastr:ToastrService,
    ) { }

  ngOnInit() { 
  }

  onSelectSGMLFile(event) {
    console.log(event.target.files);
    this.sgmlfile= event.target.files[0];
  }

  onSelectCatalogFile(event) {
    console.log(event.target.files);
    this.catalogfile= event.target.files[0];
  }


  save(form:NgForm){
    
    console.log("Document will be converted");
    let formData = new FormData();
    console.log(form.value);

    formData.append('file0', this.sgmlfile, this.sgmlfile.name);
  
    if(this.catalogfile == undefined ){
      this.catalogfile = new File([""], "filecatalog.txt");
      formData.append('file1', this.catalogfile, this.catalogfile.name);
    } else{
      formData.append('file1', this.catalogfile, this.catalogfile.name); 
    }

    this.appService.convertService(formData).
    subscribe((response) => {
          console.log('Ok', response);
          alert("Alert   " +response);
        this.toastr.success('Convert Successfully')
        this.progress = 100;
        setTimeout(() => {          
          this.display = false;
        }, 1100);
    },(error) => {
      console.log("ko"+ error.error);  
      this.toastr.error('Error in Conversion');
      alert("Alert : \r\n  " + error.error); 
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
