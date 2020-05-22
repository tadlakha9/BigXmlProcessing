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
  
  let formData = new FormData();
  for (var file of this.files) {
    formData.append('file' , file, file.name); 
  }
  formData.append('searchId', form.value.searchId);       
  formData.append('extension', form.value.extension);
  formData.append('text', form.value.text);
    
  
  this.appService.searchService(formData).
  subscribe((event: HttpEvent<any>) => {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.progress = Math.round(event.loaded / event.total * 100);
        console.log(`Uploaded! ${this.progress}%`);
        break;
      case HttpEventType.Response:
        console.log('Ok', event.body);
        // alert("Alert   " +event.body);
      this.toastr.success('Search Successfully')
        setTimeout(() => {
          this.progress = 100;
        }, 1500);
    }
  });

    form.reset();
}

}
