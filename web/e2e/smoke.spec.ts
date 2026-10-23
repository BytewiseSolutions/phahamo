import { test, expect } from '@playwright/test';

test('app loads and renders the Angular root', async ({ page }) => {
  await page.goto('/');

  const root = page.locator('app-root');
  await expect(root).toBeVisible({ timeout: 15_000 });

  await expect(page).not.toHaveTitle(/^Web$/);

  const body = await page.locator('body').innerText();
  expect(body).not.toContain('Cannot GET');
});
