import { test, expect, E2E_ADMIN } from './fixtures/auth';

const SYSTEM_ADMIN_LINKS = [
  { label: /dashboard/i,      path: '/dashboard' },
  { label: /^users$/i,        path: '/users' },
  { label: /^subscriptions$/i,path: '/subscriptions' },
  { label: /plan prices/i,    path: '/plan-prices' },
  { label: /payment methods/i,path: '/payment-methods' },
];

test.describe('SYSTEM_ADMIN sidebar navigation', () => {
  for (const { label, path } of SYSTEM_ADMIN_LINKS) {
    test(`"${label}" link navigates to ${path}`, async ({ loggedInPage: page }) => {
      const routerErrors: string[] = [];
      page.on('console', (msg) => {
        if (msg.type() === 'error' && /cannot match any routes/i.test(msg.text())) {
          routerErrors.push(msg.text());
        }
      });

      const link = page.getByRole('link', { name: label });

      await expect(link, `sidebar link "${label}" should be visible`).toBeVisible();

      await link.click();

      await expect(page, `URL after clicking "${label}"`).toHaveURL(new RegExp(path.replace('/', '\\/')), {
        timeout: 10_000,
      });

      expect(routerErrors, `router errors on ${path}`).toHaveLength(0);

      await expect(page.locator('app-root')).toBeVisible();
      await expect(page.locator('app-root')).not.toBeEmpty();
    });
  }
});
