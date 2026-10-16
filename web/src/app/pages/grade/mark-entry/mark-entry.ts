import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Assessment } from '../assessment';
import { Mark } from '../mark';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-mark-entry',
  standalone: false,
  templateUrl: './mark-entry.html',
  styleUrl: './mark-entry.scss'
})
export class MarkEntry implements OnInit {

  assessmentId!: number;
  assessment: Assessment | null = null;
  marks: Mark[] = [];

  loading = false;
  saving = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backend: BackendService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/grades']);
      return;
    }
    this.assessmentId = Number(id);
    this.loadAssessment();
    this.loadMarks();
  }

  loadAssessment(): void {
    this.backend.get<Assessment>(`/assessments/${this.assessmentId}`).subscribe({
      next: (a) => this.assessment = a,
      error: (err) => this.errorMessage = err.error?.message || 'Failed to load assessment.'
    });
  }

  loadMarks(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Mark[]>(`/assessments/${this.assessmentId}/marks`).subscribe({
      next: (list) => {
        this.marks = list;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load marks.';
        this.loading = false;
      }
    });
  }

  onScoreInput(mark: Mark, raw: string): void {
    if (raw === '' || raw === null) {
      mark.score = null;
      return;
    }
    const parsed = Number(raw);
    if (isNaN(parsed)) {
      mark.score = null;
      return;
    }
    mark.score = parsed;
  }

  onSave(): void {
    if (!this.assessment) return;

    const rows = this.marks.map(m => ({
      studentId: m.studentId,
      score: m.score
    }));

    this.saving = true;
    this.errorMessage = '';

    this.backend.putSilently<Mark[]>(`/assessments/${this.assessmentId}/marks`, rows).subscribe({
      next: (list) => {
        this.marks = list;
        this.saving = false;
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to save marks.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/grades']);
  }

  isInvalid(mark: Mark): boolean {
    if (mark.score === null || !this.assessment) return false;
    return mark.score < 0 || mark.score > this.assessment.maxMark;
  }
}
