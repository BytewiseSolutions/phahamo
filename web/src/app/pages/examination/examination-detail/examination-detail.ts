import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Examination } from '../examination';

@Component({
  selector: 'app-examination-detail',
  standalone: false,
  templateUrl: './examination-detail.html',
  styleUrl: './examination-detail.scss'
})
export class ExaminationDetail implements OnInit {
  examination: Examination | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'Invalid examination.';
      return;
    }

    this.loading = true;
    this.backend.get<Examination>(`/examinations/${id}`).subscribe({
      next: (examination) => {
        this.examination = examination;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load examination details.';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  formatDate(value: string): string {
    return new Date(value).toLocaleDateString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric', timeZone: 'UTC'
    });
  }
}
