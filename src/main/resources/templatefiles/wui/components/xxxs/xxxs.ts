import { Dialog } from '@angular/cdk/dialog';
import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { MyGridComponent, CrudParams, FieldConfig, CustomAction, ConfirmationDialogComponent, FilterConfig} from 'my-angular-commons2';


@Component({
  selector: 'app-xxxs',
  imports: [MyGridComponent, MatFormFieldModule, MatInputModule, ReactiveFormsModule,
    MatButtonModule
  ],
  templateUrl: './xxxs.html',
  styleUrl: './xxxs.css'
})
export class Xxxs {
  gridParams: CrudParams = new CrudParams();

  @ViewChild(MyGridComponent) grid!: MyGridComponent;

  constructor(private router: Router
  ) {
  }

  filters = new UntypedFormGroup({
    text: new UntypedFormControl()
  });

  ngOnInit() {
    this.gridParams.baseUrl = "http://localhost:8081";
    this.gridParams.resourceName = "xxxs";
    
    
    let idCol = new FieldConfig();
    idCol.name = 'id';
    idCol.displayText = 'شناسه';
    this.gridParams.fieldConfigs.push(idCol);

    let textCol = new FieldConfig();
    textCol.name = 'name';
    textCol.displayText = 'نام';
    this.gridParams.fieldConfigs.push(textCol);

    let createAction = new CustomAction();
    createAction.title = "ایجاد xxxFa جدید";
    createAction.onClick = () => this.router.navigate(["xxx-details"]);
    this.gridParams.customGeneralActions.push(createAction);

    let showDetailsAction = new CustomAction();
    showDetailsAction.title = 'جزئیات';
    showDetailsAction.onClick = (xxx: any) => this.router.navigate(['xxx-details', xxx.id]);
    this.gridParams.customRecordActions.push(showDetailsAction);
    
    this.makeFilterConfig();
  }

  private makeFilterConfig() {
    let textFilter: FilterConfig = new FilterConfig();
    textFilter.name = "name";
    textFilter.label = "نام";
    this.gridParams.filterConfigs.push(textFilter);

  }

}
