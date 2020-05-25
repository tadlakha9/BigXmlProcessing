import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AppService } from '../app.service';
import { ToastrService } from 'ngx-toastr';



@Component({
  selector: 'app-split',
  templateUrl: './split.component.html',
  styleUrls: ['./split.component.css']
})
export class SplitComponent implements OnInit {

  filePath:File;
  Catfile:File;
  typeOfSplit:string;
  splitType = ['line', 'size'];
  splitByRadio='';
  fileType:String;
  progress: number = 0;
  display = true;
 
  constructor(private appService:AppService, private toastr:ToastrService
    ) { }

  
  ngOnInit() {
  }

  isSelected(valueSelected){
    if(this.splitByRadio===valueSelected){
      return true;
    }else{
      return false;
    }

  }
  onSelectedXMLFile(event){
    console.log(event.target.files);
    this.filePath = event.target.files[0];
  }

  onSelectedCatalogFile(event){
    console.log(event.target.files);
    this.Catfile = event.target.files[0];
  }
  save(form:NgForm){
    this.display = true;
    console.log(form.value);
    let formData = new FormData();
    if(this.filePath == undefined){
      this.filePath = new File([""], "test.txt");
    }
    formData.append('file', this.filePath, this.filePath.name); 
    formData.append('typeOfSplit', form.value.typeOfSplit);       
    formData.append('level', form.value.level);
    formData.append('size', form.value.size);
    formData.append('splitByElement', form.value.splitByElement);
    formData.append('splitType', form.value.splitType);
    formData.append('splitByLine', form.value.splitByLine);
    formData.append('splitBySize', form.value.splitBySize);
    formData.append('fileType', form.value.fileType);
    if(this.Catfile == undefined){
      this.Catfile = new File([""], "catalog.txt");
    }
    formData.append('filecat', this.Catfile, this.Catfile.name);
    
    this.appService.splitService(formData).
    subscribe((response) => {      
          console.log('Ok', response);          
          alert("Alert   " +response);
        this.toastr.success('Split Successfully')
        this.progress = 100;
        setTimeout(() => {          
          this.display = false;
        }, 1100);
        
      
    },(error) => {
      console.log("ko"+error);  
      this.toastr.error('Error on Splitting');
    }

    );
    this.updatingProgressBar();
    

    form.reset();
  }

  updatingProgressBar(){
    console.log(this.progress);
    setTimeout(() => {
      this.progress = 1;
    }, 1000);
    setTimeout(() => {
      this.progress = 4;
    }, 1500);
    setTimeout(() => {
      this.progress = 11;
    }, 2000);
    setTimeout(() => {
      this.progress = 23;
    }, 7000);
    setTimeout(() => {
      this.progress = 29;
    }, 10000);
    setTimeout(() => {
      this.progress = 36;
    }, 15000);
    setTimeout(() => {
      this.progress = 46;
    }, 20000);
    setTimeout(() => {
      this.progress = 52;
    }, 30000);
    setTimeout(() => {
      this.progress = 62;
    }, 40000);
    setTimeout(() => {
      this.progress = 70;
    }, 50000);
    setTimeout(() => {
      this.progress = 90;
    }, 70000);
  }

  


}
