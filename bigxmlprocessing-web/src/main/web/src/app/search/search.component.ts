import { Component, OnInit } from '@angular/core';
import {  NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';



@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  
  files:File[]=[];
  searchId:string;
  constructor(private appService:AppService, private toastr:ToastrService) { }

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
    subscribe(
      (response => {
        console.log("ok"+response);
        this.toastr.success('Searching Successfully')
        }),
      (error) => {
        console.log("ko"+error);  
        this.toastr.error('Error on Searchin');
      }
    );

    form.reset();
}

}
