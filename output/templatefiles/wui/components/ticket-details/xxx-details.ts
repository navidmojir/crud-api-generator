import { Component, OnInit } from '@angular/core';
import { PageMode } from '../enums/page-mode';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule, UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { TicketService } from '../services/ticket-service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from 'my-angular-commons2';
import { BrowserModule } from '@angular/platform-browser';

@Component({
  selector: 'app-ticket-details',
  imports: [ReactiveFormsModule, CommonModule, MatGridListModule, MatFormField, MatError,
    MatInputModule, MatButton, MatDialogModule
  ],
  templateUrl: './ticket-details.html',
  styleUrl: './ticket-details.css'
})
export class TicketDetails implements OnInit {
  PageMode = PageMode;
  pageMode: PageMode = PageMode.VIEW;
  id: number = 0;
  //gridCols = 4;
  //gridRowHeight = "3:1";
  //gridGutterSize = "30px";
  patternErrorMsg = 'کاراکتر غیرمجاز';

  ticket: any;

  constructor(private route: ActivatedRoute,
    private ticketService: TicketService,
    public location: Location,
    private router: Router,
    private dialog: MatDialog
  ){
  }

  form = new UntypedFormGroup({
    name: new UntypedFormControl()
  });

  ngOnInit(): void {
    let paramId = this.route.snapshot.paramMap.get("id");
    if(paramId == null)
      this.pageMode = PageMode.CREATE;
    else 
      this.loadEntity(paramId);
      
  }

  get name(): any {
    return this.form.get('name')
  }

  loadEntity(paramId: string): void {
    this.ticketService.retrieve(paramId).subscribe(
      (ticket: any)=> {
        this.form.patchValue(ticket);
        this.ticket = ticket;
        this.form.disable();
      }
    );
  }

  create() {
    this.ticketService.create(this.form.value).subscribe(
      (result: any) => {
        this.router.navigate(['ticket-details', result.id]);
      }
    );
  }

  save() {
    this.ticketService.update(this.ticket.id, this.form.value).subscribe(
      (result) => {
        this.ticket = result;
        this.pageMode = PageMode.VIEW;
        this.form.disable();
      }
    )
  }

  switchToEditMode() {
    this.form.enable();
    this.pageMode = PageMode.UPDATE;
  }

  cancelEditMode() {
    if(this.form.touched) {
      let dialogRef = this.dialog.open(ConfirmationDialogComponent);
      dialogRef.componentInstance.confirmMessage = "محتوای فرم تغییر کرده است. آیا از عدم ذخیره اطلاعات اطمینان دارید؟";
      dialogRef.afterClosed().subscribe((result) => {
        if(result)
          this.cancelEditModeLogic();
      })
    } else 
      this.cancelEditModeLogic();
    
    
  }

  private cancelEditModeLogic() {
    this.form.patchValue(this.ticket);
    this.form.markAsUntouched();
    this.form.disable();
    this.pageMode = PageMode.VIEW;
  }

  remove() {
    let dialogRef = this.dialog.open(ConfirmationDialogComponent);
    dialogRef.componentInstance.confirmMessage = 'آیا از حذف اطمینان دارید؟';
    dialogRef.afterClosed().subscribe(
      (result) => {
        if(result == true) {
           this.ticketService.remove(this.ticket.id).subscribe(
              () => this.backToList()
            );
        }
      }
    )
   
  }

  backToList() {
    this.router.navigate(['tickets'])
  }
}
