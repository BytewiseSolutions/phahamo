import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { ImportResult } from '../import-result';
import { ImportType } from '../import-type';

@Component({
  selector: 'app-bulk-import',
  standalone: false,
  templateUrl: './bulk-import.html',
  styleUrl: './bulk-import.scss'
})
export class BulkImport {

  ImportType = ImportType;

  importType: ImportType = ImportType.Students;

  selectedFile: File | null = null;
  selectedFileName = '';

  previewing = false;
  importing = false;

  preview: ImportResult | null = null;
  finalResult: ImportResult | null = null;

  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private router: Router
  ) {}

  setType(type: ImportType): void {
    if (this.importing || this.previewing) return;
    this.importType = type;
    this.reset();
  }

  private get typePath(): string {
    return this.importType === ImportType.Students ? 'students' : 'teachers';
  }

  goToUsers(): void {
    this.router.navigate(['/users']);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    if (!file) {
      this.selectedFile = null;
      this.selectedFileName = '';
      return;
    }

    if (!file.name.toLowerCase().endsWith('.xlsx')) {
      this.errorMessage = 'Only .xlsx files are supported. Please download the template and save as Excel Workbook.';
      this.selectedFile = null;
      this.selectedFileName = '';
      input.value = '';
      return;
    }

    this.selectedFile = file;
    this.selectedFileName = file.name;
    this.preview = null;
    this.finalResult = null;
    this.errorMessage = '';
  }

  downloadTemplate(): void {
    this.backend.downloadBlob(`/import/templates/${this.typePath}`).subscribe({
      next: (blob) => {
        const link = document.createElement('a');
        const objectUrl = URL.createObjectURL(blob);
        link.href = objectUrl;
        link.download = `${this.typePath}-template.xlsx`;
        link.click();
        URL.revokeObjectURL(objectUrl);
      },
      error: () => (this.errorMessage = 'Failed to download template')
    });
  }

  previewImport(): void {
    if (!this.selectedFile) return;

    this.previewing = true;
    this.errorMessage = '';
    this.finalResult = null;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.backend.upload<ImportResult>(
      `/import/${this.typePath}?dryRun=true`,
      formData
    ).subscribe({
      next: (result) => {
        this.preview = result;
        this.previewing = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to parse file';
        this.previewing = false;
      }
    });
  }

  async confirmImport(): Promise<void> {
    if (!this.selectedFile || !this.preview) return;

    const validCount = this.preview.summary.validCount;
    if (validCount === 0) {
      this.errorMessage = 'No valid rows to import. Fix the issues and re-upload.';
      return;
    }

    const confirmed = await this.confirmDialog.confirm({
      title: 'Confirm Import',
      message: `Import ${validCount} ${this.typePath} now?\n\nEach will get a login account and a welcome email with their temporary password.`,
      confirmText: `Import ${validCount}`,
      danger: false
    });

    if (!confirmed) return;

    this.importing = true;
    this.errorMessage = '';

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.backend.upload<ImportResult>(
      `/import/${this.typePath}?dryRun=false`,
      formData
    ).subscribe({
      next: (result) => {
        this.finalResult = result;
        this.preview = null;
        this.importing = false;

        setTimeout(() => this.router.navigate(['/users']), 1500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Import failed';
        this.importing = false;
      }
    });
  }

  reset(): void {
    this.selectedFile = null;
    this.selectedFileName = '';
    this.preview = null;
    this.finalResult = null;
    this.errorMessage = '';
  }
}
