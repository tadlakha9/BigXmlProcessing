import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { splitAtColon } from '@angular/compiler/src/util';
import { NgxSpinnerService } from 'ngx-spinner';

import { toBase64String } from '@angular/compiler/src/output/source_map';
import { ToastrService } from 'ngx-toastr';
import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-split',
  templateUrl: './split.component.html',
  styleUrls: ['./split.component.css']
})
export class SplitComponent implements OnInit {

  filePath:File;
  Catfile:File;
  typeOfSplit:string;
  splitType = ['line', 'size'];
  splitByRadio='';
  fileType:String;
  progress: number = 0;
 
  constructor(private appService:AppService, private toastr:ToastrService,
    private spinner: NgxSpinnerService) { }

  
  ngOnInit() {
  }

  isSelected(valueSelected){
    if(this.splitByRadio===valueSelected){
      return true;
    }else{
      return false;
    }

  }
  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.filePath = event.target.files[0];
  }

  onSelectedCatalogFile(event){
    console.log(event.target.files);
    this.Catfile = event.target.files[0];
  }
  save(form:NgForm){
  // this.spinner.show();
    console.log(form.value);
    let formData = new FormData();
    if(this.filePath == undefined){
      this.filePath = new File([""], "test.txt");
    }
    formData.append('file', this.filePath, this.filePath.name); 
    formData.append('typeOfSplit', form.value.typeOfSplit);       
    formData.append('level', form.value.level);
    formData.append('size', form.value.size);
    formData.append('splitByElement', form.value.splitByElement);
    formData.append('splitType', form.value.splitType);
    formData.append('splitByLine', form.value.splitByLine);
    formData.append('splitBySize', form.value.splitBySize);
    formData.append('fileType', form.value.fileType);
    if(this.Catfile == undefined){
      this.Catfile = new File([""], "catalog.txt");
    }
    formData.append('filecat', this.Catfile, this.Catfile.name);
    
    this.appService.splitService(formData).
    subscribe((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          this.progress = Math.round(event.loaded / event.total * 100);
          console.log(`Uploaded! ${this.progress}%`);
          break;
        case HttpEventType.Response:
          console.log('Ok', event.body);
          // alert("Alert   " +event.body);
        this.toastr.success('Split Successfully')
          setTimeout(() => {
            this.progress = 100;
          }, 1500);
      }
    },(error) => {
      console.log("ko"+error);  
      this.toastr.error('Error on Splitting');
    }

    );


    form.reset();
    

  }


}
