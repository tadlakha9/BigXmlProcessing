import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { splitAtColon } from '@angular/compiler/src/util';
import { toBase64String } from '@angular/compiler/src/output/source_map';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {
  xmlFilePath:File;
  constructor(private appService:AppService, private toastr:ToastrService) { }
  

  ngOnInit() {
  }
  submitform(form:NgForm){
    console.log(form.value);
    let formData = new FormData();
    formData.append('feedbacktype', form.value.feedbacktype);       
    formData.append('desfeedback', form.value.desfeedback);
    formData.append('name', form.value.name);
    formData.append('email', form.value.email);
    formData.append('projectname', form.value.projectname);
    this.appService.feedbackService(formData).
    subscribe(
      (response => {
        console.log("ok"+response);
        this.toastr.success('Feedback sent Successfully')
        }),
      (error) => {
        console.log("ko"+error);  
        this.toastr.error('Error on feedback');
      }
    );

    form.reset();
    console.log('inside submit button');
  }


}


