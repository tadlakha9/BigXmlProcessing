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
  
   constructor(private appService:AppService, private toastr:ToastrService,
    ) { }
  
  ngOnInit() {
    this.sortType="Default"
  }
  submitform(form:NgForm){
  
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.xmlFilePath, this.xmlFilePath.name); 
    formData.append('sortType', form.value.sortType);       
    formData.append('attribute', form.value.attribute);
    formData.append('keyattribute', form.value.keyattribute);
    formData.append('idattribute', form.value.idattribute);
    this.appService.sortService(formData).
    subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          this.progress = Math.round(event.loaded / event.total * 100);
          console.log(`Uploaded! ${this.progress}%`);
          break;
        case HttpEventType.Response:
          console.log('Ok', event.body);
          alert("Alert   " +event.body);
        this.toastr.success('Sort Successfully')
          setTimeout(() => {
            this.progress = 100;
          }, 1500);
      }
    });

    form.reset();
    console.log('inside submit button');
  }

  onSelectofXMLFile(event){
    console.log(event.target.files);
    this.xmlFilePath = event.target.files[0];
  }

}