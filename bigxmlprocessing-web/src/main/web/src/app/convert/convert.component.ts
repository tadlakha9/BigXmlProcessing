import { Component, OnInit } from '@angular/core';
import {AppService } from '../app.service';
import {ToastrService } from 'ngx-toastr';
import { NgForm } from '@angular/forms';
import { NAMED_ENTITIES } from '@angular/compiler';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-convert',
  templateUrl: './convert.component.html',
  styleUrls: ['./convert.component.css']
})
export class ConvertComponent implements OnInit {

  sgmlfile:File;
  catalogfile:File;


  constructor(private appService:AppService, private toastr:ToastrService,
    private spinner: NgxSpinnerService) { }

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
    this.spinner.show();
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
    subscribe((response) => 
    { console.log("ok"+response);
      this.spinner.hide();
      this.toastr.success('Document Converted Successfully')
    },
    (error) => 
    { console.log("ko"+error);  
      this.spinner.hide();
      this.toastr.error('Error in Conversion');  
    });
    form.reset();
  }

}
