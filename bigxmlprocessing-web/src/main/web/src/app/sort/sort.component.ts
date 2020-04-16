import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { splitAtColon } from '@angular/compiler/src/util';
//import { Split } from '../model/split';
import { toBase64String } from '@angular/compiler/src/output/source_map';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.css']
})
export class SortComponent implements OnInit {
  xmlFilePath:File;
  //split:Split;
  constructor(private appService:AppService, private toastr:ToastrService) { }
  

  ngOnInit() {
  }
  submitform(form:NgForm){
    console.log(form.value);
    //this.split = form.value;
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
        this.toastr.success('Sort Successfully')
        }),
      (error) => {
        console.log("ko"+error);  
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

