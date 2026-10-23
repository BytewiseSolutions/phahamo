import { test as base, Page, expect } from '@playwright/test';

export const E2E_ADMIN = {
  email: 'monamane.lebohang45@gmail.com',
  password: 'password',
};

/**
 * Logs in through the real UI. Reuse this whenever a test needs
 * an authenticated session, rather than repeating the steps inline.
 */
async function loginAs(page: Page, email: string, password: string) {
  await page.goto('/login');

  await page.locator('#email').fill(email);
  await page.locator('#password').fill(password);

  // The submit button is <button type="submit"> — there is also a
  // <button type="button"> "Sign In" tab on the same page, so be specific.
  await page.locator('button[type="submit"]').click();
}

export const test = base.extend<{ loggedInPage: Page }>({
  loggedInPage: async ({ page }, use) => {
    await loginAs(page, E2E_ADMIN.email, E2E_ADMIN.password);

    // Fail fast if login didn't actually succeed
    await expect(page).not.toHaveURL(/\/login/);

    const token = await page.evaluate(() => localStorage.getItem('sms_token'));
    expect(token, 'sms_token should be set after login').toBeTruthy();

    await use(page);
  },
});

export { expect } from '@playwright/test';
