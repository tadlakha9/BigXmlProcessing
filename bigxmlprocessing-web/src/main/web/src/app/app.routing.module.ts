import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { ParsingComponent } from "./parsing/parsing.component";
import { SearchComponent } from "./search/search.component";
import { SortComponent } from "./sort/sort.component";
import { PrettyPrintComponent } from "./pretty-print/pretty-print.component";
import { SplitComponent } from "./split/split.component";
import { ConvertComponent } from "./convert/convert.component";
import { FeedbackComponent } from "./feedback/feedback.component";

const routes: Routes = [
    
    {
      path: '',
      redirectTo: '/search',
      pathMatch: 'full'
    },
    {
        path: 'search',
        component: SearchComponent,
        
      }
      ,
    {
        path: 'sort',
        component: SortComponent,
        
      }
      ,
    {
        path: 'prettyprint',
        component: PrettyPrintComponent,
        
      }
      ,
    {
        path: 'split',
        component: SplitComponent,
        
      }
      ,
    {
        path: 'convert',
        component: ConvertComponent,
        
      }
      ,
    {
        path: 'parsing',
        component: ParsingComponent,
        
      }
      ,
      {
          path: 'feedback',
          component: FeedbackComponent,
          
        }
  ];

  
@NgModule({
    imports :[
        RouterModule.forRoot(routes)
    ],
    exports: [RouterModule]
    
})
export class AppRoutingModule {}