import { Component, OnInit } from '@angular/core';
import {AppService } from '../app.service';
import {ToastrService } from 'ngx-toastr';
import { NgForm } from '@angular/forms';
import { NAMED_ENTITIES } from '@angular/compiler';

@Component({
  selector: 'app-convert',
  templateUrl: './convert.component.html',
  styleUrls: ['./convert.component.css']
})
export class ConvertComponent implements OnInit {

  sgmlfile:File;
  catalogfile:File;
  errorfile:File;


  constructor(private appService:AppService, private toastr:ToastrService) { }

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

  onSelectErrorFile(event){
    console.log(event.target.files);
    this.errorfile= event.target.files[0];
  }


  save(form:NgForm)
  {
    console.log("Document will be converted");
    let formData = new FormData();
    console.log(form.value);

    formData.append('file0', this.sgmlfile, this.sgmlfile.name);
  
    if(this.catalogfile == undefined ){
      this.catalogfile = new File([""], "filecatalog.txt");
      formData.append('file1', this.catalogfile, this.catalogfile.name);
      console.log("if for cat",this.catalogfile);
    } else{
      formData.append('file1', this.catalogfile, this.catalogfile.name); 
      console.log("else for cat");
    }

    if(this.errorfile == undefined){ 
     this.errorfile= new File([""], "error.log");
     formData.append('file2', this.errorfile, this.errorfile.name);
     console.log("if for error", this.errorfile);
    }else {
      formData.append('file2', this.errorfile, this.errorfile.name);
      console.log("else for error");
    }

    this.appService.convertService(formData).
    subscribe((response) => 
    { console.log("ok"+response);
      this.toastr.success('Document Converted Successfully')
    },
    (error) => 
    { console.log("ko"+error);  
      this.toastr.error('Error in Conversion');  
    });
    form.reset();
  }

}
