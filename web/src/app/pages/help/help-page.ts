import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../util/backend.service';
import { AuthService } from '../../util/auth.service';

interface HelpGuide {
  title: string;
  steps: string[];
}

@Component({
  selector: 'app-help-page',
  standalone: false,
  templateUrl: './help-page.html',
  styleUrl: './help-page.scss'
})
export class HelpPage {
  readonly supportForm: FormGroup;
  guides: HelpGuide[] = [];
  supportMessage = '';
  supportError = '';
  sending = false;

  private readonly guideMap: Record<string, HelpGuide[]> = {
    SCHOOL_ADMIN: [
      { title: 'Set up your school', steps: ['Open School Profile and confirm the school contact details.', 'Upload the school logo and save the profile.', 'Create academic years, terms, classes and subjects before adding records.'] },
      { title: 'Manage users', steps: ['Open Users and select Add User.', 'Choose the person’s role and complete their details.', 'The user receives account instructions by email.'] },
      { title: 'Publish an announcement', steps: ['Open Announcements and create a new announcement.', 'Choose the audience and write the message.', 'Publish it to notify the selected users.'] }
    ],
    PRINCIPAL: [
      { title: 'Review school activity', steps: ['Use Dashboard for current school statistics.', 'Open Reports for academic, attendance and financial summaries.', 'Use Audit Log to review important changes.'] },
      { title: 'Publish an announcement', steps: ['Open Announcements and create a message.', 'Choose the audience, review the content, and publish it.'] }
    ],
    TEACHER: [
      { title: 'Take attendance', steps: ['Open My Classes and choose a class.', 'Open the lesson attendance screen.', 'Mark each learner present or absent and save.'] },
      { title: 'Enter marks', steps: ['Open Grades and select the assessment.', 'Enter marks for the learners in your class.', 'Save the marks and submit them when complete.'] }
    ],
    FINANCE_OFFICER: [
      { title: 'Record a payment', steps: ['Open Student Fees and find the learner account.', 'Open Payments and select Record Payment.', 'Enter the amount, method and reference, then save.'] },
      { title: 'Send a fee reminder', steps: ['Open Student Fees and choose Fee Reminders.', 'Review outstanding balances and select the recipients.', 'Send the reminder to parents with email addresses.'] }
    ],
    PARENT: [
      { title: 'View your child’s information', steps: ['Open My Children to select a child.', 'Use Attendance, Results and Fees to view the available information.'] },
      { title: 'Contact the school', steps: ['Open Messages from the sidebar.', 'Choose a recipient, write your message, and send it.'] }
    ],
    STUDENT: [
      { title: 'Check your timetable', steps: ['Open Timetable to see your published lessons.', 'Use My Attendance, My Results and My Fees to review your records.'] },
      { title: 'Contact a teacher', steps: ['Open Messages from the sidebar.', 'Choose a recipient, write your message, and send it.'] }
    ],
    SYSTEM_ADMIN: [
      { title: 'Manage schools', steps: ['Open School Subscriptions to review school accounts.', 'Use the school actions to inspect subscription status and access.'] },
      { title: 'Manage plans and payments', steps: ['Open Plan Prices to maintain pricing.', 'Open Payment Methods to manage available payment options.'] }
    ]
  };

  constructor(
    private fb: FormBuilder,
    private backend: BackendService,
    private auth: AuthService
  ) {
    this.supportForm = this.fb.group({
      subject: ['', [Validators.required, Validators.maxLength(120)]],
      question: ['', [Validators.required, Validators.maxLength(5000)]]
    });
    const roles = this.auth.getRoles();
    const seen = new Set<string>();
    for (const role of roles) {
      for (const guide of this.guideMap[role] ?? []) {
        if (!seen.has(guide.title)) {
          seen.add(guide.title);
          this.guides.push(guide);
        }
      }
    }
  }

  submitSupport(): void {
    this.supportMessage = '';
    this.supportError = '';
    if (this.supportForm.invalid) {
      this.supportForm.markAllAsTouched();
      return;
    }
    this.sending = true;
    this.backend.postSilently<void>('/support', this.supportForm.getRawValue()).subscribe({
      next: () => {
        this.sending = false;
        this.supportForm.reset();
        this.supportMessage = 'Your question was sent to Bytewise. A confirmation email is on its way.';
      },
      error: () => {
        this.sending = false;
        this.supportError = 'We could not send your question. Please try again.';
      }
    });
  }
}
