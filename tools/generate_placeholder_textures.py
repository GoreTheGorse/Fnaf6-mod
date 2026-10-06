import os
import struct
import zlib

OUT = "src/main/resources/assets/fnaf6/textures"


def write_png(path, pixels, size=16):
    raw = b"".join(
        b"\x00" + b"".join(bytes(pixels(x, y)) for x in range(size)) for y in range(size)
    )

    def chunk(tag, data):
        body = tag + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)

    png = (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", struct.pack(">IIBBBBB", size, size, 8, 6, 0, 0, 0))
        + chunk(b"IDAT", zlib.compress(raw))
        + chunk(b"IEND", b"")
    )
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)


def checker(x, y):
    return (20, 20, 20, 255) if ((x // 8) + (y // 8)) % 2 == 0 else (235, 235, 235, 255)


def wall(x, y):
    return (120, 60, 160, 255) if y % 8 != 0 else (80, 30, 120, 255)


def neon(x, y):
    edge = x in (0, 15) or y in (0, 15)
    return (255, 255, 255, 255) if edge else (255, 40, 170, 255)


def coin(x, y):
    d = (x - 7.5) ** 2 + (y - 7.5) ** 2
    if d > 36:
        return (0, 0, 0, 0)
    return (255, 215, 0, 255) if d > 20 else (230, 170, 0, 255)


def tablet(x, y):
    if x < 3 or x > 12 or y < 1 or y > 14:
        return (0, 0, 0, 0)
    return (40, 40, 40, 255) if (x in (3, 12) or y in (1, 14)) else (60, 200, 90, 255)


def terminal(x, y):
    if x in (0, 15) or y in (0, 15):
        return (30, 30, 40, 255)
    if 3 <= x <= 12 and 3 <= y <= 9:
        return (40, 230, 230, 255)
    return (60, 70, 90, 255)


def party_table(x, y):
    if x in (0, 15) or y in (0, 15):
        return (120, 70, 20, 255)
    return (255, 140, 40, 255) if (x + y) % 6 < 3 else (255, 215, 90, 255)


def arcade(x, y):
    if x in (0, 15) or y in (0, 15):
        return (15, 15, 40, 255)
    if 3 <= x <= 12 and 2 <= y <= 8:
        return (255, 60, 200, 255) if (x + y) % 2 == 0 else (60, 200, 255, 255)
    return (30, 40, 120, 255)


def prize(x, y):
    if x in (0, 15) or y in (0, 15):
        return (140, 100, 0, 255)
    return (255, 230, 60, 255) if (x * 3 + y * 5) % 7 == 0 else (240, 190, 30, 255)


write_png(f"{OUT}/block/checker_floor.png", checker)
write_png(f"{OUT}/block/pizzeria_wall.png", wall)
write_png(f"{OUT}/block/neon_sign.png", neon)
write_png(f"{OUT}/block/pizzeria_terminal.png", terminal)
write_png(f"{OUT}/block/party_table.png", party_table)
write_png(f"{OUT}/block/arcade_cabinet.png", arcade)
write_png(f"{OUT}/block/prize_counter.png", prize)
write_png(f"{OUT}/item/faz_coin.png", coin)
write_png(f"{OUT}/item/faz_tablet.png", tablet)
print("Placeholder textures written.")
