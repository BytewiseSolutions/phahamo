import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { ClassSummary } from '../class-summary';
import { TermSummary } from '../term-summary';
import { EmailReportCardResult } from '../email-report-card-result';
import { ParentWithoutEmail } from '../parent-without-email';

@Component({
  selector: 'app-email-report-cards',
  standalone: false,
  templateUrl: './email-report-cards.html',
  styleUrl: './email-report-cards.scss'
})
export class EmailReportCards implements OnInit {

  classes: ClassSummary[] = [];
  terms: TermSummary[] = [];

  selectedClassId: number | null = null;
  selectedTermId: number | null = null;

  parentsWithoutEmailCount = 0;

  sending = false;
  loadingPreview = false;

  lastResult: EmailReportCardResult | null = null;
  parentsWithoutEmail: ParentWithoutEmail[] = [];

  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadClasses();
    this.loadTerms();
  }

  loadClasses(): void {
    this.backend.get<any>('/classes?size=100').subscribe({
      next: (res) => {
        this.classes = (res.content ?? res) as ClassSummary[];
      },
      error: () => (this.errorMessage = 'Failed to load classes')
    });
  }

  loadTerms(): void {
    this.backend.get<TermSummary[]>('/terms').subscribe({
      next: (terms) => {
        this.terms = terms;
        this.selectedTermId = terms[0]?.id ?? null;
      },
      error: () => (this.errorMessage = 'Failed to load terms')
    });
  }

  onSelectionChange(): void {
    this.lastResult = null;
    this.parentsWithoutEmail = [];
    this.parentsWithoutEmailCount = 0;
    this.loadPreview();
  }

  loadPreview(): void {
    if (!this.selectedClassId) return;
    this.loadingPreview = true;

    this.backend.get<ParentWithoutEmail[]>(
      `/report-cards/email/class/${this.selectedClassId}/parents-without-email`
    ).subscribe({
      next: (list) => {
        this.parentsWithoutEmail = list;
        this.parentsWithoutEmailCount = list.length;
        this.loadingPreview = false;
      },
      error: () => {
        this.parentsWithoutEmail = [];
        this.parentsWithoutEmailCount = 0;
        this.loadingPreview = false;
      }
    });
  }

  async sendToClass(): Promise<void> {
    if (!this.selectedClassId || !this.selectedTermId) return;

    const className = this.classes.find(c => c.id === this.selectedClassId)?.name ?? 'this class';
    const termName = this.terms.find(t => t.id === this.selectedTermId)?.name ?? 'this term';

    const confirmed = await this.confirmDialog.confirm({
      title: 'Email Report Cards',
      message: `Send report cards for ${className} (${termName}) to all parents with an email address?\n\nParents without email will be reported so you can print manually.`,
      confirmText: 'Send Emails',
      cancelText: 'Cancel',
      danger: false
    });

    if (!confirmed) return;

    this.sending = true;
    this.errorMessage = '';
    this.lastResult = null;

    this.backend.post<EmailReportCardResult>(
      `/report-cards/email/class/${this.selectedClassId}?termId=${this.selectedTermId}`,
      {}
    ).subscribe({
      next: (result) => {
        this.lastResult = result;
        this.sending = false;
        this.loadPreview();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to send emails.';
        this.sending = false;
      }
    });
  }

  canSend(): boolean {
    return !!this.selectedClassId && !!this.selectedTermId && !this.sending;
  }
}
