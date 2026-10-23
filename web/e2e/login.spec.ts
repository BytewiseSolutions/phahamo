import { test, expect, E2E_ADMIN } from './fixtures/auth';

test.describe('Login journey (SYSTEM_ADMIN)', () => {
  test('redirects to /dashboard and shows admin sidebar links', async ({ page }) => {
    await page.goto('/login');

    await expect(page).toHaveURL(/\/login/);
    await expect(page.locator('#email')).toBeVisible();
    await expect(page.locator('#password')).toBeVisible();

    await page.locator('#email').fill(E2E_ADMIN.email);
    await page.locator('#password').fill(E2E_ADMIN.password);
    await page.locator('button[type="submit"]').click();

    await expect(page).toHaveURL(/\/dashboard/, { timeout: 15_000 });

    const token = await page.evaluate(() => localStorage.getItem('sms_token'));
    expect(token, 'sms_token should be present after login').toBeTruthy();

    await expect(page.getByRole('link', { name: /schools/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /subscriptions/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /plan prices/i })).toBeVisible();

    await expect(page.getByRole('link', { name: /^classes$/i })).toHaveCount(0);
  });

  test('rejects bad credentials with the expected error message', async ({ page }) => {
    await page.goto('/login');

    await page.locator('#email').fill(E2E_ADMIN.email);
    await page.locator('#password').fill('definitely-wrong');
    await page.locator('button[type="submit"]').click();

    await expect(page.locator('.error-banner'))
      .toContainText('Invalid email or password.', { timeout: 10_000 });

    await expect(page).toHaveURL(/\/login/);
    const token = await page.evaluate(() => localStorage.getItem('sms_token'));
    expect(token).toBeNull();
  });
});

test.describe('Authenticated-page fixture', () => {
  test('loggedInPage fixture reaches dashboard without re-doing login', async ({ loggedInPage }) => {
    await loggedInPage.goto('/dashboard');
    await expect(loggedInPage).toHaveURL(/\/dashboard/);

    const token = await loggedInPage.evaluate(() => localStorage.getItem('sms_token'));
    expect(token).toBeTruthy();
  });
});
