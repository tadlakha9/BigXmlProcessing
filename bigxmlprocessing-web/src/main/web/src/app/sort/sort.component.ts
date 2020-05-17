import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { formArrayNameProvider } from '@angular/forms/src/directives/reactive_directives/form_group_name';

@Component({
  selector: 'app-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.css']
})
export class SortComponent implements OnInit {
  xmlFilePath:File;
  sortType:string;
   constructor(private appService:AppService, private toastr:ToastrService,
    private spinner: NgxSpinnerService) { }
  
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
    subscribe(
      (response => {
        console.log("ok"+response);
        alert("Alert   " +response);
        this.spinner.hide();
        this.toastr.success('Sort Successfully')
        }),
      (error) => {
        console.log("ko"+error);
        alert("Alert   " +error);
        this.spinner.hide();  
        this.toastr.error('Error on Sorting');
      }
    );

    form.reset();
    console.log('inside submit button');
  }

  onSelectofXMLFile(event){
    console.log(event.target.files);
    this.xmlFilePath = event.target.files[0];
  }

}