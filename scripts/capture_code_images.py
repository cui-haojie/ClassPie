# -*- coding: utf-8 -*-
"""Render real source code snippets as PNG for thesis document."""
import os
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "doc_assets" / "screenshots"


def _font(size=13):
    for p in ["C:/Windows/Fonts/consola.ttf", "C:/Windows/Fonts/msyh.ttc"]:
        if os.path.exists(p):
            try:
                return ImageFont.truetype(p, size)
            except Exception:
                pass
    return ImageFont.load_default()


def _title_font(size=14):
    for p in ["C:/Windows/Fonts/msyh.ttc", "C:/Windows/Fonts/simhei.ttf"]:
        if os.path.exists(p):
            try:
                return ImageFont.truetype(p, size)
            except Exception:
                pass
    return ImageFont.load_default()


def render_code_file(rel_path, title, out_name, start_line=1, max_lines=22):
    src = ROOT / rel_path
    if not src.exists():
        # try classPai frontend
        src = ROOT.parent / "classPai" / rel_path
    lines = src.read_text(encoding="utf-8", errors="ignore").splitlines()
    snippet = lines[start_line - 1 : start_line - 1 + max_lines]
    body = "\n".join(f"{start_line + i:4d} | {line}" for i, line in enumerate(snippet))
    font = _font(12)
    title_font = _title_font(14)
    h = 48 + len(snippet) * 20
    img = Image.new("RGB", (860, h), (28, 30, 33))
    draw = ImageDraw.Draw(img)
    draw.text((16, 12), f"{title}  ({rel_path})", fill=(120, 190, 255), font=title_font)
    y = 44
    for line in snippet:
        draw.text((16, y), f"{start_line:4d} | {line[:100]}", fill=(220, 220, 220), font=font)
        start_line += 1
        y += 20
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    out = OUT_DIR / out_name
    img.save(out, "PNG")
    return out


def capture_code_snippets():
    items = [
        ("src/main/java/org/example/classpiserver/security/JwtService.java", "JwtService — Token 生成", "code_jwt.png", 1, 25),
        ("src/main/java/org/example/classpiserver/service/attendance/AttendanceServiceImpl.java", "结束签到并关闭互动", "code_close_interaction.png", 175, 22),
        ("src/main/java/org/example/classpiserver/service/interaction/InteractionServiceImpl.java", "课堂互动 — 发布互动", "code_interaction.png", 62, 22),
        ("src/main/java/org/example/classpiserver/controller/course/CourseController.java", "CourseController 接口", "code_course.png", 1, 25),
        ("../classPai/src/utils/liveSocket.js", "WebSocket 实时连接", "code_websocket.png", 1, 20),
        ("../classPai/src/views/InteractionContent.vue", "互动页 — 实时订阅", "code_interaction_vue.png", 200, 22),
    ]
    paths = {}
    for rel, title, name, start, n in items:
        try:
            paths[name.replace(".png", "")] = render_code_file(rel, title, name, start, n)
            print(f"  code {name}")
        except Exception as e:
            print(f"  skip {name}: {e}")
    return paths


if __name__ == "__main__":
    capture_code_snippets()
