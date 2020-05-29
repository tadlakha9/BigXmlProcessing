import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { log } from 'util';
import { TagPlaceholder } from '@angular/compiler/src/i18n/i18n_ast';
import { CATCH_ERROR_VAR } from '@angular/compiler/src/output/output_ast';
import { NG_MODEL_WITH_FORM_CONTROL_WARNING } from '@angular/forms/src/directives';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-pretty-print',
  templateUrl: './pretty-print.component.html',
  styleUrls: ['./pretty-print.component.css']
})
export class PrettyPrintComponent implements OnInit {

  title = 'BigXMLProcessing';
  progress: number = 0;
  inputfile:File;
  code:String;
  display = true;

  constructor(private appService:AppService, private toastr:ToastrService,
    ) { }

  ngOnInit() {
    this.code = "Here is the code beautifier. Makes it easier for you to read and understand the Document";
  }
  

  onSelectInputFile(event) {
  console.log(event.target.files);
  this.inputfile =event.target.files[0];
  }

  save(form:NgForm)
  { 
    this.display = true;
    console.log("Documented will be pretty Printed ");
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.inputfile, this.inputfile.name);
    
    //to be included fileType(SGM file) option as well in future 
    //formData.append('fileType', form.value.fileType);
  
    this.appService.prettyPrintService(formData).
    subscribe((response) => {      
          console.log('Ok', response);
          alert("Alert   " +response);
          this.toastr.success('Pretty-Print Successfully')
          this.progress = 100;
          setTimeout(() => { 
          this.display = false;}, 1100); 
        },(error) => { 
          console.log("Error "+error.error);  
          this.toastr.error('Error in Pretty Print'); 
          alert("Alert : \r\n  " + error.error); 
         }
      );
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
