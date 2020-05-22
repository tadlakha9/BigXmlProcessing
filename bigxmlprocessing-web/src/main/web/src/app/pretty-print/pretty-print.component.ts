import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';

import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-pretty-print',
  templateUrl: './pretty-print.component.html',
  styleUrls: ['./pretty-print.component.css']
})
export class PrettyPrintComponent implements OnInit {

  title = 'BigXMLProcessing';
  progress: number = 0;
  inputfile:File;

  constructor(private appService:AppService, private toastr:ToastrService,
    ) { }

  ngOnInit() {
 
  }

  onSelectInputFile(event) {
  console.log(event.target.files);
  this.inputfile =event.target.files[0];
  }

  save(form:NgForm)
  { 
    console.log("Documented will be pretty Printed ");
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.inputfile, this.inputfile.name);
    
    //to be included fileType(SGM file) option as well in future 
    //formData.append('fileType', form.value.fileType);
  
    this.appService.prettyPrintService(formData).
    subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          this.progress = Math.round(event.loaded / event.total * 100);
          console.log(`Uploaded! ${this.progress}%`);
          break;
        case HttpEventType.Response:
          console.log('Ok', event.body);
          alert("Alert   " +event.body);
        this.toastr.success('Pretty-Print Successfully')
          setTimeout(() => {
            this.progress = 100;
          }, 1500);
      }
    });
   
   form.reset();
  }

}
