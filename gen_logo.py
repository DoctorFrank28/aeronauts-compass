"""Generate Aeronaut's Compass logo (128x128) and CurseForge thumbnail (480x270)."""
import struct, zlib, os, math

# ---------------------------------------------------------------------------
# PNG writer (no dependencies)
# ---------------------------------------------------------------------------
def write_png(pixels, width, height, path):
    def chunk(tag, data):
        buf = tag + data
        return struct.pack('>I', len(data)) + buf + struct.pack('>I', zlib.crc32(buf) & 0xffffffff)
    raw = b''
    for row in pixels:
        raw += b'\x00'
        for r, g, b in row:
            raw += bytes([r, g, b])
    sig = b'\x89PNG\r\n\x1a\n'
    ihdr = chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0))
    idat = chunk(b'IDAT', zlib.compress(raw, 9))
    iend = chunk(b'IEND', b'')
    os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
    with open(path, 'wb') as f:
        f.write(sig + ihdr + idat + iend)

def blend(c1, c2, t):
    t = max(0.0, min(1.0, t))
    return tuple(int(a + (b - a) * t) for a, b in zip(c1, c2))

def set_pixel(p, x, y, color, size):
    if 0 <= x < size and 0 <= y < len(p):
        p[y][x] = color

# ---------------------------------------------------------------------------
# Color palette (consistent with gen_textures.py + extras)
# ---------------------------------------------------------------------------
PA  = (232, 213, 163)   # parchment light
PD  = (205, 184, 135)   # parchment dark
DK  = (70,  45,  20)    # dark ink
RD  = (185, 52,  32)    # red N needle
RDL = (220, 85,  60)    # red highlight
ND_ = (108, 75,  40)    # S/E/W needle
NDL = (150, 110, 65)    # needle highlight
MD  = (155, 115, 65)    # diagonal / tick marks
GD  = (200, 162, 68)    # gold/brass
GDL = (235, 205, 110)   # gold highlight
BG  = (16,  20,  35)    # dark navy background
BGL = (28,  36,  58)    # navy lighter
WD  = (165, 125, 70)    # wood

# ---------------------------------------------------------------------------
# Pixel-art 4×7 letter bitmaps (N S E W A R O U T ')
# ---------------------------------------------------------------------------
GLYPHS = {
    'N': [[1,0,0,1],[1,1,0,1],[1,0,1,1],[1,0,0,1],[1,0,0,1]],
    'S': [[0,1,1,0],[1,0,0,0],[0,1,1,0],[0,0,0,1],[1,1,1,0]],
    'E': [[1,1,1,1],[1,0,0,0],[1,1,1,0],[1,0,0,0],[1,1,1,1]],
    'W': [[1,0,0,1],[1,0,0,1],[1,0,1,1],[1,1,0,1],[1,0,0,1]],
    'A': [[0,1,1,0],[1,0,0,1],[1,1,1,1],[1,0,0,1],[1,0,0,1]],
    'R': [[1,1,1,0],[1,0,0,1],[1,1,1,0],[1,0,1,0],[1,0,0,1]],
    'O': [[0,1,1,0],[1,0,0,1],[1,0,0,1],[1,0,0,1],[0,1,1,0]],
    'U': [[1,0,0,1],[1,0,0,1],[1,0,0,1],[1,0,0,1],[0,1,1,0]],
    'T': [[1,1,1,1],[0,1,1,0],[0,1,1,0],[0,1,1,0],[0,1,1,0]],
    'C': [[0,1,1,0],[1,0,0,0],[1,0,0,0],[1,0,0,0],[0,1,1,0]],
    'M': [[1,0,0,1],[1,1,1,1],[1,0,0,1],[1,0,0,1],[1,0,0,1]],
    'P': [[1,1,1,0],[1,0,0,1],[1,1,1,0],[1,0,0,0],[1,0,0,0]],
    'I': [[1,1,1],[0,1,0],[0,1,0],[0,1,0],[1,1,1]],
    'G': [[0,1,1,0],[1,0,0,0],[1,0,1,1],[1,0,0,1],[0,1,1,0]],
    'H': [[1,0,0,1],[1,0,0,1],[1,1,1,1],[1,0,0,1],[1,0,0,1]],
    "'": [[1],[1],[0],[0],[0]],
    ' ': [[0,0],[0,0],[0,0],[0,0],[0,0]],
}

def draw_text(p, text, ox, oy, color, size, scale=1):
    """Draw pixel-art text. Returns x position after last char."""
    x = ox
    for ch in text.upper():
        glyph = GLYPHS.get(ch, GLYPHS[' '])
        gw = len(glyph[0])
        gh = len(glyph)
        for gy in range(gh):
            for gx in range(gw):
                if glyph[gy][gx]:
                    for sy in range(scale):
                        for sx in range(scale):
                            set_pixel(p, x + gx*scale + sx, oy + gy*scale + sy, color, size)
        x += (gw + 1) * scale
    return x

def text_width(text, scale=1):
    w = 0
    for ch in text.upper():
        glyph = GLYPHS.get(ch, GLYPHS[' '])
        w += (len(glyph[0]) + 1) * scale
    return w - scale

# ---------------------------------------------------------------------------
# Shared compass drawing routine
# ---------------------------------------------------------------------------
def draw_compass(p, cx, cy, face_r, total_size):
    """Draw a compass rose centered at (cx,cy) with given face radius."""

    # ---- parchment face ----
    for y in range(total_size):
        for x in range(total_size):
            dx, dy = x - cx, y - cy
            dist = math.sqrt(dx*dx + dy*dy)
            if dist < face_r:
                t = (dist / face_r) ** 0.6 * 0.18
                p[y][x] = blend(PA, PD, t)

    # ---- outer gold ring ----
    ring_w = max(5, face_r // 8)
    for y in range(total_size):
        for x in range(total_size):
            dx, dy = x - cx, y - cy
            dist = math.sqrt(dx*dx + dy*dy)
            if face_r <= dist < face_r + ring_w:
                angle = math.atan2(dy, dx)
                shine = (math.cos(angle - math.pi * 0.75) + 1) / 2
                p[y][x] = blend(GD, GDL, shine * 0.55)
            elif face_r + ring_w <= dist < face_r + ring_w + 2:
                p[y][x] = DK

    # ---- tick marks ----
    for deg in range(0, 360, 5):
        rad = math.radians(deg - 90)
        if deg % 45 == 0:
            r_in, col = face_r - max(9, face_r // 6), DK
        elif deg % 15 == 0:
            r_in, col = face_r - max(6, face_r // 8), MD
        else:
            r_in, col = face_r - max(4, face_r // 10), blend(MD, PA, 0.5)
        r_out = face_r - 2
        for r in range(int(r_in), int(r_out) + 1):
            tx = int(cx + r * math.cos(rad))
            ty = int(cy + r * math.sin(rad))
            set_pixel(p, tx, ty, col, total_size)

    # ---- compass star lines (8 directions) ----
    star_r = face_r - max(14, face_r // 5)
    for angle in range(0, 360, 45):
        rad = math.radians(angle - 90)
        col = ND_ if angle % 90 == 0 else MD
        for r in range(max(10, face_r // 9), int(star_r) + 1):
            tx = int(cx + r * math.cos(rad))
            ty = int(cy + r * math.sin(rad))
            set_pixel(p, tx, ty, col, total_size)

    # ---- needle drawing ----
    def draw_needle(angle_deg, fwd, back, max_w, col_front, col_back):
        rad      = math.radians(angle_deg - 90)
        perp_rad = rad + math.pi / 2
        total    = fwd + back
        for i in range(total + 1):
            pos = i - back
            pcx = cx + pos * math.cos(rad)
            pcy = cy + pos * math.sin(rad)
            t   = i / total
            raw_w = max_w * (1 - abs(t * 2 - 1))   # tent 0→max→0
            w   = int(round(raw_w))
            col = col_front if pos >= 0 else col_back
            for dw in range(-w, w + 1):
                fx = int(round(pcx + dw * math.cos(perp_rad)))
                fy = int(round(pcy + dw * math.sin(perp_rad)))
                set_pixel(p, fx, fy, col, total_size)

    nlen  = int(face_r * 0.72)
    nback = max(5, nlen // 6)
    nw    = max(4, face_r // 12)

    draw_needle(180, nlen,         nback,     nw,     ND_,  NDL)   # S
    draw_needle(90,  nlen - nw*3,  nback - 1, nw - 1, ND_,  NDL)   # E
    draw_needle(270, nlen - nw*3,  nback - 1, nw - 1, ND_,  NDL)   # W
    draw_needle(0,   nlen,         nback,     nw,     RD,   RDL)   # N (on top)

    # ---- N/S/E/W labels ----
    lbl_r = face_r - max(18, face_r // 4)
    scale = max(1, face_r // 28)
    gw    = (4 + 1) * scale
    gh    = 5 * scale
    draw_text(p, 'N', cx - gw//2,     cy - lbl_r - gh//2, RD,  total_size, scale)
    draw_text(p, 'S', cx - gw//2,     cy + lbl_r - gh//2, ND_, total_size, scale)
    draw_text(p, 'E', cx + lbl_r - gw//2, cy - gh//2,     ND_, total_size, scale)
    draw_text(p, 'W', cx - lbl_r - gw//2 - scale, cy - gh//2, ND_, total_size, scale)

    # ---- center medallion ----
    med_r = max(7, face_r // 9)
    for y in range(total_size):
        for x in range(total_size):
            dx, dy = x - cx, y - cy
            dist = math.sqrt(dx*dx + dy*dy)
            if dist < med_r - 2:
                p[y][x] = blend(GDL, GD, (dist / (med_r - 2)) * 0.4)
            elif dist < med_r:
                p[y][x] = DK

# ---------------------------------------------------------------------------
# Logo 128×128
# ---------------------------------------------------------------------------
def make_logo():
    S = 128
    p = [[BG]*S for _ in range(S)]
    cx, cy = S//2, S//2

    # Navy background with faint grid
    for y in range(S):
        for x in range(S):
            noise = ((x * 17 + y * 31) % 9) - 4
            bg = (max(0, BG[0]+noise//3), max(0, BG[1]+noise//3), min(255, BG[2]+noise//2))
            p[y][x] = bg
            if x % 16 == 0 or y % 16 == 0:
                p[y][x] = blend(bg, BGL, 0.4)

    draw_compass(p, cx, cy, face_r=50, total_size=S)
    return p

# ---------------------------------------------------------------------------
# Thumbnail 480×270
# ---------------------------------------------------------------------------
def make_thumbnail():
    W, H = 480, 270
    p = [[BG]*W for _ in range(H)]

    # Background: dark navy with subtle map grid
    for y in range(H):
        for x in range(W):
            noise = ((x * 19 + y * 37) % 11) - 5
            bg = (max(0, BG[0]+noise//3), max(0, BG[1]+noise//3), min(255, BG[2]+noise//2))
            p[y][x] = bg
            if x % 32 == 0 or y % 32 == 0:
                p[y][x] = blend(bg, BGL, 0.35)

    # Horizontal gold separator lines (top and bottom decorative)
    for x in range(W):
        for dy in range(3):
            p[12 + dy][x] = blend(GD, BG, 0.4 + 0.3*(dy==1))
            p[H-13 + dy][x] = blend(GD, BG, 0.4 + 0.3*(dy==1))

    # Large compass on the left (set_pixel bounds-checks y via len(p))
    draw_compass(p, 135, H//2, face_r=95, total_size=W)

    # Title text: "Aeronaut's Compass"
    line1 = "AERONAUT'S"
    line2 = "COMPASS"
    tx_scale = 3
    tw1 = text_width(line1, tx_scale)
    tw2 = text_width(line2, tx_scale)
    text_cx = 320  # center x of text area
    draw_text(p, line1, text_cx - tw1//2, H//2 - 40, GDL, W, tx_scale)
    draw_text(p, line2, text_cx - tw2//2, H//2 - 10, PA,  W, tx_scale)

    # Subtitle
    sub = "NO LODESTONE REQUIRED"
    sub_scale = 1
    sw = text_width(sub, sub_scale)
    draw_text(p, sub, text_cx - sw//2, H//2 + 30, MD, W, sub_scale)

    # Horizontal rule under title
    rule_y = H//2 + 22
    for x in range(text_cx - 110, text_cx + 110):
        if 0 <= x < W:
            p[rule_y][x] = GD

    # Platform tags bottom-right
    tag_y = H - 32
    draw_text(p, "NEOFORGE  1.21.1", W - 180, tag_y, blend(MD, PA, 0.4), W, 1)

    return p

# ---------------------------------------------------------------------------
# Write
# ---------------------------------------------------------------------------
LOGO_PATH  = r"src\main\resources\aeronautscompass.png"
THUMB_PATH = r"media\thumbnail.png"

os.makedirs("media", exist_ok=True)
write_png(make_logo(),      128, 128, LOGO_PATH)
write_png(make_thumbnail(), 480, 270, THUMB_PATH)
print(f"Logo    -> {LOGO_PATH}")
print(f"Thumb   -> {THUMB_PATH}")
