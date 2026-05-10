"""Generate Navigator's Table block and GUI textures."""
import struct, zlib, os

def write_png_rgba(pixels, width, height, path):
    """Write RGBA PNG (color type 6, 4 bytes per pixel)."""
    def chunk(tag, data):
        buf = tag + data
        return struct.pack('>I', len(data)) + buf + struct.pack('>I', zlib.crc32(buf) & 0xffffffff)
    raw = b''
    for row in pixels:
        raw += b'\x00'
        for r, g, b, a in row:
            raw += bytes([r, g, b, a])
    sig = b'\x89PNG\r\n\x1a\n'
    ihdr = chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    idat = chunk(b'IDAT', zlib.compress(raw, 9))
    iend = chunk(b'IEND', b'')
    with open(path, 'wb') as f:
        f.write(sig + ihdr + idat + iend)

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
    with open(path, 'wb') as f:
        f.write(sig + ihdr + idat + iend)

# --- Colors ---
PA = (232, 213, 163)  # parchment
PG = (205, 184, 135)  # parchment grid
DK = (70, 45, 20)     # dark ink
RD = (185, 52, 32)    # red N needle
ND = (108, 75, 40)    # S/E/W needle
MD = (155, 115, 65)   # diagonal marks
WD = (165, 125, 70)   # wood medium
WL = (195, 158, 100)  # wood light
WN = (118, 85, 40)    # wood dark

# --- Block top (16x16): parchment with compass rose ---
def make_top():
    p = [[PA]*16 for _ in range(16)]
    for i in range(16):
        p[0][i] = DK; p[15][i] = DK
        p[i][0] = DK; p[i][15] = DK
    # faint grid at cols/rows 4, 11
    for c in range(1, 15):
        if p[4][c] == PA:  p[4][c]  = PG
        if p[11][c] == PA: p[11][c] = PG
    for r in range(1, 15):
        if p[r][4] == PA:  p[r][4]  = PG
        if p[r][11] == PA: p[r][11] = PG
    cx, cy = 7, 7
    # N needle (red)
    for r in range(1, cy): p[r][cx] = RD
    p[2][cx-1] = RD; p[2][cx+1] = RD  # arrowhead
    # S needle
    for r in range(cy+1, 15): p[r][cx] = ND
    p[13][cx-1] = ND; p[13][cx+1] = ND
    # W/E needles
    for c in range(1, cx): p[cy][c] = ND
    for c in range(cx+1, 15): p[cy][c] = ND
    # diagonal marks
    p[cy-2][cx-2] = MD; p[cy-2][cx+2] = MD
    p[cy+2][cx-2] = MD; p[cy+2][cx+2] = MD
    # center dot
    p[cy][cx] = DK
    return p

# --- Block side (16x16): oak planks + compass arrow ---
def make_side():
    p = [[WD]*16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if y == 0 or y == 15:       p[y][x] = DK
            elif x == 0 or x == 15:     p[y][x] = WN
            elif y % 4 == 0:            p[y][x] = WN
            elif (x + y*2) % 7 == 0:   p[y][x] = WL
            elif (x*3 + y) % 13 == 5:  p[y][x] = WN
    # small N arrow in center
    cx = 7
    for r in range(4, 11): p[r][cx] = DK
    p[3][cx-1] = DK; p[3][cx] = DK; p[3][cx+1] = DK
    return p

# --- Block bottom (16x16): plain oak planks ---
def make_bottom():
    p = [[WD]*16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            if y % 4 == 0:                      p[y][x] = WN
            elif x % 8 == ((y//4)*4) % 8:       p[y][x] = WN
            elif (x + y) % 9 == 2:              p[y][x] = WL
    return p

# --- GUI texture (256x256) ---
# Slots: compass at (7,25); player inv at (7+col*18, 83/101/119/141)
def make_gui():
    W, H = 256, 256
    # fill with parchment
    p = [[PA]*W for _ in range(H)]
    # ---- inventory section (y=76..166): darker wood-parchment ----
    IV = (195, 170, 115)   # inventory bg
    for y in range(76, 166):
        for x in range(176):
            p[y][x] = IV
    # separator line at y=74,75
    for x in range(176):
        p[74][x] = DK
        p[75][x] = (120, 90, 48)
    # outer border of entire container
    for x in range(176):
        p[0][x] = DK
        p[165][x] = DK
    for y in range(166):
        p[y][0] = DK
        p[y][175] = DK

    def draw_slot(sx, sy, bg_int, dark=(90,60,25), light=(255,240,195)):
        for i in range(18):
            p[sy][sx+i]   = dark
            p[sy+i][sx]   = dark
            p[sy+17][sx+i] = light
            p[sy+i][sx+17] = light
        for dy in range(1, 17):
            for dx in range(1, 17):
                p[sy+dy][sx+dx] = bg_int

    # compass slot at (7,25); map slot at (27,25)
    draw_slot(7,  25, (188, 162, 108))
    draw_slot(27, 25, (188, 162, 108))
    # player inventory slots
    for row in range(3):
        for col in range(9):
            draw_slot(7+col*18, 83+row*18, (152, 118, 62))
    # hotbar
    for col in range(9):
        draw_slot(7+col*18, 141, (152, 118, 62))
    # hotbar separator
    for x in range(1, 175):
        p[138][x] = (100, 72, 32)
        p[139][x] = (140, 108, 55)

    return p

# --- Slot hint icons (16×16 RGBA, ~40% opaque, greyscale) ---
# Pixel art modelled after the vanilla MC compass and filled_map item textures.
def make_hint_compass():
    A = 105  # ~41% opacity
    T  = (  0,   0,   0,   0)   # transparent corner
    def g(v): return (v, v, v, A)
    FR = g( 52)   # outer frame ring
    HL = g( 78)   # inner frame highlight
    BG = g(218)   # face background
    NB = g( 92)   # N-needle body (desaturated red → dark grey)
    NA = g( 72)   # N-needle arrowhead tip
    SN = g(195)   # S-needle body (was white/grey → lighter grey)
    SA = g(210)   # S-needle tip
    CT = g( 45)   # centre pivot
    return [
        [T,  T,  T,  T,  FR, FR, FR, FR, FR, FR, FR, FR, T,  T,  T,  T ],
        [T,  T,  T,  FR, HL, HL, BG, BG, BG, BG, HL, HL, FR, T,  T,  T ],
        [T,  T,  FR, HL, BG, BG, BG, NA, NA, BG, BG, BG, HL, FR, T,  T ],
        [T,  FR, HL, BG, BG, BG, NB, NA, NA, NB, BG, BG, BG, HL, FR, T ],
        [FR, HL, BG, BG, BG, BG, NB, NB, NB, NB, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, NB, NB, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, NB, NB, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, CT, CT, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, CT, CT, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, SN, SN, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, SN, SN, BG, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, SN, SN, SN, SN, BG, BG, BG, BG, HL, FR],
        [FR, HL, BG, BG, BG, BG, BG, SA, SA, BG, BG, BG, BG, BG, HL, FR],
        [T,  FR, HL, BG, BG, BG, BG, SA, SA, BG, BG, BG, BG, HL, FR, T ],
        [T,  T,  FR, HL, HL, BG, BG, SA, SA, BG, HL, HL, FR, T,  T,  T ],
        [T,  T,  T,  T,  FR, FR, FR, FR, FR, FR, FR, FR, T,  T,  T,  T ],
    ]

def make_hint_map():
    A = 105
    def g(v): return (v, v, v, A)
    BO = g( 55)   # outer border
    BI = g( 78)   # inner border / margin
    BG = g(210)   # paper background
    LA = g(185)   # land feature
    WA = g(150)   # water feature
    MK = g( 65)   # location marker cross
    return [
        [BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO],
        [BO, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BO],
        [BO, BI, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BI, BO],
        [BO, BI, BG, LA, LA, LA, LA, WA, WA, WA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, LA, LA, WA, WA, WA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, MK, LA, WA, WA, WA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, MK, MK, MK, BG, BG, BG, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, MK, LA, BG, BG, BG, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, LA, LA, LA, LA, LA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, LA, LA, LA, LA, LA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, WA, WA, WA, WA, LA, LA, LA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, WA, WA, WA, WA, LA, LA, LA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, LA, LA, LA, LA, LA, LA, LA, LA, LA, LA, BG, BI, BO],
        [BO, BI, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BI, BO],
        [BO, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BI, BO],
        [BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO, BO],
    ]

# --- Write files ---
base_block = r"src\main\resources\assets\mapcompass\textures\block"
base_gui   = r"src\main\resources\assets\mapcompass\textures\gui"
os.makedirs(base_block, exist_ok=True)
os.makedirs(base_gui,   exist_ok=True)

write_png(make_top(),    16,  16,  os.path.join(base_block, "navigator_table_top.png"))
write_png(make_side(),   16,  16,  os.path.join(base_block, "navigator_table_side.png"))
write_png(make_bottom(), 16,  16,  os.path.join(base_block, "navigator_table_bottom.png"))
write_png(make_gui(),    256, 256, os.path.join(base_gui,   "navigator_table.png"))
write_png_rgba(make_hint_compass(), 16, 16, os.path.join(base_gui, "slot_hint_compass.png"))
write_png_rgba(make_hint_map(),     16, 16, os.path.join(base_gui, "slot_hint_map.png"))
print("All textures written.")
