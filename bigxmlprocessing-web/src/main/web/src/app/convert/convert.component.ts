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
    subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          this.progress = Math.round(event.loaded / event.total * 100);
          console.log(`Uploaded! ${this.progress}%`);
          break;
        case HttpEventType.Response:
          console.log('Ok', event.body);
          // alert("Alert   " +event.body);
        this.toastr.success('Convert Successfully')
          setTimeout(() => {
            this.progress = 100;
          }, 1500);
      }
    });
    form.reset();
  }

}
