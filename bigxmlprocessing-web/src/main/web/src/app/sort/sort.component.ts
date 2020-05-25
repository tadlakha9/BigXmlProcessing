import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';


import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.css']
})
export class SortComponent implements OnInit {
  xmlFilePath:File;
  sortType:string;
  progress: number = 0;
  display = true;
  
   constructor(private appService:AppService, private toastr:ToastrService,
    ) { }
  
  ngOnInit() {
    this.sortType="Default"
  }
  submitform(form:NgForm){
    this.display = true;
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.xmlFilePath, this.xmlFilePath.name); 
    formData.append('sortType', form.value.sortType);       
    formData.append('attribute', form.value.attribute);
    formData.append('keyattribute', form.value.keyattribute);
    formData.append('idattribute', form.value.idattribute);
    this.appService.sortService(formData).
    subscribe((response) => {
          console.log('Ok', response);
          alert("Alert   " +response);
        this.toastr.success('Sort Successfully')
        this.progress = 100;
        setTimeout(() => {          
          this.display = false;
        }, 1100);
      
    });

    this.updatingProgressBar();
    form.reset();
    console.log('inside submit button');
  }

  onSelectofXMLFile(event){
    console.log(event.target.files);
    this.xmlFilePath = event.target.files[0];
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