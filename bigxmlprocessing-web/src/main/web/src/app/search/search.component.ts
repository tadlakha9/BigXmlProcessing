import { Component, OnInit } from '@angular/core';
import {  NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';

import { HttpEventType, HttpEvent } from '@angular/common/http';



@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  
  files:File[]=[];
  searchId:string;
  progress: number = 0;
  display = true;

  constructor(private appService:AppService, private toastr:ToastrService,
    ) { }

  ngOnInit() {
  }
  submitform(){
    console.log('inside submit button');
  }

  filesPicked(event){
    this.files = event.target.files;
    console.log("files:",this.files);    
  }

   

save(form:NgForm){
  this.display = true;
  let formData = new FormData();
  for (var file of this.files) {
    formData.append('file' , file, file.name); 
  }
  formData.append('searchId', form.value.searchId);       
  formData.append('extension', form.value.extension);
  formData.append('text', form.value.text);
    
  
  this.appService.searchService(formData).
  subscribe((response) => {
    
        console.log('Ok', response);
        alert("Alert   " +response);
      this.toastr.success('Search Successfully')
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
