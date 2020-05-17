import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder} from "@angular/forms";
import {  NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-parsing',
  templateUrl: './parsing.component.html',
  styleUrls: ['./parsing.component.css']
})
export class ParsingComponent implements OnInit {

  
  profileForm:FormGroup;
  xmlFilePath:File;
  xsdFilePath:File;
  xsltFilePath:File;
  errorFilePath:File;
  
  constructor(private fb:FormBuilder, private appService:AppService,private toastr:ToastrService,
    private spinner: NgxSpinnerService) { }
  
  ngOnInit(){
    this.profileForm = this.fb.group({
      profile:['']
    });
  }
  
  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.xmlFilePath = event.target.files[0];
  }
  onSelectedXSDFile(event){
    console.log(event.target.files);
    this.xsdFilePath = event.target.files[0];
  }
  
  onSelectedERRORFile(event){
    console.log(event.target.files);
  }
  
  save(form:NgForm){
  this.spinner.show();
  let formData = new FormData();
  formData.append('file', this.xmlFilePath, this.xmlFilePath.name);      
  formData.append('xsdFile', this.xsdFilePath, this.xsdFilePath.name);
    
  this.appService.parseXML(formData).
    subscribe(
      (response => {
        this.spinner.hide();
        console.log("ok"+response);
        this.toastr.success('File parse done Successfully'+response)
        }),
      (error) => {
        console.log("ko"+error); 
        this.spinner.hide();
        this.toastr.error('Error on file Parse'+error);
      }
    );

    form.reset();
}

}
