import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { Term } from '../../terms/term';
import { AcademicYear } from '../../academic-years/academic-year';
import { FeeStructure } from '../fee-structure';
import { AnnualFee } from '../annual-fee';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

type Tab = 'termFees' | 'annualFees';

@Component({
  selector: 'app-fees-page',
  standalone: false,
  templateUrl: './fees-page.html',
  styleUrl: './fees-page.scss'
})
export class FeesPage implements OnInit {

  activeTab: Tab = 'termFees';

  classes: SchoolClass[] = [];
  terms: Term[] = [];
  academicYears: AcademicYear[] = [];

  selectedClassId: number | null = null;
  selectedTermId: number | null = null;
  selectedAcademicYearId: number | null = null;

  structures: FeeStructure[] = [];
  annualFees: AnnualFee[] = [];

  loading = false;
  errorMessage = '';

  showStructureForm = false;
  editingStructure: FeeStructure | null = null;

  showAnnualFeeForm = false;
  editingAnnualFee: AnnualFee | null = null;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadFilters();
  }

  loadFilters(): void {
    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;
        this.selectedClassId = null;

        this.backend.get<Term[]>('/terms').subscribe({
          next: (terms) => {
            this.terms = terms;
            this.selectedTermId = null;

            this.backend.get<AcademicYear[]>('/academic-years').subscribe({
              next: (years) => {
                this.academicYears = years;
                this.selectedAcademicYearId = years[0]?.id ?? null;
                this.loadForTab();
              },
              error: () => {
                this.errorMessage = 'Failed to load academic years.';
              }
            });
          },
          error: () => {
            this.errorMessage = 'Failed to load terms.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load classes.';
      }
    });
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
    this.loadForTab();
  }

  loadForTab(): void {
    if (this.activeTab === 'termFees') {
      this.loadStructures();
    } else {
      this.loadAnnualFees();
    }
  }

  loadStructures(): void {
    this.loading = true;
    this.errorMessage = '';

    const params: string[] = [];
    if (this.selectedClassId) params.push(`classId=${this.selectedClassId}`);
    if (this.selectedTermId) params.push(`termId=${this.selectedTermId}`);

    let url = '/fee-structures';
    if (params.length > 0) url += '?' + params.join('&');

    this.backend.get<FeeStructure[]>(url).subscribe({
      next: (rows) => {
        this.structures = rows.sort((a, b) =>
          a.className.localeCompare(b.className, undefined, { numeric: true })
        );
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load fee structures.';
        this.structures = [];
        this.loading = false;
      }
    });
  }

  loadAnnualFees(): void {
    this.loading = true;
    this.errorMessage = '';

    const params: string[] = [];
    if (this.selectedClassId) params.push(`classId=${this.selectedClassId}`);
    if (this.selectedAcademicYearId) params.push(`academicYearId=${this.selectedAcademicYearId}`);

    let url = '/annual-fees';
    if (params.length > 0) url += '?' + params.join('&');

    this.backend.get<AnnualFee[]>(url).subscribe({
      next: (rows) => {
        this.annualFees = rows.sort((a, b) => {
          const nameCompare = a.name.localeCompare(b.name);
          if (nameCompare !== 0) return nameCompare;
          return a.className.localeCompare(b.className, undefined, { numeric: true });
        });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load annual fees.';
        this.annualFees = [];
        this.loading = false;
      }
    });
  }

  openCreateStructure(): void {
    this.editingStructure = null;
    this.showStructureForm = true;
  }

  openEditStructure(structure: FeeStructure): void {
    this.editingStructure = structure;
    this.showStructureForm = true;
  }

  onStructureSaved(): void {
    this.showStructureForm = false;
    this.editingStructure = null;
    this.loadStructures();
  }

  onStructureFormClosed(): void {
    this.showStructureForm = false;
    this.editingStructure = null;
  }

  async deleteStructure(structure: FeeStructure): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Fee Structure',
      message: `Delete fee structure for ${structure.className} — ${structure.termName}?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) {
      return;
    }

    this.backend.delete<void>(`/fee-structures/${structure.id}`).subscribe({
      next: () => this.loadStructures(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete.';
      }
    });
  }

  openCreateAnnualFee(): void {
    this.editingAnnualFee = null;
    this.showAnnualFeeForm = true;
  }

  openEditAnnualFee(fee: AnnualFee): void {
    this.editingAnnualFee = fee;
    this.showAnnualFeeForm = true;
  }

  onAnnualFeeSaved(): void {
    this.showAnnualFeeForm = false;
    this.editingAnnualFee = null;
    this.loadAnnualFees();
  }

  onAnnualFeeFormClosed(): void {
    this.showAnnualFeeForm = false;
    this.editingAnnualFee = null;
  }

  async deleteAnnualFee(fee: AnnualFee): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Annual Fee',
      message: `Delete ${fee.name} for ${fee.className}?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.backend.delete<void>(`/annual-fees/${fee.id}`).subscribe({
      next: () => this.loadAnnualFees(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete.';
      }
    });
  }

  formatAmount(value: number | null | undefined): string {
    if (value == null) return '—';
    return `M ${value.toFixed(2)}`;
  }
}
