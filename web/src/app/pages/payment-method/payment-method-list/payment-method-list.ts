import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { PaymentMethod, PaymentMethodType } from '../../../util/payment-method';

@Component({
  selector: 'app-payment-method-list',
  standalone: false,
  templateUrl: './payment-method-list.html',
  styleUrl: './payment-method-list.scss'
})
export class PaymentMethodList implements OnInit {

  methods: PaymentMethod[] = [];
  loading = false;
  errorMessage = '';

  showForm = false;
  editingMethod: PaymentMethod | null = null;
  saving = false;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadMethods();
  }

  loadMethods(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<PaymentMethod[]>('/payment-methods').subscribe({
      next: (methods) => {
        this.methods = methods;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load payment methods.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editingMethod = null;
    this.showForm = true;
    this.errorMessage = '';
  }

  openEdit(method: PaymentMethod): void {
    this.editingMethod = method;
    this.showForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingMethod = null;
    this.saving = false;
  }

  onSave(event: {
    id: number | null;
    type: PaymentMethodType;
    label: string;
    details: string;
    active: boolean;
    sortOrder: number;
  }): void {
    this.saving = true;
    this.errorMessage = '';

    const payload = {
      type: event.type,
      label: event.label,
      details: event.details,
      active: event.active,
      sortOrder: event.sortOrder
    };

    if (event.id !== null) {
      this.backend.put<PaymentMethod>(`/payment-methods/${event.id}`, payload).subscribe({
        next: () => {
          this.saving = false;
          this.onFormClosed();
          this.loadMethods();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update payment method.';
        }
      });
    } else {
      this.backend.post<PaymentMethod>('/payment-methods', payload).subscribe({
        next: () => {
          this.saving = false;
          this.onFormClosed();
          this.loadMethods();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to create payment method.';
        }
      });
    }
  }

  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
