# -*- coding: utf-8 -*-
"""Capture real ClassPi UI screenshots via Playwright."""
import json
import time
from pathlib import Path

from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "doc_assets" / "screenshots"
BASE = "http://localhost:5174"

TEACHER = {"account": "2782314722@qq.com", "password": "cuihaojie123."}
STUDENT = {"account": "12423020124@stu.cqut.edu.cn", "password": "cuihaojie123."}


def shot(page, name, url=None, wait_ms=1500, full_page=False):
    if url:
        page.goto(url, wait_until="networkidle", timeout=60000)
    page.wait_for_timeout(wait_ms)
    path = OUT_DIR / f"{name}.png"
    page.screenshot(path=str(path), full_page=full_page)
    print(f"  saved {path.name}")
    return path


def login(page, account, password):
    page.goto(f"{BASE}/", wait_until="networkidle")
    page.wait_for_timeout(800)
    acc = page.locator("#input_acc")
    acc.click()
    acc.fill(account)
    pwd = page.locator("#password")
    pwd.click()
    pwd.fill(password)
    page.click("button.login")
    page.wait_for_url("**/mainClass**", timeout=30000)
    page.wait_for_timeout(1200)


def logout_via_storage(page):
    page.evaluate("""() => {
        localStorage.clear();
        sessionStorage.clear();
    }""")


def pick_course_id(page):
    """Try to read a course id from localStorage pinia or page."""
    data = page.evaluate("""() => {
        try {
            const raw = localStorage.getItem('account');
            return raw;
        } catch(e) { return null; }
    }""")
    return data


def capture_all():
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    paths = {}

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1440, "height": 900}, device_scale_factor=1)
        page = context.new_page()

        # Public pages
        print("Public pages...")
        paths["ui_login"] = shot(page, "ui_login", f"{BASE}/")
        paths["ui_register"] = shot(page, "ui_register", f"{BASE}/register")

        # Teacher flow
        print("Teacher flow...")
        login(page, TEACHER["account"], TEACHER["password"])
        paths["ui_main_teacher"] = shot(page, "ui_main_teacher", f"{BASE}/mainClass", 2000)

        # Navigate course content
        cid = 346
        for try_id in [346, 348, 234, 123]:
            page.goto(f"{BASE}/courseContent?id={try_id}", wait_until="networkidle")
            page.wait_for_timeout(2000)
            body = page.locator("body").inner_text()
            if len(body) > 200 and "课程" in body:
                cid = try_id
                paths["ui_course"] = shot(page, "ui_course_teacher", wait_ms=500)
                break

        # Interaction tab
        if "ui_course" in paths:
            try:
                tab = page.get_by_text("课程互动", exact=False).first
                if tab.is_visible():
                    tab.click()
                    page.wait_for_timeout(1000)
            except Exception:
                pass
            paths["ui_course_interaction"] = shot(page, "ui_course", wait_ms=800)

        # Live class
        paths["ui_live"] = shot(page, "ui_live", f"{BASE}/liveClass?id={cid}", 2000)

        # Prep area
        paths["ui_prep"] = shot(page, "ui_prep", f"{BASE}/prepArea", 2000)

        # User settings
        paths["ui_setting"] = shot(page, "ui_setting", f"{BASE}/userSetting", 2000)

        # Grades tab on course
        page.goto(f"{BASE}/courseContent?id={cid}", wait_until="networkidle")
        page.wait_for_timeout(1500)
        try:
            g = page.get_by_text("成绩册", exact=False).first
            if g.is_visible():
                g.click()
                page.wait_for_timeout(1200)
                paths["ui_grade"] = shot(page, "ui_grade", wait_ms=500)
        except Exception:
            pass

        # Homework - click first homework if visible
        page.goto(f"{BASE}/courseContent?id={cid}", wait_until="networkidle")
        page.wait_for_timeout(1500)
        try:
            hw = page.locator(".homework, .box").filter(has_text="作业").first
            if not hw.count():
                hw = page.get_by_text("作业", exact=False).first
            # stay on course page homework section
            paths["ui_homework_list"] = shot(page, "ui_homework", wait_ms=500)
        except Exception:
            paths["ui_homework_list"] = shot(page, "ui_homework", wait_ms=500)

        # Try interaction detail page
        try:
            page.goto(f"{BASE}/courseContent?id={cid}", wait_until="networkidle")
            page.wait_for_timeout(1500)
            tab = page.get_by_text("课程互动", exact=False).first
            if tab.is_visible():
                tab.click()
                page.wait_for_timeout(800)
            item = page.locator(".activity-box, .box").first
            if item.is_visible():
                item.click()
                page.wait_for_timeout(2000)
                if "interactionContent" in page.url:
                    paths["ui_interaction"] = shot(page, "ui_interaction", wait_ms=500)
        except Exception:
            pass

        # Logout and student
        print("Student flow...")
        context.clear_cookies()
        logout_via_storage(page)
        page.goto(f"{BASE}/", wait_until="networkidle")
        login(page, STUDENT["account"], STUDENT["password"])
        paths["ui_main_student"] = shot(page, "ui_main_student", f"{BASE}/mainClass", 2000)

        page.goto(f"{BASE}/courseContent?id={cid}", wait_until="networkidle")
        page.wait_for_timeout(2000)
        paths["ui_course_student"] = shot(page, "ui_course_student", wait_ms=500)

        browser.close()

    manifest = OUT_DIR / "manifest.json"
    manifest.write_text(json.dumps({k: str(v) for k, v in paths.items()}, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Done. {len(paths)} screenshots -> {OUT_DIR}")
    return paths


if __name__ == "__main__":
    capture_all()
