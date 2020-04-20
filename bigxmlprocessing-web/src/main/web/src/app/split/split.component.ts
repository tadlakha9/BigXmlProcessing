import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { splitAtColon } from '@angular/compiler/src/util';

import { toBase64String } from '@angular/compiler/src/output/source_map';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-split',
  templateUrl: './split.component.html',
  styleUrls: ['./split.component.css']
})
export class SplitComponent implements OnInit {

  filePath:File;
  typeOfSplit:string;
 
  constructor(private appService:AppService, private toastr:ToastrService) { }

  
  ngOnInit() {
  }

  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.filePath = event.target.files[0];
  }

  save(form:NgForm){
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.filePath, this.filePath.name); 
    formData.append('typeOfSplit', form.value.typeOfSplit);       
    formData.append('level', form.value.level);
    formData.append('size', form.value.size);
    formData.append('splitByElement', form.value.splitByElement);
    this.appService.splitService(formData).
    subscribe(
      (response => {
        console.log("ok"+response);
        this.toastr.success('Split Successfully')
        }),
      (error) => {
        console.log("ko"+error);  
        this.toastr.error('Error on Splitting');
      }
    );

    form.reset();
    

  }


}
