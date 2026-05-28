#!/usr/bin/env python3
"""Generate WeChat miniprogram tabBar icons (81x81 PNG)."""

from PIL import Image, ImageDraw

SIZE = 81
GRAY = "#9B8E84"
CORAL = "#E8725A"
BG = "#FFFFFF"


def draw_home(draw, color):
    # House shape
    draw.polygon([(40, 18), (18, 36), (24, 36), (24, 56), (34, 56), (34, 44), (46, 44), (46, 56), (56, 56), (56, 36), (62, 36)], fill=color)


def draw_discover(draw, color):
    # Compass/compass rose
    cx, cy = 40, 40
    r = 18
    draw.ellipse([cx-r, cy-r, cx+r, cy+r], outline=color, width=3)
    draw.polygon([(cx, cy-14), (cx-5, cy+8), (cx, cy+4), (cx+5, cy+8)], fill=color)
    draw.polygon([(cx, cy+14), (cx-5, cy-8), (cx, cy-4), (cx+5, cy-8)], fill=color)


def draw_create(draw, color):
    # Plus in a circle
    cx, cy = 40, 40
    r = 18
    draw.ellipse([cx-r, cy-r, cx+r, cy+r], outline=color, width=3)
    draw.line([(cx, cy-10), (cx, cy+10)], fill=color, width=3)
    draw.line([(cx-10, cy), (cx+10, cy)], fill=color, width=3)


def draw_achievement(draw, color):
    # Trophy / shield
    cx, cy = 40, 40
    # Shield body
    draw.pieslice([cx-14, cy-18, cx+14, cy+10], 0, 180, fill=color)
    draw.pieslice([cx-14, cy-18, cx+14, cy+10], 180, 360, fill=color)
    draw.rectangle([cx-14, cy-4, cx+14, cy+6], fill=color)
    draw.pieslice([cx-10, cy+2, cx+10, cy+22], 0, 180, fill=color)
    # Star
    star_pts = [(cx, cy-6), (cx+3, cy+1), (cx+8, cy+1), (cx+4, cy+4), (cx+6, cy+9), (cx, cy+6), (cx-6, cy+9), (cx-4, cy+4), (cx-8, cy+1), (cx-3, cy+1)]
    draw.polygon(star_pts, fill=BG)


def draw_profile(draw, color):
    # Person silhouette
    cx, cy = 40, 40
    # Head
    draw.ellipse([cx-8, cy-16, cx+8, cy], fill=color)
    # Shoulders
    draw.pieslice([cx-20, cy+2, cx+20, cy+30], 0, 180, fill=color)


def generate(name, draw_func, color, out_path):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw_func(draw, color)
    img.save(out_path, "PNG")
    print(f"  -> {out_path}")


if __name__ == "__main__":
    out_dir = "/Users/sunlin/Documents/workspace/LifeSaga/miniprogram/images"
    icons = [
        ("home", draw_home),
        ("discover", draw_discover),
        ("create", draw_create),
        ("achievement", draw_achievement),
        ("profile", draw_profile),
    ]
    print("Generating tabBar icons...")
    for name, draw_func in icons:
        generate(name, draw_func, GRAY, f"{out_dir}/tab-{name}.png")
        generate(name, draw_func, CORAL, f"{out_dir}/tab-{name}-active.png")
    print("Done.")
