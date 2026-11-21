#!/usr/bin/env python3
"""Real-app walk before any owner QA (AGENTS §6.4, refactor F5).

Drives the exact candidate binaries the way the owner would, with real mouse clicks and key presses,
and saves a screenshot per step. Standard library only (ctypes on Windows, adb for Android).

Windows (the App de PC, always with its own data folder, never the owner's ~/.dnd_custom_aid):
  qa_real_app.py win-start --data D:\\qa\\datos [--exe "...\\DnD Custom Aid.exe"]
  qa_real_app.py win-shot out.png             screenshot of the App window
  qa_real_app.py win-click X Y                click at X,Y in the window (pixels of win-shot)
  qa_real_app.py win-type "texto"             type text into the focused field
  qa_real_app.py win-key enter|tab|esc|backspace
  qa_real_app.py win-size 1500 950            fixed window size, so steps repeat
  qa_real_app.py win-drag X1 Y1 X2 Y2         a real mouse drag in small steps
  qa_real_app.py win-scroll X Y CLICKS        mouse wheel (negative scrolls down)
  qa_real_app.py win-file "Título del selector" archivo.json   answer the App's file dialog
  qa_real_app.py win-stop --data D:\\qa\\datos   close it and confirm the owner's data was not touched

  With a test data folder the App shows «Seguir sin cuenta (datos de prueba)» and creates campaigns
  only on this computer, so the walk never needs the owner's account. Screenshots and the file
  dialog work with the App in the background; clicks and keys take the real mouse and keyboard
  (Windows ignores them otherwise) and stop rather than reach another window. Tell the owner
  before a walk: it takes the mouse for a few minutes.

Android emulator (always -read-only -no-snapshot-save, so nothing is kept):
  qa_real_app.py emu-start AVD_NAME
  qa_real_app.py emu-install file.apk         (the agent's emulator only; the owner never installs with adb)
  qa_real_app.py emu-open
  qa_real_app.py emu-tap X Y | emu-tap-text "Texto" [--index N] | emu-type "texto" | emu-key ENTER|BACK
  qa_real_app.py emu-shot out.png
  qa_real_app.py emu-stop
"""
from __future__ import annotations

import argparse
import json
import os
import struct
import subprocess
import sys
import time
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_EXE = ROOT / "desktopApp/build/compose/binaries/main/app/DnD Custom Aid/DnD Custom Aid.exe"
OWNER_DATA = Path.home() / ".dnd_custom_aid"
TITLE = "D&D Custom Aid — DATOS DE PRUEBA"
STATE = "qa_real_app_state.json"
ANDROID_PACKAGE = "io.github.mrsimkin.dndcustomaid"


# ---------------------------------------------------------------- PNG (no Pillow)
def write_png(path: Path, width: int, height: int, bgra: bytes) -> None:
    rows = bytearray()
    stride = width * 4
    for y in range(height):
        rows.append(0)
        line = bgra[y * stride:(y + 1) * stride]
        for x in range(0, stride, 4):
            rows += bytes((line[x + 2], line[x + 1], line[x]))
    def chunk(tag: bytes, data: bytes) -> bytes:
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(rows), 6)) + chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(png)


# ---------------------------------------------------------------- Windows
def _win():
    import ctypes
    from ctypes import wintypes
    user32 = ctypes.WinDLL("user32", use_last_error=True)
    gdi32 = ctypes.WinDLL("gdi32")
    try:
        ctypes.WinDLL("shcore").SetProcessDpiAwareness(2)
    except OSError:
        user32.SetProcessDPIAware()
    return ctypes, wintypes, user32, gdi32


def find_window(title: str = TITLE) -> int:
    ctypes, wintypes, user32, _ = _win()
    found = []
    proc = ctypes.WINFUNCTYPE(wintypes.BOOL, wintypes.HWND, wintypes.LPARAM)

    def each(hwnd, _):
        n = user32.GetWindowTextLengthW(hwnd)
        if n and user32.IsWindowVisible(hwnd):
            buf = ctypes.create_unicode_buffer(n + 1)
            user32.GetWindowTextW(hwnd, buf, n + 1)
            if buf.value == title:
                found.append(hwnd)
        return True
    user32.EnumWindows(proc(each), 0)
    return found[0] if found else 0


def client_origin(hwnd: int):
    ctypes, wintypes, user32, _ = _win()
    rect = wintypes.RECT()
    user32.GetClientRect(hwnd, ctypes.byref(rect))
    pt = wintypes.POINT(0, 0)
    user32.ClientToScreen(hwnd, ctypes.byref(pt))
    return pt.x, pt.y, rect.right, rect.bottom


def require_window() -> int:
    hwnd = find_window()
    if not hwnd:
        raise SystemExit(f"No está abierta la ventana «{TITLE}». Usa win-start.")
    return hwnd


# Screenshots and the file dialog work with the App in the background. Clicks and keys need the
# real mouse and keyboard (Windows ignores them otherwise), so they are guarded: they stop instead
# of reaching any other window.

def win_shot(out: Path, title: str | None = None) -> None:
    ctypes, wintypes, user32, gdi32 = _win()
    hwnd = find_window(title) if title else require_window()
    if not hwnd:
        raise SystemExit(f"No veo la ventana «{title}».")
    _, _, w, h = client_origin(hwnd)
    screen = user32.GetDC(0)
    mem = gdi32.CreateCompatibleDC(screen)
    bmp = gdi32.CreateCompatibleBitmap(screen, w, h)
    gdi32.SelectObject(mem, bmp)
    user32.PrintWindow(hwnd, mem, 0x1 | 0x2)  # PW_CLIENTONLY | PW_RENDERFULLCONTENT

    class BITMAPINFOHEADER(ctypes.Structure):
        _fields_ = [("biSize", wintypes.DWORD), ("biWidth", wintypes.LONG), ("biHeight", wintypes.LONG),
                    ("biPlanes", wintypes.WORD), ("biBitCount", wintypes.WORD), ("biCompression", wintypes.DWORD),
                    ("biSizeImage", wintypes.DWORD), ("biXPelsPerMeter", wintypes.LONG), ("biYPelsPerMeter", wintypes.LONG),
                    ("biClrUsed", wintypes.DWORD), ("biClrImportant", wintypes.DWORD)]
    info = BITMAPINFOHEADER(ctypes.sizeof(BITMAPINFOHEADER), w, -h, 1, 32, 0, 0, 0, 0, 0, 0)
    buf = ctypes.create_string_buffer(w * h * 4)
    gdi32.GetDIBits(mem, bmp, 0, h, buf, ctypes.byref(info), 0)
    gdi32.DeleteObject(bmp)
    gdi32.DeleteDC(mem)
    user32.ReleaseDC(0, screen)
    write_png(out, w, h, buf.raw)
    print(f"captura {out} ({w}x{h})")


def win_size(width: int, height: int) -> None:
    hwnd = require_window()
    _, _, user32, _ = _win()
    user32.SetWindowPos(hwnd, 0, 20, 20, width, height, 0x0010 | 0x0004)  # SWP_NOACTIVATE | SWP_NOZORDER
    time.sleep(1.0)
    print(f"ventana de {width}x{height}")


def focus(hwnd: int) -> None:
    """Brings the App to the front, or stops: input never goes to another window."""
    _, _, user32, _ = _win()
    if user32.IsIconic(hwnd):
        user32.ShowWindow(hwnd, 9)  # SW_RESTORE
    activated = False
    for _ in range(3):
        if user32.GetForegroundWindow() == hwnd:
            break
        # Windows lets a process take the foreground right after input. A Shift tap counts as input and,
        # unlike Alt, does not arm the window menu (which swallowed the next click).
        user32.keybd_event(0x10, 0, 0, 0)
        user32.keybd_event(0x10, 0, 0x0002, 0)
        user32.SetForegroundWindow(hwnd)
        activated = True
        time.sleep(0.4)
    if user32.GetForegroundWindow() != hwnd:
        raise SystemExit("No pude traer la App al frente (¿alguien usa el equipo?); no hago clic ni escribo.")
    # A window that has just come to the front needs a moment before it takes clicks reliably.
    time.sleep(1.0 if activated else 0.2)


def win_click(x: int, y: int) -> None:
    """A real left click. Windows does not deliver clicks to a window in the background, so the
    walk takes the mouse while it runs; it stops instead of clicking anything else."""
    ctypes, wintypes, user32, _ = _win()
    hwnd = require_window()
    focus(hwnd)
    ox, oy, _, _ = client_origin(hwnd)
    user32.WindowFromPoint.argtypes = [wintypes.POINT]
    if user32.GetAncestor(user32.WindowFromPoint(wintypes.POINT(ox + x, oy + y)), 2) != hwnd:  # GA_ROOT
        raise SystemExit(f"En {x},{y} hay otra ventana encima de la App; no hago clic.")
    # Arrive like a hand: a few pixels away first, then onto the target, so the App sees the pointer
    # enter before the press (some controls ignore a press that comes with no move).
    user32.SetCursorPos(ox + x - 6, oy + y - 4)
    time.sleep(0.12)
    user32.SetCursorPos(ox + x, oy + y)
    time.sleep(0.25)
    user32.mouse_event(0x0002, 0, 0, 0, 0)  # left down
    time.sleep(0.08)
    user32.mouse_event(0x0004, 0, 0, 0, 0)  # left up
    time.sleep(0.7)
    print(f"clic en {x},{y}")


def win_drag(x1: int, y1: int, x2: int, y2: int, steps: int = 20) -> None:
    """A real mouse drag from x1,y1 to x2,y2 in small steps, as a hand would do it."""
    ctypes, wintypes, user32, _ = _win()
    hwnd = require_window()
    focus(hwnd)
    ox, oy, _, _ = client_origin(hwnd)
    user32.WindowFromPoint.argtypes = [wintypes.POINT]
    for x, y in ((x1, y1), (x2, y2)):
        if user32.GetAncestor(user32.WindowFromPoint(wintypes.POINT(ox + x, oy + y)), 2) != hwnd:
            raise SystemExit(f"En {x},{y} hay otra ventana encima de la App; no arrastro.")
    user32.SetCursorPos(ox + x1, oy + y1)
    time.sleep(0.15)
    user32.mouse_event(0x0002, 0, 0, 0, 0)  # left down
    time.sleep(0.15)
    for i in range(1, steps + 1):
        user32.SetCursorPos(ox + x1 + (x2 - x1) * i // steps, oy + y1 + (y2 - y1) * i // steps)
        time.sleep(0.03)
    time.sleep(0.15)
    user32.mouse_event(0x0004, 0, 0, 0, 0)  # left up
    time.sleep(0.7)
    print(f"arrastre de {x1},{y1} a {x2},{y2}")


def win_scroll(x: int, y: int, clicks: int) -> None:
    """Mouse wheel at x,y: negative clicks scroll down."""
    ctypes, wintypes, user32, _ = _win()
    hwnd = require_window()
    focus(hwnd)
    ox, oy, _, _ = client_origin(hwnd)
    user32.SetCursorPos(ox + x, oy + y)
    time.sleep(0.1)
    user32.mouse_event(0x0800, 0, 0, clicks * 120, 0)  # MOUSEEVENTF_WHEEL
    time.sleep(0.6)
    print(f"rueda {clicks} en {x},{y}")


VK = {"enter": 0x0D, "tab": 0x09, "esc": 0x1B, "backspace": 0x08, "down": 0x28, "up": 0x26, "pagedown": 0x22, "pageup": 0x21}


def win_key(name: str) -> None:
    _, _, user32, _ = _win()
    focus(require_window())
    parts = name.lower().split("+")
    mods = {"ctrl": 0x11, "shift": 0x10, "alt": 0x12}
    held = [mods[p] for p in parts[:-1]]
    last = parts[-1]
    vk = VK.get(last, ord(last.upper()) if len(last) == 1 else None)
    if vk is None:
        raise SystemExit(f"Tecla desconocida: {name}")
    for m in held:
        user32.keybd_event(m, 0, 0, 0)
    user32.keybd_event(vk, 0, 0, 0)
    user32.keybd_event(vk, 0, 0x0002, 0)
    for m in reversed(held):
        user32.keybd_event(m, 0, 0x0002, 0)
    time.sleep(0.5)
    print(f"tecla {name}")


def win_type(text: str) -> None:
    ctypes, wintypes, user32, _ = _win()
    hwnd = require_window()

    class KEYBDINPUT(ctypes.Structure):
        _fields_ = [("wVk", wintypes.WORD), ("wScan", wintypes.WORD), ("dwFlags", wintypes.DWORD),
                    ("time", wintypes.DWORD), ("dwExtraInfo", ctypes.c_size_t)]

    class INPUT(ctypes.Structure):
        class _U(ctypes.Union):
            _fields_ = [("ki", KEYBDINPUT), ("pad", ctypes.c_byte * 32)]
        _anonymous_ = ("u",)
        _fields_ = [("type", wintypes.DWORD), ("u", _U)]

    for ch in text:
        focus(hwnd)  # checked before every letter: if the owner switches window, typing stops
        for flags in (0x0004, 0x0004 | 0x0002):  # KEYEVENTF_UNICODE, then key up
            inp = INPUT(type=1)
            inp.ki = KEYBDINPUT(0, ord(ch), flags, 0, 0)
            user32.SendInput(1, ctypes.byref(inp), ctypes.sizeof(INPUT))
            time.sleep(0.02)
        time.sleep(0.06)  # a person's pace: faster letters can arrive before Compose takes the last one
    time.sleep(0.4)
    print(f"escrito {text!r}")


def win_file(title: str, path: Path) -> None:
    """Answers the App's Windows file dialog (titled `title`) with `path`, without the keyboard."""
    ctypes, wintypes, user32, _ = _win()
    dialog = 0
    for _ in range(20):
        dialog = find_window(title)
        if dialog:
            break
        time.sleep(0.5)
    if not dialog:
        raise SystemExit(f"No apareció el selector «{title}».")
    edit = 0
    combo = user32.FindWindowExW(dialog, 0, "ComboBoxEx32", None)
    if combo:
        inner = user32.FindWindowExW(combo, 0, "ComboBox", None)
        edit = user32.FindWindowExW(inner, 0, "Edit", None) if inner else 0
    if not edit:
        edit = user32.FindWindowExW(dialog, 0, "Edit", None)
    if not edit:
        raise SystemExit("No encontré el campo «Nombre» del selector.")
    user32.SendMessageW.argtypes = [wintypes.HWND, wintypes.UINT, wintypes.WPARAM, wintypes.LPCWSTR]
    user32.SendMessageW(edit, 0x000C, 0, str(path.resolve()))  # WM_SETTEXT
    user32.PostMessageW(dialog, 0x0111, 1, 0)                  # WM_COMMAND IDOK
    for _ in range(20):
        if not find_window(title):
            break
        time.sleep(0.5)
    time.sleep(1.5)
    print(f"elegido {path}")


def owner_data_stamp() -> dict:
    stamp = {}
    if OWNER_DATA.is_dir():
        for p in sorted(OWNER_DATA.rglob("*")):
            if p.is_file():
                st = p.stat()
                stamp[str(p.relative_to(OWNER_DATA))] = [st.st_size, st.st_mtime_ns]
    return stamp


def guard_data_dir(data: Path) -> Path:
    data = data.resolve()
    if data == OWNER_DATA.resolve() or OWNER_DATA.resolve() in data.parents:
        raise SystemExit("Esa es la carpeta de datos real del propietario. Usa una carpeta de prueba.")
    data.mkdir(parents=True, exist_ok=True)
    return data


def win_start(data: Path, exe: Path, wait: int) -> None:
    data = guard_data_dir(data)
    if find_window():
        raise SystemExit("Ya hay una App de prueba abierta. Ciérrala con win-stop.")
    if not exe.is_file():
        raise SystemExit(f"No existe {exe}. Genera la distribución con gradle :desktopApp:createDistributable.")
    env = dict(os.environ, DND_CUSTOM_AID_DATA_DIR=str(data))
    stamp = owner_data_stamp()
    proc = subprocess.Popen([str(exe)], env=env, cwd=str(exe.parent), stdin=subprocess.DEVNULL,
                            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    (data / STATE).write_text(json.dumps({"pid": proc.pid, "owner": stamp}), encoding="utf-8")
    deadline = time.time() + wait
    while time.time() < deadline:
        if find_window():
            time.sleep(2)
            print(f"App abierta (pid {proc.pid}) con datos en {data}")
            return
        if proc.poll() is not None:
            raise SystemExit(f"La App se cerró al arrancar (código {proc.returncode}).")
        time.sleep(1)
    raise SystemExit("La ventana no apareció a tiempo.")


def win_stop(data: Path) -> None:
    data = guard_data_dir(data)
    state = json.loads((data / STATE).read_text(encoding="utf-8"))
    hwnd = find_window()
    if hwnd:
        _, _, user32, _ = _win()
        user32.PostMessageW(hwnd, 0x0010, 0, 0)  # WM_CLOSE
        for _ in range(20):
            if not find_window():
                break
            time.sleep(0.5)
    subprocess.run(["taskkill", "/PID", str(state["pid"]), "/T", "/F"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    db = data / "dnd_custom_aid.db"
    print(f"Base de prueba: {db} ({'existe' if db.is_file() else 'NO EXISTE'})")
    touched = [k for k, v in owner_data_stamp().items() if state["owner"].get(k) != v]
    if touched:
        raise SystemExit("ATENCIÓN: cambiaron archivos de la carpeta real del propietario: " + ", ".join(touched))
    print("La carpeta real del propietario no cambió.")


# ---------------------------------------------------------------- Android emulator
def sdk() -> Path:
    return Path(os.environ.get("ANDROID_SDK_ROOT") or os.environ.get("ANDROID_HOME") or Path(os.environ["LOCALAPPDATA"]) / "Android/Sdk")


def adb(*args: str, capture: bool = False):
    r = subprocess.run(["adb", *args], stdin=subprocess.DEVNULL, capture_output=True)
    if r.returncode != 0:
        raise SystemExit(f"adb {' '.join(args)} falló: {r.stderr.decode(errors='ignore').strip()}")
    return r.stdout if capture else None


def emu_start(avd: str, wait: int) -> None:
    exe = sdk() / "emulator/emulator.exe"
    subprocess.Popen([str(exe), "-avd", avd, "-read-only", "-no-snapshot-save", "-no-boot-anim", "-no-window"],
                     stdin=subprocess.DEVNULL, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    deadline = time.time() + wait
    while time.time() < deadline:
        r = subprocess.run(["adb", "shell", "getprop", "sys.boot_completed"], stdin=subprocess.DEVNULL, capture_output=True)
        if r.stdout.strip() == b"1":
            print(f"Emulador {avd} listo (solo lectura, sin guardar instantánea).")
            return
        time.sleep(3)
    raise SystemExit("El emulador no terminó de arrancar a tiempo.")


def emu_all():
    """Every on-screen element with text or a description: (centre x, centre y, label)."""
    import re
    import xml.etree.ElementTree as ET
    adb("shell", "uiautomator", "dump", "/sdcard/qa_ui.xml", capture=True)
    root = ET.fromstring(adb("exec-out", "cat", "/sdcard/qa_ui.xml", capture=True).decode("utf-8", errors="ignore"))
    out = []
    for node in root.iter("node"):
        label = ((node.get("text") or "") + " " + (node.get("content-desc") or "")).strip()
        if label:
            x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds", "")))
            if x2 > x1 and y2 > y1:
                out.append(((x1 + x2) // 2, (y1 + y2) // 2, label))
    return out


def emu_find(text: str, index: int = 0):
    """Centre of the index-th on-screen element whose text or description contains `text`."""
    hits = [h for h in emu_all() if text.lower() in h[2].lower()]
    if len(hits) <= index:
        raise SystemExit(f"No veo «{text}» en la pantalla ({len(hits)} coincidencias).")
    return hits[index]


def emu_shot(out: Path) -> None:
    png = adb("exec-out", "screencap", "-p", capture=True)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_bytes(png)
    print(f"captura {out}")


def main() -> int:
    sys.stdout.reconfigure(encoding="utf-8")
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="cmd", required=True)
    s = sub.add_parser("win-start"); s.add_argument("--data", type=Path, required=True); s.add_argument("--exe", type=Path, default=DEFAULT_EXE); s.add_argument("--wait", type=int, default=90)
    s = sub.add_parser("win-stop"); s.add_argument("--data", type=Path, required=True)
    s = sub.add_parser("win-shot"); s.add_argument("out", type=Path); s.add_argument("--title")
    s = sub.add_parser("win-click"); s.add_argument("x", type=int); s.add_argument("y", type=int)
    s = sub.add_parser("win-type"); s.add_argument("text")
    s = sub.add_parser("win-key"); s.add_argument("name")
    s = sub.add_parser("win-drag"); [s.add_argument(n, type=int) for n in ("x1", "y1", "x2", "y2")]
    s = sub.add_parser("win-scroll"); s.add_argument("x", type=int); s.add_argument("y", type=int); s.add_argument("clicks", type=int)
    s = sub.add_parser("win-file"); s.add_argument("title"); s.add_argument("path", type=Path)
    s = sub.add_parser("win-size"); s.add_argument("width", type=int); s.add_argument("height", type=int)
    s = sub.add_parser("emu-start"); s.add_argument("avd"); s.add_argument("--wait", type=int, default=240)
    s = sub.add_parser("emu-install"); s.add_argument("apk", type=Path)
    sub.add_parser("emu-open")
    s = sub.add_parser("emu-tap"); s.add_argument("x", type=int); s.add_argument("y", type=int)
    sub.add_parser("emu-texts")
    s = sub.add_parser("emu-tap-text"); s.add_argument("text"); s.add_argument("--index", type=int, default=0)
    s = sub.add_parser("emu-type"); s.add_argument("text")
    s = sub.add_parser("emu-key"); s.add_argument("name")
    s = sub.add_parser("emu-shot"); s.add_argument("out", type=Path)
    sub.add_parser("emu-stop")
    a = ap.parse_args()
    if a.cmd == "win-start": win_start(a.data, a.exe, a.wait)
    elif a.cmd == "win-stop": win_stop(a.data)
    elif a.cmd == "win-shot": win_shot(a.out, a.title)
    elif a.cmd == "win-click": win_click(a.x, a.y)
    elif a.cmd == "win-type": win_type(a.text)
    elif a.cmd == "win-key": win_key(a.name)
    elif a.cmd == "win-size": win_size(a.width, a.height)
    elif a.cmd == "win-file": win_file(a.title, a.path)
    elif a.cmd == "win-drag": win_drag(a.x1, a.y1, a.x2, a.y2)
    elif a.cmd == "win-scroll": win_scroll(a.x, a.y, a.clicks)
    elif a.cmd == "emu-start": emu_start(a.avd, a.wait)
    elif a.cmd == "emu-install": adb("install", "-r", str(a.apk)); print("instalada")
    elif a.cmd == "emu-open":
        adb("shell", "monkey", "-p", ANDROID_PACKAGE, "-c", "android.intent.category.LAUNCHER", "1")
        time.sleep(10)  # the first screen needs a few seconds before it answers taps
        print("App abierta")
    elif a.cmd == "emu-tap": adb("shell", "input", "tap", str(a.x), str(a.y)); time.sleep(1.5); print(f"toque en {a.x},{a.y}")
    elif a.cmd == "emu-texts":
        for x, y, label in emu_all():
            print(f"{x:5d},{y:5d}  {label}")
    elif a.cmd == "emu-tap-text":
        x, y, label = emu_find(a.text, a.index)
        adb("shell", "input", "tap", str(x), str(y)); time.sleep(1.5); print(f"toque en «{label}» ({x},{y})")
    elif a.cmd == "emu-type": adb("shell", "input", "text", a.text.replace(" ", "%s")); print("escrito")
    elif a.cmd == "emu-key": adb("shell", "input", "keyevent", "KEYCODE_" + a.name.upper()); print(f"tecla {a.name}")
    elif a.cmd == "emu-shot": emu_shot(a.out)
    elif a.cmd == "emu-stop": subprocess.run(["adb", "emu", "kill"], stdin=subprocess.DEVNULL, capture_output=True); print("emulador cerrado")
    return 0


if __name__ == "__main__":
    sys.exit(main())
