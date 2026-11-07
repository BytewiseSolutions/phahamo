import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-legal-page',
  standalone: false,
  templateUrl: './legal-page.html',
  styleUrl: './legal-page.scss'
})
export class LegalPage implements OnInit {
  documentType: 'terms' | 'privacy' = 'terms';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.documentType = this.route.snapshot.data['documentType'] === 'privacy' ? 'privacy' : 'terms';
  }

  get isPrivacy(): boolean {
    return this.documentType === 'privacy';
  }
}
