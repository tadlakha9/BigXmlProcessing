import { Component, OnInit } from '@angular/core';
import {  NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';



@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  
  files:File[]=[];
  searchId:string;
  constructor(private appService:AppService, private toastr:ToastrService,
    private spinner: NgxSpinnerService) { }

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
  this.spinner.show();
  let formData = new FormData();
  for (var file of this.files) {
    formData.append('file' , file, file.name); 
  }
  formData.append('searchId', form.value.searchId);       
  formData.append('extension', form.value.extension);
  formData.append('text', form.value.text);
    
  
  this.appService.searchService(formData).
    subscribe(
      (response => {
        this.spinner.hide();
        console.log("ok"+response);
        alert("Alert   " +response);
        this.toastr.success('File search done Successfully')
        }),
      (error) => {
        console.log("ko"+error); 
        alert("Alert   " +error);
        this.spinner.hide(); 
        this.toastr.error('Error on file search');
      }
    );

    form.reset();
}

}
