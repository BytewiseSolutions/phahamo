import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../util/backend.service';

interface LegalDocument { documentType: 'TERMS' | 'PRIVACY'; version: string; effectiveAt: string; }

@Component({
  selector: 'app-legal-documents-page',
  standalone: false,
  templateUrl: './legal-documents-page.html',
  styleUrl: './legal-documents-page.scss'
})
export class LegalDocumentsPage implements OnInit {
  documents: LegalDocument[] = [];
  documentTypes: Array<'TERMS' | 'PRIVACY'> = ['TERMS', 'PRIVACY'];
  version: Record<'TERMS' | 'PRIVACY', string> = { TERMS: '', PRIVACY: '' };
  files: Partial<Record<'TERMS' | 'PRIVACY', File>> = {};
  loading = false;
  message = '';
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.backend.get<LegalDocument[]>('/legal/admin/documents').subscribe({
      next: docs => this.documents = docs,
      error: () => this.errorMessage = 'Unable to load legal document history.'
    });
  }

  selectFile(type: 'TERMS' | 'PRIVACY', event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) this.files[type] = input.files[0];
  }

  publish(type: 'TERMS' | 'PRIVACY'): void {
    const file = this.files[type];
    if (!file || !this.version[type].trim()) {
      this.errorMessage = 'Choose a text/Markdown file and enter its version.';
      return;
    }
    const form = new FormData();
    form.append('type', type);
    form.append('version', this.version[type].trim());
    form.append('file', file, file.name);
    this.loading = true;
    this.errorMessage = '';
    this.backend.upload<LegalDocument>('/legal/admin/documents', form).subscribe({
      next: () => { this.loading = false; this.message = `${type === 'TERMS' ? 'Terms' : 'Privacy Policy'} published.`; this.load(); },
      error: err => { this.loading = false; this.errorMessage = err.error?.message || 'Document could not be published.'; }
    });
  }
}
