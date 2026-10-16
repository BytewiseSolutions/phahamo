import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { GradeBand, GradeResolveResult } from '../grade-band';

@Component({
  selector: 'app-grade-band-list',
  standalone: false,
  templateUrl: './grade-band-list.html',
  styleUrl: './grade-band-list.scss'
})
export class GradeBandList implements OnInit {

  bands: GradeBand[] = [];
  loading = false;
  errorMessage = '';
  saving = false;

  showForm = false;
  editingBand: GradeBand | null = null;
  editingIndex = -1;

  previewScore: number | null = null;
  previewResult: GradeResolveResult | null = null;
  previewError = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadBands();
  }

  loadBands(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<GradeBand[]>('/grade-bands').subscribe({
      next: (list) => {
        this.bands = this.sortDescending(list);
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load grading scale.';
        this.loading = false;
      }
    });
  }

  private sortDescending(list: GradeBand[]): GradeBand[] {
    return [...list].sort((a, b) => b.minScore - a.minScore);
  }

  openCreate(): void {
    this.editingBand = null;
    this.editingIndex = -1;
    this.showForm = true;
  }

  openEdit(band: GradeBand, index: number): void {
    this.editingBand = band;
    this.editingIndex = index;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingBand = null;
    this.editingIndex = -1;
  }

  onFormSave(band: GradeBand): void {
    const updated = [...this.bands];

    if (this.editingIndex >= 0) {
      updated[this.editingIndex] = band;
    } else {
      updated.push(band);
    }

    const sorted = this.sortDescending(updated);
    const error = this.validate(sorted);

    if (error) {
      this.errorMessage = error;
      return;
    }

    this.errorMessage = '';
    this.saving = true;

    this.backend.putSilently<GradeBand[]>('/grade-bands', sorted).subscribe({
      next: (saved) => {
        this.bands = this.sortDescending(saved);
        this.saving = false;
        this.closeForm();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to save grading scale.';
      }
    });
  }

  private validate(sorted: GradeBand[]): string | null {
    if (sorted.length === 0) return 'At least one grade band is required.';

    for (const b of sorted) {
      if (b.minScore == null || b.maxScore == null) {
        return 'Every band must have a min and max score.';
      }
      if (b.minScore < 0 || b.maxScore > 100) {
        return `Band "${b.grade || '?'}" is out of range (must be 0–100).`;
      }
      if (b.maxScore <= b.minScore) {
        return `Band "${b.grade || '?'}" has max ≤ min.`;
      }
      if (!b.grade.trim()) return 'Every band must have a grade.';
      if (!b.remark.trim()) return `Band "${b.grade}" must have a remark.`;
    }

    for (let i = 0; i < sorted.length; i++) {
      for (let j = i + 1; j < sorted.length; j++) {
        const a = sorted[i];
        const b = sorted[j];
        if (a.minScore <= b.maxScore && b.minScore <= a.maxScore) {
          return `Bands "${a.grade}" and "${b.grade}" overlap.`;
        }
      }
    }

    const seen = new Set<string>();
    for (const band of sorted) {
      const g = band.grade.trim().toUpperCase();
      if (seen.has(g)) return `Duplicate grade: ${band.grade}`;
      seen.add(g);
    }

    return null;
  }

  displayMin(minScore: number): string {
    return Number.isInteger(minScore) ? String(minScore) : minScore.toFixed(1);
  }

  displayMax(band: GradeBand): string {
    return Number.isInteger(band.maxScore) ? String(band.maxScore) : band.maxScore.toFixed(1);
  }

  onPreview(): void {
    if (this.previewScore === null || this.previewScore === undefined) return;

    this.previewError = '';
    this.previewResult = null;

    this.backend.get<GradeResolveResult>(`/grade-bands/resolve?score=${this.previewScore}`).subscribe({
      next: (res) => this.previewResult = res,
      error: (err) => this.previewError = err.error?.message || 'Failed to resolve score.'
    });
  }
}
