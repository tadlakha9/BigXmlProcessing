import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
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

  constructor( private appService:AppService, private toastr:ToastrService) { }

  ngOnInit() {
 
  }

  onSelectInputFile(event) {
  console.log(event.target.files);
  this.inputfile =event.target.files[0];
  }

  save(form:NgForm)
  {
    console.log("Documented will be pretty Printed ");
    console.log(form.value);
    let formData = new FormData();
    formData.append('file', this.inputfile, this.inputfile.name);
    
    //to be included fileType(SGM file) option as well in future 
    //formData.append('fileType', form.value.fileType);
  
    this.appService.prettyPrintService(formData).
    subscribe((response) => {
      console.log("ok"+response);
      this.toastr.success('Pretty Printed Successfully')
      },
      (error) => { console.log("ko"+error);  
      this.toastr.error('Error in Pretty Print');  
    });
   alert("Please give your valuable feedback!")
   form.reset();
  }

}
