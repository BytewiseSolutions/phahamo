import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../../util/backend.service';

interface LegalDocument {
  version: string;
  content: string;
  effectiveAt: string;
}

@Component({
  selector: 'app-legal-page',
  standalone: false,
  templateUrl: './legal-page.html',
  styleUrl: './legal-page.scss'
})
export class LegalPage implements OnInit {
  documentType: 'terms' | 'privacy' = 'terms';
  document: LegalDocument | null = null;
  loading = true;
  errorMessage = '';

  constructor(private route: ActivatedRoute, private backend: BackendService) {}

  ngOnInit(): void {
    this.documentType = this.route.snapshot.data['documentType'] === 'privacy' ? 'privacy' : 'terms';
    this.backend.get<LegalDocument>(`/legal/${this.documentType}`).subscribe({
      next: document => { this.document = document; this.loading = false; },
      error: () => { this.errorMessage = 'This legal document is not available yet.'; this.loading = false; }
    });
  }

  get isPrivacy(): boolean {
    return this.documentType === 'privacy';
  }
}
