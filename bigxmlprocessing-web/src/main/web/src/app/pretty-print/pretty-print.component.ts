import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from "@angular/router";
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-pretty-print',
  templateUrl: './pretty-print.component.html',
  styleUrls: ['./pretty-print.component.css']
})
export class PrettyPrintComponent implements OnInit {

  title = 'BigXMLProcessing';

  inputfile:File;

  constructor( private appService:AppService, private router:Router, private toastr:ToastrService) { }

  ngOnInit() {
 
  }

  onSelectInputFile(event) {
  console.log(event.target.files);
  this.inputfile =event.target.files[0];
  }

  // onXMLSelect(event) {
  //   console.log("XML Selected");
  // }
  // onSGMLSelect(event){
  //   console.log("SGML Selected");
  //}

save(form:NgForm)
{
  console.log("Documented will be pretty Printed ");
  console.log(form.value);
  let formData = new FormData();
  formData.append('file', this.inputfile, this.inputfile.name);
  formData.append('fileType', form.value.fileType);
  this.appService.prettyPrintService(formData).
  subscribe((response) => {
    console.log("ok"+response);
    this.toastr.success('Pretty Printed Successfully')
    },
      (error) => { console.log("ko"+error);  
      this.toastr.error('Error in Pretty Print');  
  });
   this.router.navigate(["/feedback"]);
   alert("Please give your valuable feedback!")
  form.reset();
 
}

}
